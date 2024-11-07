package dev.thew.regions;

import dev.thew.regions.handler.*;
import lombok.Getter;
import lombok.Setter;
import dev.thew.regions.command.BaseCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Regions extends JavaPlugin {

    @Getter
    @Setter
    private static Regions instance;
    @Getter
    private final HandlerService handlerService = new HandlerService();

    @Override
    public void onEnable() {
        setInstance(this);

        handlerService.load();

        hookCommand();
    }

    @Override
    public void onDisable() {
        handlerService.shutdown();
    }

    private void hookCommand(){
        PluginCommand command = getCommand("base");
        if (command != null) command.setExecutor(new BaseCommandExecutor());
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void sendError(Player player, String message) {
        player.sendMessage(String.format(" §cОшибка! §x§C§9§C§9§C§9%s", message));
    }
}
