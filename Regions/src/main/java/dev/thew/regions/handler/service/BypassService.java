package dev.thew.regions.handler.service;

import lombok.SneakyThrows;
import dev.thew.regions.bypass.BlocksInteractBypassHandler;
import dev.thew.regions.model.BypassResult;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class BypassService implements dev.thew.regions.handler.Handler {

    private static final List<dev.thew.regions.model.BypassHandler> bypasses = new ArrayList<>();

    @SneakyThrows
    public static BypassResult handle(Event event) {
        for (dev.thew.regions.model.BypassHandler bypassHandler : bypasses) {
            BypassResult bypassResult = bypassHandler.handle(event);
            if (bypassResult != BypassResult.IGNORE) return bypassResult;
        }

        return BypassResult.CANCEL;
    }

    @Override
    public void load() {
        bypasses.add(new BlocksInteractBypassHandler());
    }

    @Override
    public void shutdown() {

    }
}
