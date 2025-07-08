package dev.thew.regions.event;

import dev.thew.regions.model.BreakCause;
import dev.thew.regions.model.Region;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class RegionRemovePrimeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Region region;
    private final BreakCause cause;
    private final Entity source;
    private boolean isCancelled = false;

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
