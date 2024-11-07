package dev.thew.potionregions;

import dev.thew.potionregions.model.Potion;
import dev.thew.potionregions.service.PotionHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class PotionRegions extends JavaPlugin {

    @Setter
    @Getter
    private static PotionRegions instance;
    private final PotionHandler potionHandler = new PotionHandler();

    @Override
    public void onEnable() {
        setInstance(this);
        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();

        potionHandler.load();
    }

    @Override
    public void onDisable() {
        HashMap<Player, Potion> potions = potionHandler.getPlayers();
        for (Map.Entry<Player, Potion> entry : potions.entrySet()) {
            Potion potion = entry.getValue();
            Player player = entry.getKey();

            for (PotionEffect effect : potion.getEffects())
                player.removePotionEffect(effect.getType());
        }
    }
}
