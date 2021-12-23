package xyz.msws.supergive.selectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to select a specific entity group.
 *
 * @author imodm
 */
public class AnnotatedSelector implements Selector {

    private final SuperGive plugin;

    public AnnotatedSelector(SuperGive plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<Entity> getEntities(String arg, CommandSender sender) {
        if (!arg.startsWith("@"))
            return null;
        String key = MSG.normalize(arg.substring(1));
        List<Entity> result = new ArrayList<>();
        switch (key) {
            case "world":
                if (!(sender instanceof Player))
                    return null;
                if (!sender.hasPermission("supergive.selector.annotated.world"))
                    return null;
                return ((Player) sender).getWorld().getEntities();
            case "worldplayers":
                if (!(sender instanceof Player))
                    return null;
                if (!sender.hasPermission("supergive.selector.annotated.worldplayers"))
                    return null;
                return ((Player) sender).getWorld().getEntities().stream().filter(e -> e.getType() == EntityType.PLAYER)
                        .collect(Collectors.toList());
            case "all":
                if (!sender.hasPermission("supergive.selector.annotated.all"))
                    return null;
                for (World w : Bukkit.getWorlds()) {
                    result.addAll(w.getEntities());
                }
                if (plugin.getConfig().getBoolean("AllSelectorWarning")) {
                    MSG.tell(sender, "&4&lSuperGive", MSG.BOLD + "&cThe &4@all &cSelector - &6Precaution");
                    MSG.tell(sender, "", "It is not recommended to use the &8@all&7 selector as it can");
                    MSG.tell(sender, "", "result in unintended consequences. &8@all &7targets ALL entities");
                    MSG.tell(sender, "", "such as horses, minecart chests, zombies, etc. in all worlds.");
                    MSG.tell(sender, "", "It is recommended to use &8@players &7or specify an entity type");
                    MSG.tell(sender, "", "such as &8@enderman&7. If you are aware of the ramifications");
                    MSG.tell(sender, "", "of this selector and want to hide this warning,");
                    MSG.tell(sender, "", "you can do so in the config by disabling the");
                    MSG.tell(sender, "", "AllSelectorWarning in the config.");
                }

                return result;
            case "everyone":
            case "players":
                if (!sender.hasPermission("supergive.selector.annotated.players"))
                    return null;
                return new ArrayList<>(Bukkit.getOnlinePlayers());
            case "me":
            case "self":
                if (!(sender instanceof Entity))
                    return null;
                if (!sender.hasPermission("supergive.selector.annotated.self"))
                    return null;
                return Collections.singletonList(((Entity) sender));
            default:
                if (key.startsWith("world")) {
                    // Sender can specify world-limited targets
                    if (!(sender instanceof Player))
                        return null;
                    World w = ((Player) sender).getWorld();
                    GameMode mode = Utils.getGameMode(key.substring("world".length()));
                    if (mode != null) {
                        if (!sender.hasPermission("supergive.selector.annotated.gamemode." + mode))
                            return null;
                        return w.getPlayers().stream().filter(p -> p.getGameMode() == mode).collect(Collectors.toList());
                    }
                    EntityType type = Utils.getEntityType(key.substring("world".length()));
                    if (type != null) {
                        if (!sender.hasPermission("supergive.selector.annotated.entitytype." + type))
                            return null;
                        return w.getEntities().stream().filter(e -> e.getType() == type).collect(Collectors.toList());
                    }
                }
                GameMode mode = Utils.getGameMode(key);
                if (mode != null) {
                    if (!sender.hasPermission("supergive.selector.annotated.gamemode." + mode))
                        return null;
                    return Bukkit.getOnlinePlayers().stream().filter(p -> p.getGameMode() == mode)
                            .collect(Collectors.toList());
                }
                EntityType type = Utils.getEntityType(key);
                if (type != null) {
                    if (!sender.hasPermission("supergive.selector.annotated.entitytype." + type))
                        return null;
                    for (World w : Bukkit.getWorlds())
                        result.addAll(
                                w.getEntities().stream().filter(e -> e.getType() == type).collect(Collectors.toList()));
                    return result;
                }
        }
        return null;
    }

    @Override
    public String getDescriptor(String arg, CommandSender sender) {
        switch (arg.substring(1).toLowerCase()) {
            case "all":
                return "all entities";
            case "world":
                return "current world";
            case "worldplayers":
                return "current players in world";
            case "players":
            case "everyone":
                return "all players";
            case "me":
            case "self":
                return "yourself";
        }
        GameMode mode = Utils.getGameMode(arg.substring(1));
        if (mode != null)
            return "players in " + mode.toString().toLowerCase();
        EntityType type = Utils.getEntityType(arg.substring(1));
        if (type != null)
            return MSG.camelCase(type.toString()).toLowerCase() + "s";
        return arg.substring(1);
    }

    @Override
    public List<String> tabComplete(String current) {
        List<String> result = new ArrayList<>();
        if (!current.startsWith("@"))
            return null;
        String prev = "";
        for (String s : new String[]{"players", "everyone", "me", "world", "worldplayers", "all", "survival",
                "creative", "adventure", "spectator", "worldsurvival", "worldcreative", "worldadventure",
                "worldspectator"}) {
            if (("@" + s).toLowerCase().startsWith(current.toLowerCase()))
                result.add(prev + "@" + s);
        }
        for (EntityType type : EntityType.values()) {
            String t = MSG.normalize(type.toString());
            if (t.startsWith(MSG.normalize(current))) {
                result.add("@" + t);
            }
        }
        return result;
    }

}
