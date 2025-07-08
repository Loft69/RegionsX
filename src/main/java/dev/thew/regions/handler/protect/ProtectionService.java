package dev.thew.regions.handler.protect;

import dev.thew.regions.handler.bypass.BypassService;
import lombok.RequiredArgsConstructor;
import dev.thew.regions.Regions;
import dev.thew.regions.model.BypassResult;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

@RequiredArgsConstructor
public class ProtectionService implements Listener, Handler {

    private final RegionHandler regionsService;

    @Override
    public void load() {
        Regions instance = Regions.getInstance();
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @Override
    public void shutdown() {}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        if (!hasPermission(event.getPlayer(), event.getBlock().getLocation())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (!hasPermission(event.getPlayer(), event.getBlock().getLocation())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        if (block != null && hasPermission(event.getPlayer(), block.getLocation())) return;

        if (BypassService.handle(event) == BypassResult.CANCEL) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!hasPermission(player, event.getVehicle().getLocation())) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (!(event.getAttacker() instanceof Player player)) return;
        if (!hasPermission(player, event.getVehicle().getLocation())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEnter(VehicleEnterEvent event){
        if (!(event.getEntered() instanceof Player player)) return;
        if (!hasPermission(player, event.getVehicle().getLocation())) event.setCancelled(true);
    }


    private boolean hasPermission(Player player, Location location) {
        Region region = regionsService.getRegion(location);
        if (region == null) return true;
        if (player.hasPermission("regions.admin")) return true;
        return region.isMemberOrOwner(player.getName());
    }


}
