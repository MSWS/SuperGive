package xyz.msws.supergive.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import xyz.msws.supergive.loadout.Loadout;

/**
 * Called when a loadout is deleted.
 *
 * @author imodm
 */
public class LoadoutDeleteEvent extends LoadoutEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancel = false;

    public LoadoutDeleteEvent(Loadout loadout) {
        super(loadout);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

}
