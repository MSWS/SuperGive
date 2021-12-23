package xyz.msws.supergive.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

/**
 * Adds support for specifying the owner of a playerhead.
 *
 * @author imodm
 */
public class OwnerAttribute implements ItemAttribute {

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("owner:"))
            return item;
        if (item.getType() != Material.PLAYER_HEAD)
            return item;
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(line.substring("owner:".length())));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta))
            return null;
        SkullMeta skull = (SkullMeta) meta;
        return "owner:" + skull.getOwningPlayer().getName();
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length > 2) {
            if (!args[1].equalsIgnoreCase("playerhead"))
                return null;
        }
        if (!current.toLowerCase().startsWith("owner:")) {
            if ("owner:".startsWith(current.toLowerCase()))
                return Collections.singletonList("owner:");
            return null;
        }
        return null;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.owner";
    }

    @Override
    public String humanReadable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof SkullMeta))
            return null;
        SkullMeta skull = (SkullMeta) meta;
        return "owned by " + skull.getOwningPlayer().getName();
    }

}
