package xyz.msws.supergive;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.msws.supergive.items.ItemBuilder;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;
import xyz.msws.supergive.modules.AbstractModule;
import xyz.msws.supergive.modules.ModulePriority;
import xyz.msws.supergive.modules.commands.CommandModule;
import xyz.msws.supergive.selectors.NativeSelector;
import xyz.msws.supergive.selectors.Selector;
import xyz.msws.supergive.utils.Lang;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SuperGive extends JavaPlugin {

    private final Set<AbstractModule> modules = new HashSet<>();
    private YamlConfiguration lang;
    private static SuperGive instance;

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(Loadout.class, "Loadout");
    }

    @Override
    public void onEnable() {
        instance = this;

        prepareFiles();

        modules.add(new ItemBuilder(this));
        modules.add(new NativeSelector(this));
        modules.add(new CommandModule(this));
        modules.add(new LoadoutManager(this));

        enableModules();

        Lang.load(lang);
    }

    @Deprecated
    public static SuperGive getPlugin() {
        return instance;
    }

    /**
     * Ensure config and lang files exist
     */
    private void prepareFiles() {
        File conf = new File(this.getDataFolder(), "config.yml");
        if (!conf.exists())
            saveResource("config.yml", false);
        File langFile = new File(this.getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            try {
                // Lang file doesn't exist, create and assign all the lang values
                langFile.createNewFile();
                YamlConfiguration c = new YamlConfiguration();
                for (Lang l : Lang.values()) {
                    c.set(l.getKey(), l.getValue());
                }
                c.save(langFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lang = YamlConfiguration.loadConfiguration(langFile);
    }

    /**
     * Gets the language config, ideally should never have to be used. Use
     * {@link Lang} for future general access.
     *
     * @return
     */
    public YamlConfiguration getLang() {
        return lang;
    }

    /**
     * Gets the plugin's item builder, this should generally be unmodified unless
     * affected by third-party plugins.
     *
     * @return
     */
    public ItemBuilder getBuilder() {
//		return builder;
        return getModule(ItemBuilder.class);
    }

    /**
     * Gets the {@link NativeSelector} by default. This can also be overriden by
     * third-party plugins.
     *
     * @return
     */
    public Selector getSelector() {
        return getModule(NativeSelector.class);
    }

    @Override
    public void onDisable() {
        disableModules();
    }

    private void enableModules() {
        for (ModulePriority priority : ModulePriority.values()) {
            enableModules(priority);
        }
    }

    private void enableModules(ModulePriority priority) {
        for (AbstractModule mod : modules.stream().filter(mod -> mod.getPriority() == priority)
                .collect(Collectors.toSet())) {
            mod.initialize();
            mod.setEnabled(true);
        }
    }

    private void disableModules() {
        List<ModulePriority> reverse = Arrays.asList(ModulePriority.values());
        Collections.reverse(reverse);
        for (ModulePriority priority : reverse) {
            disableModules(priority);
        }
    }

    private void disableModules(ModulePriority priority) {
        for (AbstractModule mod : modules.stream().filter(mod -> mod.getPriority() == priority)
                .collect(Collectors.toSet())) {
            mod.disable();
            mod.setEnabled(false);
        }
    }

    public void enableModule(AbstractModule module) {
        module.initialize();
        module.setEnabled(true);
        modules.add(module);
    }

    public <T extends AbstractModule> T getModule(Class<T> mod) {
        for (AbstractModule m : getModules())
            if (mod.isAssignableFrom(m.getClass()))
                return mod.cast(m);
        return null;
    }

    public AbstractModule getModule(String string) {
        for (AbstractModule mod : getModules())
            if (mod.getId().equals(string))
                return mod;
        return null;
    }

    public Set<AbstractModule> getModules() {
        return modules;
    }

}
