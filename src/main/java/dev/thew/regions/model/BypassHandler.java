package dev.thew.regions.model;

import org.bukkit.event.Event;

public interface BypassHandler {
    BypassResult handle(Event event);
}
