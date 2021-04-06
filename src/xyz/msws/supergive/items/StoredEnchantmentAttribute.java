package xyz.msws.supergive.items;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Adds support for adding enchantments to enchantedbooks.
 *
 * @author imodm
 */
public class StoredEnchantmentAttribute implements ItemAttribute {

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("stored:"))
            return item;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof EnchantmentStorageMeta))
            return item;
        EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
        Enchantment ench = Utils.getEnchantment(line.split(":")[1]);
        if (ench == null) {
            MSG.warn("Unknown enchantment: " + line);
            return item;
        }
        int level = 1;
        try {
            level = line.split(":").length > 2 ? Integer.parseInt(line.split(":")[2]) : 1;
        } catch (NumberFormatException e) {
            MSG.warn("Invalid enchantment level for " + line);
        }
        book.addStoredEnchant(ench, level, true);
        item.setItemMeta(book);
        return item;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getModification(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof EnchantmentStorageMeta))
            return null;
        EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
        StringBuilder builder = new StringBuilder();
        try {
            for (Entry<Enchantment, Integer> entry : book.getStoredEnchants().entrySet()) {
                builder.append("stored:" + MSG.normalize(entry.getKey().getKey().getKey()));
                if (entry.getValue() != 1)
                    builder.append(":").append(entry.getValue());
                builder.append(" ");
            }
        } catch (NoSuchMethodError e) {
            // 1.8 Compatibility
            for (Entry<Enchantment, Integer> entry : book.getStoredEnchants().entrySet()) {
                builder.append("stored:" + MSG.normalize(entry.getKey().getName()));
                if (entry.getValue() != 1)
                    builder.append(":").append(entry.getValue());
                builder.append(" ");
            }
        }

        return builder.toString().trim();
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (args.length < 2)
            return null;
        if (!MSG.normalize(args[1]).equalsIgnoreCase("enchantedbook"))
            return null;
        List<String> result = new ArrayList<>();

        if ("stored:".startsWith(current))
            result.add("stored:");
        if (current.startsWith("stored:")) {
            try {
                for (Enchantment ench : Enchantment.values()) {
                    if (("stored" + MSG.normalize(ench.getKey().getKey())).startsWith(MSG.normalize(current)))
                        result.add("stored:" + MSG.normalize(ench.getKey().getKey()) + ":");
                }
            } catch (NoSuchMethodError e) {
                // 1.8 Compatibility
                for (Enchantment ench : Enchantment.values()) {
                    if (("stored" + MSG.normalize(ench.getName())).startsWith(MSG.normalize(current)))
                        result.add("stored:" + MSG.normalize(ench.getKey().getKey()) + ":");
                }
            }

        }
        return result;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.stored";
    }

    @SuppressWarnings("deprecation")
    @Override
    public String humanReadable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof EnchantmentStorageMeta))
            return null;
        EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
        List<String> enchantments = new ArrayList<>();

        String result = "&6storing &a";
        try {
            for (Entry<Enchantment, Integer> ench : book.getStoredEnchants().entrySet()) {
                enchantments.add(ench.getKey().getKey().getKey() + (ench.getValue() == 1 ? "" : ench.getValue() + ""));
            }
        } catch (NoSuchMethodError e) {
            // 1.8 Compatibility
            for (Entry<Enchantment, Integer> ench : book.getStoredEnchants().entrySet()) {
                enchantments.add(ench.getKey().getName() + (ench.getValue() == 1 ? "" : ench.getValue() + ""));
            }
        }

        result = result + String.join(" &7and &a", enchantments);
        return result.trim();
    }

}
