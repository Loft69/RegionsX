package dev.thew.regions.model;

import org.bukkit.event.Event;

@FunctionalInterface
public interface BypassHandler {
    BypassResult handle(Event event);
}
