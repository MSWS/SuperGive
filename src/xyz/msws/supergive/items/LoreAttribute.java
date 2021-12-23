package xyz.msws.supergive.items;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adds support for giving itemstacks lores.
 *
 * @author imodm
 */
public class LoreAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("lore:"))
            return item;
        List<String> lore = new ArrayList<>();
        for (String s : line.substring("lore:".length()).split(",")) {
            lore.add(MSG.color(MSG.RESET + s));
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore())
            return null;
        return "lore:" + String.join(",", meta.getLore());
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (!current.toLowerCase().startsWith("lore:")) {
            if ("lore:".startsWith(current.toLowerCase()))
                return Collections.singletonList("lore:");
            return null;
        }
        return null;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.lore";
    }

    @Override
    public String humanReadable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore())
            return null;
        return "with lore: " + String.join(",", meta.getLore());
    }
}
