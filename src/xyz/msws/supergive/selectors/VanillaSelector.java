package xyz.msws.supergive.selectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Allows for the vanilla selector.
 *
 * @author imodm
 */
public class VanillaSelector implements Selector {

    @Override
    public List<Entity> getEntities(String arg, CommandSender sender) {
        if (!sender.hasPermission("supergive.selector.vanilla"))
            return null;
        try {
            return Bukkit.selectEntities(sender, arg);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (NoSuchMethodError e) {
            return null;
        }
    }

    @Override
    public String getDescriptor(String arg, CommandSender sender) {
        switch (arg.toLowerCase()) {
            case "@a":
                return "all players";
            case "@p":
                return "the nearest player";
            case "@e":
                return "all entities";
            case "@r":
                return "a random player";
            case "@s":
                return "yourself";
        }
        Player player = Bukkit.getPlayer(arg);
        if (player != null)
            return player.getName();
        return arg;
    }

    @Override
    public List<String> tabComplete(String current) {
        if (!current.toLowerCase().startsWith("@")) {
            if ("@".startsWith(current.toLowerCase()))
                return Collections.singletonList("@");
        }
        List<String> result = new ArrayList<>();
        for (String s : new String[]{"a", "p", "e", "r", "s"}) {
            if (("@" + s).startsWith(current.toLowerCase()))
                result.add("@" + s);
        }
        return result;
    }

}
