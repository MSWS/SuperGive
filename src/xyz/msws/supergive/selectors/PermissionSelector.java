package xyz.msws.supergive.selectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to filter the current players by the fact that they have a permission.
 *
 * @author imodm
 */
public class PermissionSelector implements Selector {

    @Override
    public List<Entity> getEntities(String arg, CommandSender sender) {
        if (!sender.hasPermission("supergive.selector.permission"))
            return null;
        if (!arg.startsWith("perm:"))
            return null;
        return Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(arg.substring("perm:".length())))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescriptor(String arg, CommandSender sender) {
        return "permissioned players";
    }

    @Override
    public List<String> tabComplete(String current) {
        if (!current.toLowerCase().startsWith("perm:")) {
            if ("perm:".startsWith(current.toLowerCase()))
                return Arrays.asList("perm:");
            return null;
        }
        return null;
    }

}
