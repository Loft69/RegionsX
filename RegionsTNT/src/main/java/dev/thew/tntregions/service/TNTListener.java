package dev.thew.tntregions.service;

import dev.thew.regions.craft.Explosion;
import dev.thew.regions.craft.ExplosionDamageCalculator;
import dev.thew.regions.event.RegionExplodeEvent;
import dev.thew.regions.handler.Handler;
import dev.thew.regions.handler.HandlerService;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.handler.region.RegionService;
import dev.thew.regions.handler.visual.VisualizationService;
import dev.thew.regions.model.BreakCause;
import dev.thew.regions.model.Region;
import dev.thew.tntregions.TNTRegions;
import dev.thew.tntregions.model.CustomTNT;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;

public class TNTListener implements Listener, Handler {

    private TNTService tntService = null;

    @EventHandler
    public void onExplode(RegionExplodeEvent event) {
        Region region = event.getRegion();
        Entity entity = event.getEntity();

        Bukkit.getScheduler().runTaskAsynchronously(TNTRegions.getInstance(), () -> {
            if (region == null) return;
            if (!(entity instanceof TNTPrimed tntPrimed)) return;

            if (region.haveEndurance()) event.setCancelled(true);

            CustomTNT customTNT = tntService.getCustomTNT(tntPrimed);
            if (customTNT == null) return;
            if (!region.haveEndurance()) return;
            if (!customTNT.haveDamage()) return;

            event.setCancelled(true);

            int damage = customTNT.getDamage();
            RegionHandler regionHandler = HandlerService.getHandler(RegionService.class);
            regionHandler.damageRegion(region, damage, BreakCause.EXPLOSION);

            if (customTNT.isSetCorners()) VisualizationService.setCorners(region, customTNT.getMaterial());
        });
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed tntPrimed)) return;

        CustomTNT customTNT = tntService.getCustomTNT(tntPrimed);
        if (customTNT == null) return;

        List<Player> players = customTNT.getNearPlayers();
        for (Player player : players)
            player.setFireTicks(customTNT.getFireTicks());


        if (customTNT.isRemoveEnable()){
            List<Block> blocksToRemove = getBlocks(entity.getLocation(), (float) (customTNT.getYield()));
            customTNT.removeMaterial(blocksToRemove);
        }

        tntService.remove(customTNT);
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

    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        for (Block oldBlock : event.getBlocks()) {
            String customTNTname = tntService.isTNTBlock(oldBlock);
            if (customTNTname.isEmpty()) continue;

            Location oldLocation = oldBlock.getLocation().clone();

            CustomTNT customTNT = tntService.getCache().get(oldBlock);
            if (customTNT == null)
                customTNT = tntService.getCustomTNT(customTNTname);

            Vector vector = event.getDirection().getDirection();
            Location newLocation = oldLocation.add(vector);
            Block newBlock = newLocation.getBlock();

            tntService.remove(oldBlock);
            tntService.put(newBlock, customTNT);
        }
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        for (Block oldBlock : event.getBlocks()) {
            String customTNTname = tntService.isTNTBlock(oldBlock);
            if (customTNTname.isEmpty()) continue;

            Location oldLocation = oldBlock.getLocation().clone();

            CustomTNT customTNT = tntService.getCache().get(oldBlock);
            if (customTNT == null)
                customTNT = tntService.getCustomTNT(customTNTname);

            Vector vector = event.getDirection().getDirection();
            Location newLocation = oldLocation.add(vector);
            Block newBlock = newLocation.getBlock();

            tntService.remove(oldBlock);
            tntService.put(newBlock, customTNT);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        if (!block.getType().equals(Material.TNT)) return;

        tntService.remove(block);
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!item.getType().equals(Material.TNT)) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if (!dataContainer.has(tntService.getNamespacedKey(), PersistentDataType.STRING)) return;

        String itemKey = dataContainer.get(tntService.getNamespacedKey(), PersistentDataType.STRING);

        CustomTNT customTNT = tntService.getCustomTNT(itemKey);
        if (customTNT == null) return;

        Block block = event.getBlockPlaced();
        tntService.setMetaDataPlacedBlock(customTNT.getName(), block);

        tntService.getCache().put(block, customTNT);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed tntPrimed)) return;

        Location location = event.getLocation();
        Block block = location.getBlock();

        CustomTNT customTNT = tntService.getCacheTNT(block);
        if (customTNT == null) return;

        customTNT.spawnEntity(tntPrimed);
    }

    @Override
    public void load() {
        TNTRegions tntRegions = TNTRegions.getInstance();
        tntService = tntRegions.getTntService();

        Bukkit.getPluginManager().registerEvents(this, tntRegions);
    }

    @Override
    public void shutdown() {}
}
