package dev.thew.regions.handler.hologram;

import dev.thew.regions.Regions;
import dev.thew.regions.handler.Handler;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;

public class HologramService implements Handler {
    @Override
    public void load() {}

    @Override
    public void shutdown() {
        HolographicDisplaysAPI.get(Regions.getInstance()).deleteHolograms();
    }
}
