package xyz.msws.supergive.items;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Adds support for making items unbreakable.
 *
 * @author imodm
 */
public class UnbreakableAttribute implements ItemAttribute {

    private static Method spigot;
    private static Method isUnbreakable, setUnbreakable;

    @Override
    public ItemStack modify(String line, ItemStack item) {
        if (!line.startsWith("unbreakable:"))
            return item;
        boolean val = Boolean.valueOf(line.substring("unbreakable:".length()));

        ItemMeta meta = item.getItemMeta();
        try {
            meta.setUnbreakable(val);
        } catch (NoSuchMethodError e) {
            // 1.8 Compatibility
            try {
                if (spigot == null) {
                    spigot = meta.getClass().getMethod("spigot");
                    spigot.setAccessible(true);
                }
                Object so = spigot.invoke(meta);
                if (setUnbreakable == null) {
                    setUnbreakable = so.getClass().getMethod("setUnbreakable", boolean.class);
                    setUnbreakable.setAccessible(true);
                }
                setUnbreakable.invoke(so, val);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getModification(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        try {
            return meta.isUnbreakable() ? "unbreakable:true" : null;
        } catch (NoSuchMethodError e) {
            // 1.8 Compatibility
            try {
                if (spigot == null) {
                    spigot = meta.getClass().getMethod("spigot");
                    spigot.setAccessible(true);
                }
                Object c = spigot.invoke(meta);
                if (isUnbreakable == null) {
                    isUnbreakable = c.getClass().getMethod("isUnbreakable");
                    isUnbreakable.setAccessible(true);
                }
                Object result = isUnbreakable.invoke(c);
                if (!(result instanceof Boolean))
                    return null;
                return ((Boolean) result) ? "unbreakable:true" : null;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public List<String> tabComplete(String current, String[] args, CommandSender sender) {
        if (!current.toLowerCase().startsWith("unbrekable:")) {
            if ("unbreakable:".startsWith(current.toLowerCase()))
                return Arrays.asList("unbreakable:");
            return null;
        }
        return null;
    }

    @Override
    public String getPermission() {
        return "supergive.attribute.unbreakable";
    }

    @Override
    public String humanReadable(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        ItemMeta meta = item.getItemMeta();
        try {
            return meta.isUnbreakable() ? "that is unbreakable" : null;
        } catch (NoSuchMethodError e) {
            // 1.8 Compatibility
            try {
                if (spigot == null) {
                    spigot = meta.getClass().getMethod("spigot");
                    spigot.setAccessible(true);
                }
                Object c = spigot.invoke(meta);
                if (isUnbreakable == null) {
                    isUnbreakable = c.getClass().getMethod("isUnbreakable");
                    isUnbreakable.setAccessible(true);
                }
                Object result = isUnbreakable.invoke(c);
                if (!(result instanceof Boolean))
                    return null;
                return ((Boolean) result) ? "that is unbreakable" : null;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

}
