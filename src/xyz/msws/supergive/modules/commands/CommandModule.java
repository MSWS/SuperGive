package xyz.msws.supergive.modules.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.modules.AbstractModule;
import xyz.msws.supergive.modules.ModulePriority;
import xyz.msws.supergive.utils.MSG;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module that manages all commands, used to enable/disable specific commands.
 *
 * @author imodm
 */
@SuppressWarnings("JavaReflectionMemberAccess")
public class CommandModule extends AbstractModule implements Listener {

    private Map<Command, Boolean> commands;
    private CommandMap map;

    public CommandModule(SuperGive plugin) {
        super("CommandModule", plugin);
    }

    @Override
    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        commands = new HashMap<>();

        try {
            map = (CommandMap) Bukkit.class.getMethod("getCommandMap").invoke(null); // Paper
        } catch (NoSuchMethodError | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // Spigot
            try {
                final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                bukkitCommandMap.setAccessible(true);
                map = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
                MSG.error("Unable to get CommandMap");
                e.printStackTrace();
                return;
            }
        }

        MSG.log("&9Enabling commands...");

        commands.put(new GiveCommand(plugin), false);
        commands.put(new LoadoutCommand(plugin, "loadout"), false);
        commands.put(new GenerateCommand(plugin), false);

        List<Command> cmds = new ArrayList<>();
        for (Command cmd : commands.keySet()) {
            cmds.add(cmd);
            commands.put(cmd, true);
        }

        enableCommands(cmds);
        MSG.log(MSG.SUCCESS + "Successfully enabled " + MSG.SUBJECT + commands.size() + MSG.SUCCESS + " command"
                + (commands.size() == 1 ? "" : "s"));
    }

    public void enableCommands(List<Command> commands) {
        commands.forEach(this::enableCommand);
    }

    public void enableCommand(Command command) {
        map.register(plugin.getName(), command);
        commands.put(command, true);
        try {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.updateCommands();
            }
        } catch (NoSuchMethodError ignored) {

        }
    }

    public void disableCommands(List<Command> commands) {
        commands.forEach(this::disableCommand);
    }

    public void disableCommand(Command cmd) {

        getKnownCommands().entrySet().removeIf(c -> c.getValue().equals(cmd));

        commands.put(cmd, false);
        try {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.updateCommands();
            }
        } catch (NoSuchMethodError ignored) {

        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Command> getKnownCommands() {
        try {
            return (Map<String, Command>) map.getClass().getMethod("getKnownCommands").invoke(map);
        } catch (NoSuchMethodError | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            try {
                return (Map<String, Command>) getPrivateField(map, "knownCommands");
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (NoSuchFieldException e2) {
                // 1.16
                try {
                    return (Map<String, Command>) map.getClass()
                            .getMethod("getKnownCommands").invoke(map);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                        | NoSuchMethodException | SecurityException e1) {
                    e1.printStackTrace();
                }
                MSG.error("Could not get known commands");
            }
        }

        return null;
    }

    public Command getCommand(String command) {
        return commands.keySet().stream().filter(cmd -> cmd.getName().equals(command)).findFirst().orElse(null);
    }

    public boolean isEnabled(Command cmd) {
        return commands.getOrDefault(cmd, true);
    }

    public Map<Command, Boolean> getCommands() {
        return commands;
    }

    @Override
    public void disable() {
        disableCommands(new ArrayList<>(commands.keySet()));
    }

    /*
     * Code from "zeeveener" at
     * https://bukkit.org/threads/how-to-unregister-commands-from-your-plugin.
     * 131808/ , edited by RandomHashTags
     */
    private Object getPrivateField(Object object, String field)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = field
                .equals("commandMap")
                ? clazz.getDeclaredField(field)
                : field.equals("knownCommands")
                ? Bukkit.getVersion().contains("1.13") ? clazz.getSuperclass().getDeclaredField(field)
                : clazz.getDeclaredField(field)
                : null;
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    @Override
    public ModulePriority getPriority() {
        return ModulePriority.MEDIUM;
    }

}
