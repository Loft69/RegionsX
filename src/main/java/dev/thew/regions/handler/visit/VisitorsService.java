package dev.thew.regions.handler.visit;

import lombok.RequiredArgsConstructor;
import dev.thew.regions.Regions;
import dev.thew.regions.event.RegionJoinEvent;
import dev.thew.regions.event.RegionQuitEvent;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.handler.Handler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

@RequiredArgsConstructor
public class VisitorsService implements Listener, Handler {

    private final RegionHandler regionsService;

    private final HashMap<Player, Region> previousRegions = new HashMap<>();

    @Override
    public void load() {
        Regions instance = Regions.getInstance();
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @Override
    public void shutdown() {}

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if (player.getWalkSpeed() <= 0 || player.getFlySpeed() <= 0 || player.getVelocity().length() <= 0) return;

        Location playerLocation = player.getLocation();

        Region currentRegion = regionsService.getRegion(playerLocation);
        Region previousRegion = previousRegions.get(player);

        if (currentRegion == previousRegion) return;

        if (previousRegion != null) {

            RegionQuitEvent regionQuitEvent = new RegionQuitEvent(previousRegion, player);
            Regions.callEvent(regionQuitEvent);

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Вы покинули регион игрока §e" + previousRegion.getOwner()));
        }

        if (currentRegion != null) {

            RegionJoinEvent regionJoinEvent = new RegionJoinEvent(currentRegion, player);
            Regions.callEvent(regionJoinEvent);

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Вы вошли в регион игрока §e" + currentRegion.getOwner()));
        }

        previousRegions.put(player, currentRegion);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        previousRegions.remove(event.getPlayer());
    }
}
