package dev.thew.regions.handler.observer;

import com.google.common.collect.Lists;
import dev.thew.regions.craft.Explosion;
import dev.thew.regions.craft.ExplosionDamageCalculator;
import dev.thew.regions.event.RegionExplodeEvent;
import dev.thew.regions.event.RegionRemovePrimeEvent;
import dev.thew.regions.model.BreakCause;
import dev.thew.regions.utils.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.thew.regions.Regions;
import dev.thew.regions.model.Region;
import dev.thew.regions.model.RegionType;
import dev.thew.regions.handler.menu.MenuHandler;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.handler.regionType.RegionTypeHandler;
import dev.thew.regions.handler.Handler;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class BlocksObserverService implements Listener, Handler {

    private final RegionHandler regionsService;
    private final RegionTypeHandler regionTypesService;
    private final MenuHandler menuService;

    @Override
    public void load() {
        Regions instance = Regions.getInstance();
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @Override
    public void shutdown() {}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreateRegion(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        ItemStack itemStack = event.getItemInHand();
        RegionType regionType = regionTypesService.getType(itemStack);
        if (regionType == null) return;

        if (!regionsService.isAvailable(player, location, regionType)) {
            event.setCancelled(true);
            return;
        }

        regionsService.createRegion(player, itemStack, location, regionType, event);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreakRegion(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        if (regionTypesService.isRegionBlock(event.getBlock())) return;

        Region region = regionsService.getRegionByBaseLocation(location);
        if (region == null) return;

        if (!region.isOwner(player.getName())) {
            Regions.sendError(player, Message.U_NOT_OWNER);
            event.setCancelled(true);
            return;
        }

        RegionRemovePrimeEvent regionRemovePrimeEvent = new RegionRemovePrimeEvent(region, BreakCause.BLOCK_BREAK, event.getPlayer());
        Regions.callEvent(regionRemovePrimeEvent);
        if (regionRemovePrimeEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        regionsService.deleteRegion(region, BreakCause.BLOCK_BREAK);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInteractRegion(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();

        if (block == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        if (regionTypesService.isRegionBlock(block)) return;

        Location location = block.getLocation();
        Region region = regionsService.getRegionByBaseLocation(location);
        if (region == null) return;

        if (!region.isMemberOrOwner(player.getName())) {
            Regions.sendError(player, Message.U_NOT_MEMBER);
            return;
        }

        event.setCancelled(true);
        menuService.open(player, region);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        if (!regionsExist(event.getBlocks()).isEmpty()) event.setCancelled(true);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        if (!regionsExist(event.getBlocks()).isEmpty()) event.setCancelled(true);
    }

    @EventHandler
    public void onExplodeBlock(@NonNull BlockExplodeEvent event) {
        removeRegionFromExplosion(event.blockList());
    }

    @EventHandler
    public void onExplodeEntity(@NonNull EntityExplodeEvent event) {
        removeRegionFromExplosion(event.blockList());
    }

    private List<Region> regionsExist(List<Block> list) {
        List<Region> regions = new ArrayList<>();

        for (Block block : list){
            Region region = getRegionFromBlock(block);
            if (region == null) continue;

            regions.add(region);
        }

        return regions;
    }

    private void removeRegionFromExplosion(List<Block> blocks) {
        List<Region> regions = regionsExist(blocks);

        for (Region region : regions)
            blocks.removeIf(block -> block.equals(region.getBaseLocation().getBlock()));
    }

    @EventHandler
    public void onPrimeExplode(ExplosionPrimeEvent event){
        Entity entity = event.getEntity();
        Location location = entity.getLocation().clone();

        if (entity instanceof TNTPrimed tntPrimed)
            location.setY(location.getY() + tntPrimed.getHeight() * 0.0625D);
        else if (entity instanceof Wither wither)
            location.setY(location.getY() + wither.getHeight());

        float radius = event.getRadius();
        handleExplode(location, radius, entity);
    }

    private void handleExplode(Location location, float radius, Entity entity) {

        Set<Region> set = new HashSet<>(getRegionFromExplosion(location, radius - 2));

        List<Region> regions = Lists.newArrayList(set);

        for (Region region : regions){

            RegionType regionType = region.getRegionType();
            if (!regionType.canExplode()) continue;

            if (region.getEndurance() <= 1){

                RegionRemovePrimeEvent regionRemovePrimeEvent = new RegionRemovePrimeEvent(region, BreakCause.EXPLOSION, entity);
                Regions.callEvent(regionRemovePrimeEvent);
                if (regionRemovePrimeEvent.isCancelled()) return;

                Block block = region.getBaseLocation().getBlock();
                block.breakNaturally();

                regionsService.deleteRegion(region, BreakCause.EXPLOSION);
            } else {

                RegionExplodeEvent regionExplodeEvent = new RegionExplodeEvent(region, entity);
                Regions.callEvent(regionExplodeEvent);
                if (regionExplodeEvent.isCancelled()) return;

                regionsService.damageRegion(region, 1, BreakCause.EXPLOSION);
            }

//            Player owner = Bukkit.getPlayerExact(region.getOwner());
//
//            String x = region.getX();
//            String y = region.getY();
//            String z = region.getZ();
//
//            String alarm = "§x§f§b§7§1§0§0Вторжение в регион!";
//            String coords = "Координаты §x§a§3§f§b§0§0" + x + " " + y + " " + z;
//
//            if (owner != null) owner.sendTitle(alarm, coords, 15, 40, 15);
//
//            for (String stringMember : region.getMembers()) {
//                Player member = Bukkit.getPlayerExact(stringMember);
//                if (member != null) member.sendTitle(alarm, coords, 15, 40, 15);
//            }
        }
    }

    private Set<Region> getRegionFromExplosion(Location location, float size) {

        List<Block> blocks = getBlocks(location, size);
        Set<Region> regions = new HashSet<>();

        for (Block block : blocks) {
            Region region = getRegionFromBlock(block);
            if (region == null) continue;

            regions.add(region);
        }

        return regions;
    }

    private List<Block> getBlocks(Location location, float size) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        World world = location.getWorld();
        assert world != null;
        WorldServer worldServer = ((CraftWorld) world).getHandle();

        ExplosionDamageCalculator explosionDamageCalculator = new ExplosionDamageCalculator();
        Explosion explosion = new Explosion(explosionDamageCalculator, worldServer, world, x, y, z, size, true);

        return explosion.explode();
    }

    private Region getRegionFromBlock(Block block) {
        if (regionTypesService.isRegionBlock(block)) return null;

        Location location = block.getLocation();

        return regionsService.getRegionByBaseLocation(location);
    }


}
