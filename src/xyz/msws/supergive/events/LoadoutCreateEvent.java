package xyz.msws.supergive.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import xyz.msws.supergive.loadout.Loadout;

/**
 * Called when a player creates a loadout with /loadout create.
 *
 * @author imodm
 */
public class LoadoutCreateEvent extends LoadoutEvent {
    private static final HandlerList handlers = new HandlerList();

    private Player player;

    public LoadoutCreateEvent(Player creator, Loadout loadout) {
        super(loadout);
        this.loadout = loadout;
    }

    public void setLoadout(Loadout loadout) {
        this.loadout = loadout;
    }

    public Loadout getLoadout() {
        return loadout;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
