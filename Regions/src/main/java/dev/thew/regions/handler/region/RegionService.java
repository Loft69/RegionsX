package dev.thew.regions.handler.region;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.thew.regions.handler.database.DatabaseHandler;
import dev.thew.regions.utils.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.thew.regions.Regions;
import dev.thew.regions.handler.database.databases.RegionDatabase;
import dev.thew.regions.event.RegionCreateEvent;
import dev.thew.regions.event.RegionRemoveEvent;
import dev.thew.regions.model.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;

import java.util.*;

@RequiredArgsConstructor
public class RegionService implements RegionHandler {

    private final HashMap<WorldPart, List<Region>> cache = new HashMap<>();
    private final DatabaseHandler databaseHandler;

    @Override
    public void load() {
        RegionDatabase regionDatabase = databaseHandler.getDatabase(RegionDatabase.class);
        List<Region> regions = regionDatabase.startLoad();

        for (Region region : regions){
            if (!baseBlockExists(region.getBaseLocation(), region.getRegionType())) {
                regionDatabase.delete(region);
                continue;
            }
            postLoad(region);
        }
    }

    @Override
    public void shutdown() {
        RegionDatabase regionDatabase = databaseHandler.getDatabase(RegionDatabase.class);

        for (List<Region> regions : cache.values())
            for (Region region : regions)
                regionDatabase.save(region);
    }

    private boolean baseBlockExists(@NonNull Location location, @NonNull RegionType regionType) {

        Block block = location.getBlock();
        Material material = regionType.material();

        if (block.getType() == Material.AIR) return false;
        return block.getType().equals(material);
    }

    public void postLoad(@NonNull Region region) {

        WorldPart worldPart = WorldPart.of(region.getBaseLocation());
        List<Region> regions = cache.getOrDefault(worldPart, new ArrayList<>());

        regions.add(region);
        if (regions.size() == 1) cache.put(worldPart, regions);

        if (!region.isHoloHidden())
            region.showHologram();
    }

    @Override
    public Region getRegion(@NonNull Location location) {

        WorldPart worldPart = WorldPart.of(location);
        List<Region> regions = cache.get(worldPart);

        if (regions == null) return null;

        for (Region region : regions)
            if (region.isInside(location)) return region;

        return null;
    }

    @Override
    public Region getRegionById(@NonNull String id) {

        for (List<Region> regions : cache.values())
            for (Region region : regions)
                if (region.getId().equalsIgnoreCase(id)) return region;


        return null;
    }

    public boolean locHaveWGRegion(Location location) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        boolean isEnable = pluginManager.isPluginEnabled("WorldGuard");
        if (!isEnable) return false;

        com.sk89q.worldedit.util.Location loc = new com.sk89q.worldedit.util.Location(new BukkitWorld(location.getWorld()), location.getX(), location.getY(), location.getZ()); // can also be adapted from Bukkit, as mentioned above
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(new BukkitWorld(location.getWorld()));
        if (regions == null) return false;
        ApplicableRegionSet set = regions.getApplicableRegions(loc.toVector().toBlockPoint());


