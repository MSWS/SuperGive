package xyz.msws.supergive.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import xyz.msws.supergive.loadout.Loadout;

/**
 * Called when a loadout is edited.
 *
 * @author imodm
 */
public class LoadoutEditEvent extends LoadoutEvent {
    private static final HandlerList handlers = new HandlerList();

    private Loadout before;

    public LoadoutEditEvent(Player editor, Loadout before, Loadout after) {
        super(after);
        this.before = before;
    }

    public Loadout getOldLoadout() {
        return this.before;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
