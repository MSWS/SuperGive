package xyz.msws.supergive.items;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;

import java.util.Collections;
import java.util.List;

/**
 * Adds support for specifying the display name for an itemstack.
 *
 * @author imodm
 */
public class NameAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("name:"))
            return item;
        String name = line.substring("name:".length());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MSG.color("&r" + name));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName())
            return null;
        return "name:" + meta.getDisplayName();
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;
        if (!current.toLowerCase().startsWith("name:")) {
            if ("name:".startsWith(current.toLowerCase()))
                return Collections.singletonList("name:");
            return null;
        }
        return null;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.name";
    }

    @Override
    public String humanReadable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName())
            return null;
        return "named " + meta.getDisplayName();
    }

}
