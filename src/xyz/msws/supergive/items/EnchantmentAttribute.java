package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

/**
 * Adds support on adding enchantments to all itemstacks.
 * 
 * @author imodm
 *
 */
public class EnchantmentAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.contains(":"))
			return item;
		Enchantment ench = Utils.getEnchantment(line.split(":")[0]);
		if (ench == null) {
			return item;
		}
		try {
			item.addUnsafeEnchantment(ench, Integer.parseInt(line.split(":")[1]));
		} catch (NumberFormatException e) {
		}
		return item;
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return null;
		if (item.getEnchantments().isEmpty())
			return null;
		StringBuilder result = new StringBuilder();
		try {
			for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
				result.append(ench.getKey().getKey().getKey()).append(":").append(ench.getValue()).append(" ");
			}
		} catch (NoSuchMethodError e) {
			for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
				result.append(ench.getKey().getName()).append(":").append(ench.getValue()).append(" ");
			}
		}

		return result.toString().trim();
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<String> tabComplete(String current, String[] args, CommandSender sender) {
		if (current.length() < 3) // Don't spam tab completions
			return null;
		List<String> result = new ArrayList<>();
		try {
			for (Enchantment ench : Enchantment.values()) {
				String n = ench.getKey().getKey();
				if (MSG.normalize(n).startsWith(MSG.normalize(current)))
					result.add(MSG.normalize(n) + ":");
			}
		} catch (NoSuchMethodError e) {
			for (Enchantment ench : Enchantment.values()) {
				String n = ench.getName();
				if (MSG.normalize(n).startsWith(MSG.normalize(current)))
					result.add(MSG.normalize(n) + ":");
			}
		}

		return result;
	}

	@Override
	public String getPermission() {
		return "supergive.attribute.enchantment";
	}

}
