package xyz.msws.supergive.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adds support for adding item flags to all itemstacks.
 *
 * @author imodm
 */
public class ItemFlagAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("flag:"))
            return item;
        ItemFlag flag = Utils.getItemFlag(line.substring("flag:".length()));
        if (flag == null)
            return item;
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flag);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        StringBuilder result = new StringBuilder();
        for (ItemFlag flag : meta.getItemFlags()) {
            result.append("flag:").append(MSG.normalize(flag.toString())).append(" ");
        }
        return result.toString().trim();
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (!current.toLowerCase().startsWith("flag:")) {
            if ("flag:".startsWith(current.toLowerCase()))
                return Collections.singletonList("flag:");
            return null;
        }
        List<String> result = new ArrayList<>();
        for (ItemFlag flag : ItemFlag.values()) {
            String fs = MSG.normalize(flag.toString());
            if (MSG.normalize(("flag:" + fs)).toLowerCase().startsWith(MSG.normalize(current))) {
                result.add("flag:" + fs);
            }
        }

        return result;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.itemflag";
    }

    @Override
    public String humanReadable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        if (meta.getItemFlags().isEmpty())
            return null;

        List<String> flags = meta.getItemFlags().stream().map(f -> ChatColor.YELLOW + MSG.camelCase(f.toString())).collect(Collectors.toList());
        return "with the flag" + (flags.size() == 1 ? "" : "s") + ": &e" + String.join("&7, &e", flags);
    }

}
