package xyz.msws.supergive.events;

import org.bukkit.event.HandlerList;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;

/**
 * Called whenever a loadout is added to the {@link LoadoutManager}, this is
 * also consequently called during loadout creation.
 *
 * @author imodm
 */
public class LoadoutLoadEvent extends LoadoutEvent {
    private static final HandlerList handlers = new HandlerList();

    public LoadoutLoadEvent(Loadout loadout) {
        super(loadout);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
