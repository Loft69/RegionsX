package dev.thew.regions.event;

import dev.thew.regions.model.BreakCause;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.thew.regions.model.Region;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class RegionRemoveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Region region;
    private final BreakCause cause;
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
