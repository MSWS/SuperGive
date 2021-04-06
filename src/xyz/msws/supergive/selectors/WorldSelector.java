package xyz.msws.supergive.selectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Selects entities within a specific world.
 *
 * @author imodm
 */
public class WorldSelector implements Selector {

    @Override
    public List<Entity> getEntities(String arg, CommandSender sender) {
        if (!sender.hasPermission("supergive.selector.world"))
            return null;
        if (!arg.toLowerCase().startsWith("world:"))
            return null;
        String n = Utils.getOption(arg.substring("world:".length()),
                Bukkit.getWorlds().stream().map(w -> w.getName()).collect(Collectors.toList()));
        if (n == null)
            return null;
        World world = Bukkit.getWorld(n);
        if (!sender.hasPermission("supergive.selector.world." + world.getName()))
            return null;
        return world.getEntities();
    }

    @Override
    public String getDescriptor(String arg, CommandSender sender) {
        String n = Utils.getOption(arg.substring("world:".length()),
                Bukkit.getWorlds().stream().map(w -> w.getName()).collect(Collectors.toList()));
        return "entities in " + n;
    }

    @Override
    public List<String> tabComplete(String current) {
        if (!current.toLowerCase().startsWith("world:")) {
            if ("world".startsWith(current.toLowerCase()))
                return Arrays.asList("world:");
            return null;
        }
        List<String> result = new ArrayList<>();
        for (World w : Bukkit.getWorlds()) {
            if (("world:" + MSG.normalize(w.getName())).startsWith("world:" + MSG.normalize(current)))
                result.add("world:" + MSG.normalize(w.getName()));
        }
        return result;
    }

}
