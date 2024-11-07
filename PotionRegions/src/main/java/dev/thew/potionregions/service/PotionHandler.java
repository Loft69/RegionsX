package dev.thew.potionregions.service;

import dev.thew.potionregions.PotionRegions;
import dev.thew.potionregions.model.Potion;
import dev.thew.regions.event.RegionJoinEvent;
import dev.thew.regions.event.RegionQuitEvent;
import dev.thew.regions.model.Region;
import dev.thew.regions.model.RegionType;
import dev.thew.regions.handler.Handler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PotionHandler implements Listener, Handler {

    private final List<Potion> potions = new ArrayList<>();
    @Getter
    private final HashMap<Player, Potion> players = new HashMap<>();

    @Override
    public void load() {
        Bukkit.getPluginManager().registerEvents(this, PotionRegions.getInstance());

        FileConfiguration config = PotionRegions.getInstance().getConfig();

        ConfigurationSection configurationSection = config.getConfigurationSection("effects");
        assert configurationSection != null;
        for (String key : configurationSection.getKeys(false)) {

            ConfigurationSection effectSection = configurationSection.getConfigurationSection(key);
            assert effectSection != null;

            String regionTypeId = effectSection.getString("regionTypeId");
            assert regionTypeId != null;
            List<PotionEffect> effects = new ArrayList<>();

            ConfigurationSection potionsSection = effectSection.getConfigurationSection("list");
            assert potionsSection != null;
            for (String regionId : potionsSection.getKeys(false)) {
                ConfigurationSection potinoSection = potionsSection.getConfigurationSection(regionId);
                assert potinoSection != null;

                String potinoSectionString = potinoSection.getString("type");
                assert potinoSectionString != null;

                PotionEffectType potionEffectType = PotionEffectType.getByName(potinoSectionString);
                assert potionEffectType != null;

                int amplifier = potinoSection.getInt("amplifier");
                int duration = potinoSection.getInt("duration");
                boolean ambient = potinoSection.getBoolean("ambient");
                boolean particle = potinoSection.getBoolean("particle");
                PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particle);
                effects.add(potionEffect);
            }

            potions.add(new Potion(regionTypeId, effects));
        }

    }

    public Potion getPotion(String regionTypeId) {
        for (Potion potion : potions)
            if (potion.getRegionTypeId().equals(regionTypeId)) return potion;

        return null;
    }


    @EventHandler
    public void onJoin(RegionJoinEvent event) {
        Player player = event.getPlayer();

        Region region = event.getRegion();
        if (!region.isMemberOrOwner(player.getName())) return;

        RegionType regionType = region.getRegionType();

        String regionTypeId = regionType.id();
        Potion potion = getPotion(regionTypeId);
        if (potion == null) return;

        for (PotionEffect effect : potion.getEffects())
            player.addPotionEffect(effect);

        players.put(player, potion);
    }

    @EventHandler
    public void onQuit(RegionQuitEvent event) {
        Player player = event.getPlayer();

        Region region = event.getRegion();
        RegionType regionType = region.getRegionType();

        String regionTypeId = regionType.id();
        Potion potion = getPotion(regionTypeId);
        if (potion == null) return;

        for (PotionEffect effect : potion.getEffects())
            player.removePotionEffect(effect.getType());

        players.remove(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Potion potion = players.get(player);
        if (potion == null) return;

        for (PotionEffect effect : potion.getEffects())
            player.removePotionEffect(effect.getType());
    }



    @Override
    public void shutdown() {}
}
