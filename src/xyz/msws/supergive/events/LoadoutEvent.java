package xyz.msws.supergive.events;

import org.bukkit.event.Event;
import xyz.msws.supergive.loadout.Loadout;

/**
 * Represents a loadout related event.
 *
 * @author imodm
 */
public abstract class LoadoutEvent extends Event {

    protected Loadout loadout;

    public LoadoutEvent(Loadout loadout) {
        this.loadout = loadout;
    }

    public Loadout getLoadout() {
        return loadout;
    }

    public void setLoadout(Loadout loadout) {
        this.loadout = loadout;
    }
}
