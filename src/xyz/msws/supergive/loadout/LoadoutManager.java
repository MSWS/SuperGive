package xyz.msws.supergive.loadout;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.events.LoadoutDeleteEvent;
import xyz.msws.supergive.events.LoadoutLoadEvent;
import xyz.msws.supergive.modules.AbstractModule;
import xyz.msws.supergive.modules.ModulePriority;
import xyz.msws.supergive.utils.MSG;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Managers and keeps track of {@link Loadout}.
 *
 * @author imodm
 */
public class LoadoutManager extends AbstractModule {

    private Map<String, Loadout> loads = new HashMap<>();

    public LoadoutManager(SuperGive plugin) {
        super(plugin);
        this.id = "LoadoutManager";
    }

    @Override
    public void initialize() {
        loads.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Loadouts");
        if (section == null)
            return;
        for (Entry<String, Object> values : section.getValues(false).entrySet()) {
            if (!(values.getValue() instanceof Loadout)) {
                MSG.warn("Invalid loadout specified for " + values.getKey());
                continue;
            }
            loads.put(values.getKey(), (Loadout) values.getValue());
        }
    }

    /**
     * Returns the loadout assigned with this key, null if none are
     *
     * @param key
     * @return The loadout or null
     */
    public Loadout getLoadout(String key) {
        return loads.getOrDefault(key, null);
    }

    public Loadout matchLoadout(String key) {
        for (Entry<String, Loadout> k : loads.entrySet()) {
            if (MSG.normalize(k.getKey()).equals(MSG.normalize(key)))
                return k.getValue();
        }
        for (Entry<String, Loadout> k : loads.entrySet()) {
            if (MSG.normalize(k.getKey()).startsWith(MSG.normalize(key)))
                return k.getValue();
        }
        for (Entry<String, Loadout> k : loads.entrySet()) {
            if (MSG.normalize(k.getKey()).contains(MSG.normalize(key)))
                return k.getValue();
        }
        return null;
    }

    public void addLoadout(String key, Loadout loadout) {
        LoadoutLoadEvent event = new LoadoutLoadEvent(loadout);
        Bukkit.getPluginManager().callEvent(event);
        loadout = event.getLoadout();
        loads.put(key, loadout);
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Loadouts");
        if (section == null) {
            plugin.getConfig().createSection("Loadouts");
            section = plugin.getConfig().getConfigurationSection("Loadouts");
        }
        section.set(key, loadout);
        plugin.saveConfig();
    }

    public boolean deleteLoadout(String key) {
        Loadout loadout = getLoadout(key);
        if (loadout == null)
            return false;
        LoadoutDeleteEvent delete = new LoadoutDeleteEvent(loadout);
        Bukkit.getPluginManager().callEvent(delete);
        if (delete.isCancelled())
            return false;
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("Loadouts");
        if (section == null)
            return false;
        section.set(key, null);
        loads.remove(key);
        plugin.saveConfig();
        return true;
    }

    public Set<String> getLoadoutNames() {
        return loads.keySet();
    }

    public Collection<Loadout> getLoadouts() {
        return loads.values();
    }

    public Map<String, Loadout> getLoadoutMap() {
        return loads;
    }

    @Override
    public void disable() {
        loads.clear();
    }

    @Override
    public ModulePriority getPriority() {
        return ModulePriority.MEDIUM;
    }

}
