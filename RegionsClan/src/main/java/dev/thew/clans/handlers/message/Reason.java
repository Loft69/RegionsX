package dev.thew.clans.handlers.message;

import dev.thew.clans.model.Clan;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public enum Reason {

    COMMAND_NOT_FOUND {
        @Override
        String finallyText(FileConfiguration config, Player player, Clan clan, String... args) {
            return config.getString(this.name());
        }
    };


    abstract String finallyText(FileConfiguration config, Player player, Clan clan, String... args);

}
