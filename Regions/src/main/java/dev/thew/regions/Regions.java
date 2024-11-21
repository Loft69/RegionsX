package dev.thew.regions;

import dev.thew.regions.handler.*;
import dev.thew.regions.utils.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public final class Regions extends JavaPlugin {

    @Getter
    @Setter
    private static Regions instance;
    private final HandlerService handlerService = new HandlerService();

    @Override
    public void onEnable() {
        setInstance(this);

        handlerService.load(this);
    }

    @Override
    public void onDisable() {
        handlerService.shutdown();
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void sendError(Player player, String message) {
        player.sendMessage(String.format(Message.ERROR_PREFIX, message));
    }
}
