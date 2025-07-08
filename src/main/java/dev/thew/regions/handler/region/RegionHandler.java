package dev.thew.regions.handler.region;

import dev.thew.regions.handler.Handler;
import dev.thew.regions.model.BreakCause;
import lombok.NonNull;
import dev.thew.regions.model.Region;
import dev.thew.regions.model.RegionType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface RegionHandler extends Handler {

    Region getRegion(@NonNull Location location);
    Region getRegionById(@NonNull String id);
    int countRegions(@NonNull Player player);
    void clearRegions();
    Region getRegionByBaseLocation(Location location);
    void deleteRegion(@NonNull Region region, BreakCause cause);
    List<Region> getPlayerRegions(@NonNull String nickname);
    List<Region> getRegions();
    void damageRegion(@NonNull Region region, int damage, BreakCause breakCause);
    boolean isAvailable(Player player, @NonNull Location location, RegionType regionType);
    void createRegion(@NonNull Player player, ItemStack itemStack, Location location, RegionType regionType, BlockPlaceEvent event);
    
}
