package dev.thew.tntregions;

import dev.thew.tntregions.command.TNTCommand;
import dev.thew.tntregions.service.TNTService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TNTRegions extends JavaPlugin {

    @Setter
    @Getter
    private static TNTRegions instance;
    @Getter
    private final TNTService tntService = new TNTService();

    @Override
    public void onEnable() {
        setInstance(this);

        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
        FileConfiguration config = getConfig();

        tntService.setConfig(config);
        tntService.load();

        hookCommand();
    }

    private void hookCommand(){
        PluginCommand command = getCommand("customtnt");
        if (command != null) command.setExecutor(new TNTCommand(tntService));
    }
}
