package dev.thew.clans;

import dev.thew.clans.handlers.HandlerService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Clans extends JavaPlugin {

    @Setter
    @Getter
    private static Clans instance;
    private final HandlerService handler = new HandlerService();

    @Override
    public void onEnable() {
        setInstance(this);
        handler.load();
    }

    @Override
    public void onDisable() {
        handler.shutdown();
    }
}
