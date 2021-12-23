package xyz.msws.supergive.items;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author imodm
 */
public interface ItemAttribute {
    /**
     * Modifies the itemstack given the string.
     *
     * @param line
     * @param item
     * @return the resulting itemstack
     */
    ItemStack modify(String line, ItemStack item);

    /**
     * Returns a corresponding string that would represent the current modification
     * of an itemstack.
     *
     * @param item
     * @return the list of modifications
     */
    String getModification(ItemStack item);

    /**
     * Returns a string with similar purpose to
     * {@link ItemAttribute#getModification(ItemStack)} however should be more
     * user-friendly.
     *
     * @param item
     * @return a user-friendly version of the modifications
     */
    String humanReadable(ItemStack item);

    /**
     * Returns the permission needed to use this attribute.
     *
     * @return The permission, may be null.
     */
    String getPermission();

    /**
     * Get tab completion possibilities from a string.
     *
     * @param current
     * @param args
     * @param sender
     * @return the list of tab completion options
     */
    List<String> tabComplete(String current, String[] args, CommandSender sender);
}