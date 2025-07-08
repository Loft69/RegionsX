package dev.thew.regions.utils;

import lombok.NonNull;
import dev.thew.regions.Regions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static @NonNull YamlConfiguration loadConfiguration(String fileName) {

        Plugin instance = Regions.getInstance();

        instance.saveResource(fileName, false);
        File file = new File(instance.getDataFolder(), fileName);

        return YamlConfiguration.loadConfiguration(file);
    }

}