        return !set.getRegions().isEmpty();
    }

    @Override
    public boolean isAvailable(Player player, @NonNull Location location, RegionType regionType) {

        World world = location.getWorld();
        assert world != null;

        if (!regionType.containsWorld(world.getName())) {
            Regions.sendError(player, "В этом мире запрещено размещать приват");
            return false;
        }

        int playerRegions = countRegions(player);

        if (!checkLimit(player, playerRegions + 1)) {
            Regions.sendError(player, "Достигнут лимит владения приватами §8(§7увеличить лимит §f/donate§8)");
            return false;
        }

        WorldPart worldPart = WorldPart.of(location);
        List<Region> regions = cache.getOrDefault(worldPart, Collections.emptyList());

        Selection selection = new Selection(location, regionType);

        for (Region region : regions)
            if (region.isCrossing(selection)) {
                Regions.sendError(player, Message.REGIONS_CROSSING);
                return false;
            }

        if (checkWGRegion(location, regionType)){
            Regions.sendError(player, Message.REGIONS_CROSSING);
            return false;
        }

        return true;
    }

    private boolean checkWGRegion(Location center, RegionType regionType) {
        int radius = regionType.radius();

        Location min = center.clone().add(-radius, -radius, -radius);
        Location max = center.clone().add(radius, radius, radius);

        int minX = (int) Math.floor(min.getX());
        int minY = (int) Math.floor(min.getY());
        int minZ = (int) Math.floor(min.getZ());
        int maxX = (int) Math.ceil(max.getX());
        int maxY = (int) Math.ceil(max.getY());
        int maxZ = (int) Math.ceil(max.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(max.getWorld(), x, y, z);
                    if (locHaveWGRegion(location)) return true;
                }
            }
        }

        return false;
    }

    @Override
    public void createRegion(@NonNull Player player, ItemStack itemStack, Location location, RegionType regionType, BlockPlaceEvent event) {

        String regionId = generateRegionId(location);
        Region region = new Region(regionId, location, regionType, player.getName(), null, regionType.endurance(), new ArrayList<>(), false);

        RegionCreateEvent regionCreateEvent = new RegionCreateEvent(region);
        Regions.callEvent(regionCreateEvent);

        if (regionCreateEvent.isCancelled()){
            event.setCancelled(true);
            return;
        }

        itemStack.setAmount(itemStack.getAmount() - 1);
        postLoad(region);
    }

    private @NonNull String generateRegionId(@NonNull Location location) {
        assert location.getWorld() != null;
        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    @Override
    public void clearRegions(){
        cache.clear();
    }

    private boolean checkLimit(@NonNull Player player, int count) {

        int max = 0;

        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (!permission.getPermission().startsWith("regions.limit.")) continue;

            String[] permissionParts = permission.getPermission().split("\\.");
            if (permissionParts.length != 3) continue;

            int limit = Integer.parseInt(permissionParts[2]);
            if (limit > max) max = limit;
        }

        return count <= max;
    }

    @Override
    public int countRegions(@NonNull Player player) {

        int count = 0;

        for (List<Region> regions : cache.values())
            for (Region region : regions)
                if (region.isOwner(player.getName())) count++;

        return count;
    }

    @Override
    public Region getRegionByBaseLocation(Location location) {

        WorldPart worldPart = WorldPart.of(location);
        List<Region> regions = cache.getOrDefault(worldPart, Collections.emptyList());

        for (Region region : regions)
            if (region.getBaseLocation().equals(location)) return region;

        return null;
    }

    @Override
    public void deleteRegion(@NonNull Region region, BreakCause cause) {

        WorldPart worldPart = WorldPart.of(region.getBaseLocation());

        List<Region> regions = cache.getOrDefault(worldPart, Collections.emptyList());
        regions.remove(region);

        region.hideHologram();

        RegionDatabase regionDatabase = databaseHandler.getDatabase(RegionDatabase.class);
        regionDatabase.delete(region);

        RegionRemoveEvent regionRemoveEvent = new RegionRemoveEvent(region, cause);
        Regions.callEvent(regionRemoveEvent);
    }

    @Override
    public List<Region> getPlayerRegions(@NonNull String targetName) {

        List<Region> result = new ArrayList<>();

        for (List<Region> regions : cache.values())
            for (Region region : regions)
                if (region.isMemberOrOwner(targetName)) result.add(region);

        return result;
    }

    @Override
    public List<Region> getRegions() {

        List<Region> result = new ArrayList<>();

        for (List<Region> regions : cache.values())
            result.addAll(regions);

        return result;
    }

    @Override
    public void damageRegion(@NonNull Region region, int damage, BreakCause breakCause){
        region.decreaseEndurance(damage);

        if (!region.haveEndurance()){
            deleteRegion(region, breakCause);
            return;
        }
        region.reloadHologram();
    }
}
