package dev.thew.regions.utils;

import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigReader {
    public static @NonNull String get(@NonNull String input) {
        YamlConfiguration config = FileUtils.loadConfiguration("messages.yml");

        String output = config.getString(input);
        if (output == null) return input;

        return Color.apply(output);
    }
}