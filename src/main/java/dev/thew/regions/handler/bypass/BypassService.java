package dev.thew.regions.handler.bypass;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import dev.thew.regions.bypass.BlocksInteractBypassHandler;
import dev.thew.regions.model.BypassResult;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BypassService implements dev.thew.regions.handler.Handler {
    static List<dev.thew.regions.model.BypassHandler> bypasses = new ArrayList<>();

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
