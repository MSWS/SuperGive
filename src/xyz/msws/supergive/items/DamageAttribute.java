package xyz.msws.supergive.items;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.msws.supergive.utils.MSG;

import java.util.Collections;
import java.util.List;

/**
 * Adds support for specifying damage on {@link Damageable} items.
 *
 * @author imodm
 */
public class DamageAttribute implements ItemAttribute {

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("damage:"))
            return item;
        ItemMeta meta = item.getItemMeta();
        try {
            Class.forName("org.bukkit.inventory.meta.Damageable");
            if (!(meta instanceof Damageable))
                return item;
            try {
                ((Damageable) meta).setDamage(Integer.parseInt(line.substring("damage:".length())));
            } catch (NumberFormatException e) {
                MSG.warn("Invalid number for damage: " + line.substring("damage:".length()));
            }
            item.setItemMeta(meta);
            return item;
        } catch (ClassNotFoundException e) {
            try {
                item.setDurability(Short.parseShort(line.substring("damage:".length())));
            } catch (NumberFormatException e1) {
                // 1.8 Compatibility
                MSG.warn("Invalid number for damage: " + line.substring("damage:".length()));
            }
            return item;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getModification(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        int durability = 0;
        try {
            Class.forName("org.bukkit.inventory.meta.Damageable");
            if (!(meta instanceof Damageable))
                return null;
            durability = ((Damageable) meta).getDamage();
        } catch (ClassNotFoundException e) {
            // 1.8 Compatibility
            durability = ((Number) item.getDurability()).intValue();
        }

        return durability == 0 ? null : "damage:" + durability;
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (!"damage:".startsWith(current.toLowerCase()))
            return null;
        return Collections.singletonList("damage:");
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.damage";
    }

    @SuppressWarnings("deprecation")
    @Override
    public String humanReadable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        int durability = 0;
        try {
            Class.forName("org.bukkit.inventory.meta.Damageable");
            if (!(meta instanceof Damageable))
                return null;
            durability = ((Damageable) meta).getDamage();
        } catch (ClassNotFoundException e) {
            // 1.8 Compatibility
            durability = ((Number) item.getDurability()).intValue();
        }

        return durability == 0 ? null : "with " + durability + " damage";
    }

}
