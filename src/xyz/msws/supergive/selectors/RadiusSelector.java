package xyz.msws.supergive.selectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RadiusSelector implements Selector {

	@Override
	public List<Entity> getEntities(String arg, CommandSender sender) {
		int radius = 0;
		if (!sender.hasPermission("supergive.selector.radius"))
			return null;
		if (!arg.startsWith("radius:"))
			return null;
		try {
			radius = Integer.parseInt(arg.substring("radius:".length()));
		} catch (NumberFormatException e) {
			return null;
		}
		if (sender instanceof CommandBlock) {
			CommandBlock block = (CommandBlock) sender;
			return new ArrayList<>(block.getWorld().getNearbyEntities(block.getLocation(), radius, radius, radius));
		} else if (sender instanceof Player) {
			Player player = (Player) sender;
			return new ArrayList<>(player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius));
		}
		return null;
	}

	@Override
	public String getDescriptor(String arg, CommandSender sender) {
		return "entities within " + arg.substring("radius:".length()) + " blocks";
	}

	@Override
	public List<String> tabComplete(String current) {
		if (!current.toLowerCase().startsWith("radius:")) {
			if ("radius:".startsWith(current.toLowerCase()))
				return Arrays.asList("radius:");
			return null;
		}
		return null;
	}

}
