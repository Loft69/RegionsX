package dev.thew.regions.handler.settings;

import dev.thew.regions.Regions;
import lombok.NonNull;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class SettingsService implements SettingsHandler {

    private final FileConfiguration config;

    public SettingsService(@NonNull Regions instance) {
        if (!new File(instance.getDataFolder(), "config.yml").exists()) instance.saveDefaultConfig();
        this.config = instance.getConfig();
    }

    @Override
    public void load() {}

    @Override
    public void shutdown() {}

    @Override
    public String getDatabaseURL() {
        return config.getString("database-url");
    }
}
