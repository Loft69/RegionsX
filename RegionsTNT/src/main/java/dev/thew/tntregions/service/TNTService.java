package dev.thew.tntregions.service;

import dev.thew.regions.event.RegionExplodeEvent;
import dev.thew.regions.handler.Handler;
import dev.thew.regions.handler.HandlerService;
import dev.thew.regions.handler.RegionHandler;
import dev.thew.regions.handler.service.RegionService;
import dev.thew.regions.handler.service.VisitorsService;
import dev.thew.regions.handler.service.VisualizationService;
import dev.thew.regions.model.BreakCause;
import dev.thew.regions.model.Region;
import dev.thew.tntregions.TNTRegions;
import dev.thew.tntregions.model.CustomTNT;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_16_R3.IFluidContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TNTService implements Listener, Handler {

    private FileConfiguration config = null;
    private final List<CustomTNT> tnts = new ArrayList<>();
    private NamespacedKey namespacedKey = null;

    @Override
    public void load() {
        TNTRegions tntRegions = TNTRegions.getInstance();
        Bukkit.getPluginManager().registerEvents(this, tntRegions);

        namespacedKey = new NamespacedKey(tntRegions, "CustomTNT");

        if (config == null) return;
        ConfigurationSection tntsSection = config.getConfigurationSection("tnts");
        if (tntsSection == null) return;
        for (String key : tntsSection.getKeys(false)) {
            ConfigurationSection tntSection = tntsSection.getConfigurationSection(key);
            if (tntSection == null) continue;

            ConfigurationSection explosionSection = tntSection.getConfigurationSection("explosion");
            if (explosionSection == null) continue;

            double yield = explosionSection.getDouble("yield");
            boolean setGlowing = explosionSection.getBoolean("setGlowing");
            int fuseTicks = explosionSection.getInt("fuseTicks");
            int damage = explosionSection.getInt("damaged");

            ConfigurationSection itemSection = tntSection.getConfigurationSection("item");
            if (itemSection == null) continue;

            String name = itemSection.getString("name");
            List<String> lore = itemSection.getStringList("lore");

            ConfigurationSection cornersSection = tntSection.getConfigurationSection("corners");
            if (cornersSection == null) continue;

            boolean enable = cornersSection.getBoolean("enable");
            String materialString = cornersSection.getString("material");
            if (materialString == null) continue;
            Material material = Material.getMaterial(materialString);

            ConfigurationSection fireSection = tntSection.getConfigurationSection("fire");
            if (fireSection == null) continue;

            boolean enableFire = fireSection.getBoolean("enable");
            int radius = fireSection.getInt("radius");
            int ticks = fireSection.getInt("ticks");


            CustomTNT customTNT = CustomTNT.builder()
                    .radius(radius)
                    .isGlowing(setGlowing)
                    .itemName(name)
                    .material(material)
                    .lore(lore)
                    .damage(damage)
                    .entity(null)
                    .fuseTicks(fuseTicks)
                    .setCorners(enable)
                    .isFire(enableFire)
                    .name(key)
                    .fireTicks(ticks)
                    .yield(yield).build();

            //CustomTNT(key, yield, fuseTicks, setGlowing, damage, name, lore, enable, material);
            // String key, double yield, int fuseTicks, boolean setGlowing, int damage, String itemName, List<String> lore) {
            tnts.add(customTNT);
        }

    }

    @Override
    public void shutdown() {}

    @EventHandler
    public void onExplode(RegionExplodeEvent event) {
        Region region = event.getRegion();
        Entity entity = event.getEntity();

        if (region == null) return;
        if (!(entity instanceof TNTPrimed tntPrimed)) return;

        if (region.haveEndurance()) event.setCancelled(true);

        CustomTNT customTNT = getCustomTNT(tntPrimed);
        if (customTNT == null) return;
        if (!region.haveEndurance()) return;
        if (!customTNT.haveDamage()) return;

        event.setCancelled(true);

        int damage = customTNT.getDamage();
        RegionHandler regionHandler = HandlerService.getHandler(RegionService.class);
        regionHandler.damageRegion(region, damage, BreakCause.EXPLOSION);

        if (customTNT.isSetCorners()) VisualizationService.setCorners(region, customTNT.getMaterial());
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed tntPrimed)) return;

        CustomTNT customTNT = getCustomTNT(tntPrimed);
        if (customTNT == null) return;

        List<Player> players = customTNT.getNearPlayers();
        for (Player player : players)
            player.setFireTicks(customTNT.getFireTicks());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;

        boolean hasBlock = event.hasBlock();
        boolean hasItem = event.hasItem();
        if (!hasBlock && !hasItem) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if (!dataContainer.has(namespacedKey, PersistentDataType.STRING)) return;

        String itemKey = dataContainer.get(namespacedKey, PersistentDataType.STRING);

        CustomTNT customTNT = getCustomTNT(itemKey);
        if (customTNT == null) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Player player = event.getPlayer();
        customTNT.spawnEntity(player, block);

        item.setAmount(item.getAmount() - 1);
    }

    public CustomTNT getCustomTNT(TNTPrimed tntPrimed){
        for (CustomTNT tnt : tnts) {
            TNTPrimed tntPrime = tnt.getEntity();
            if (tntPrime == null) continue;
            if (tntPrime.equals(tntPrimed)) return tnt;
        }

        return null;
    }

    public CustomTNT getCustomTNT(String name){
        for (CustomTNT tnt : tnts) if (tnt.getName().equalsIgnoreCase(name)) return tnt;
        return null;
    }

    public List<String> getIds(){
        List<String> ids = new ArrayList<>();
        for (CustomTNT tnt : tnts) ids.add(tnt.getName());
        return ids;
    }
}
