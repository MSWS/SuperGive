package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

public class PotionAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.contains(":"))
			return item;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof PotionMeta))
			return item;
		PotionMeta potion = (PotionMeta) meta;
		PotionEffectType type = Utils.getPotionEffect(line.split(":")[0]);

		if (type == null)
			return item;
		try {
			PotionEffect eff = new PotionEffect(type, Integer.parseInt(line.split(":")[1]),
					line.split(":").length > 2 ? Integer.parseInt(line.split(":")[2]) : 0);
			potion.addCustomEffect(eff, true);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			MSG.warn("Potentially invalid potion format: " + line);
		}
		item.setItemMeta(potion);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof PotionMeta))
			return null;
		PotionMeta potion = (PotionMeta) meta;
		StringBuilder result = new StringBuilder();
		for (PotionEffect effect : potion.getCustomEffects()) {
			result.append(effect.getType().getName()).append(":").append(effect.getDuration());
			if (effect.getAmplifier() != 0)
				result.append(":").append(effect.getAmplifier());
			result.append(" ");
		}
		return result.toString().trim();
	}

	@Override
	public List<String> tabComplete(String current, String[] args, CommandSender sender) {
		if (args.length < 2)
			return null;
		if (!args[1].toLowerCase().contains("potion"))
			return null;
		List<String> result = new ArrayList<>();
		for (PotionEffectType type : PotionEffectType.values()) {
			if (MSG.normalize(type.getName()).startsWith(MSG.normalize(current)))
				result.add(MSG.normalize(type.getName()) + ":");
		}
		return result;
	}

}
