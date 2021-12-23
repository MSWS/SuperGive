package xyz.msws.supergive.selectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Used to allow more lenient name searching.
 *
 * @author imodm
 */
public class NameSelector implements Selector {

    @Override
    public List<Entity> getEntities(String arg, CommandSender sender) {
        try {
            Bukkit.class.getMethod("selectEntities", CommandSender.class, String.class);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(arg))
                    return null; // Avoid clashing with VanillaSelector to prevent duplicate "that are"
            }
        } catch (NoSuchMethodError | NoSuchMethodException | SecurityException e) {
            // 1.8 Compatibility
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(arg))
                    return Collections.singletonList(p); // VanillaSelector doesn't work so we can add it
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(arg.toLowerCase()))
                return Collections.singletonList(p);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().contains(arg.toLowerCase()))
                return Collections.singletonList(p);
        }
        return null;
    }

    @Override
    public String getDescriptor(String arg, CommandSender sender) {
        return getEntities(arg, sender).get(0).getName();
    }

    @Override
    public List<String> tabComplete(String current) {
        List<String> result = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().contains(current.toLowerCase()))
                result.add(p.getName());
        }
        return result;
    }

}
