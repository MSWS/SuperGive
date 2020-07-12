package xyz.msws.supergive.events;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.msws.supergive.loadout.Loadout;

/**
 * Called when the /give command is used to give an item
 * 
 * @author imodm
 *
 */
public class CommandGiveItemEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Loadout loadout;
	private List<Entity> receivers;

	public CommandGiveItemEvent(CommandSender sender, List<Entity> receiver, Loadout items) {
		loadout = items;
		this.receivers = receiver;
	}

	public Loadout getLoadout() {
		return loadout;
	}

	public List<Entity> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<Entity> list) {
		this.receivers = list;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
