package dev.thew.regions.handler.settings;

import dev.thew.regions.Regions;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingsService implements SettingsHandler {
    FileConfiguration config;

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
