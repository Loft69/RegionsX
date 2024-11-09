package dev.thew.tntregions.service;

import dev.thew.regions.handler.Handler;
import dev.thew.tntregions.TNTRegions;
import dev.thew.tntregions.model.CustomTNT;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class TNTService implements Handler {

    private FileConfiguration config = null;
    private final List<CustomTNT> tnts = new ArrayList<>();
    private final HashMap<Block, CustomTNT> cache = new HashMap<>();
    private NamespacedKey namespacedKey = null;

    @Override
    public void load() {
        TNTRegions tntRegions = TNTRegions.getInstance();



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

            List<Material> toRemove = new ArrayList<>();
            List<String> toRemoveLore = tntSection.getStringList("toremove");

            for (String str : toRemoveLore) {
                Material mat = Material.getMaterial(str.toUpperCase());
                if (mat == null) continue;
                if (mat.equals(Material.AIR)) continue;

                toRemove.add(mat);
            }

            CustomTNT customTNT = CustomTNT.builder()
                    .toRemove(toRemove)
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

            tnts.add(customTNT);
        }

        File file = new File(TNTRegions.getInstance().getDataFolder().getAbsolutePath());
        File[] files = file.listFiles();
        if (files != null && files.length > 1) {
            for (File f : files) {
                if (!f.getName().equalsIgnoreCase("config.yml")) {
                    String id = f.getName();
                    createCacheById(id, f);
                }
            }
        }
    }

    @SneakyThrows
    @Override
    public void shutdown() {

        File file = new File(TNTRegions.getInstance().getDataFolder().getAbsolutePath());
        File[] files = file.listFiles();
        if (files != null && files.length > 1) {
            for (File f : files) {
                if (!f.getName().equalsIgnoreCase("config.yml")){
                    boolean isDelete = f.delete();
                    if (isDelete) System.out.println("Deleted " + f.getName());
                    else System.out.println("Failed to delete " + f.getName());
                }

            }
        }

        for (Map.Entry<Block, CustomTNT> entry : cache.entrySet()) {
            Block block = entry.getKey();
            if (block == null) continue;
            if (!block.getType().equals(Material.TNT)) continue;

            String generateId = generateId(entry.getKey(), entry.getValue().getName());
            File ff = new File(TNTRegions.getInstance().getDataFolder().getAbsolutePath(), generateId);
            if (!ff.exists()){
                boolean isCreated = ff.createNewFile();
                if (isCreated) System.out.println("File " + generateId + " success create");
                else System.out.println("File " + generateId + " failed create");
            }



        }

    }

    private void createCacheById(String raw, File file) {
        String[] args = raw.split(";");
        if (args.length != 5) throw new RuntimeException("Invalid number of arguments in yml file " + raw);

        String worldName = args[0];
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);
        String name = args[4];

        World world = Bukkit.getWorld(worldName);
        if (world == null) throw new RuntimeException("World " + worldName + " not found");

        Block block = world.getBlockAt(x, y, z);
        if (!block.getType().equals(Material.TNT)) {
            file.delete();
            return;
        }

        CustomTNT customTNT = getCustomTNT(name);
        if (customTNT == null) throw new RuntimeException("CustomTNT " + name + " not found");

        cache.put(block, customTNT);
    }

    private String generateId(Block block, String name) {
        World world = block.getWorld();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        String worldName = world.getName();

        return worldName + ";" + x + ";" + y + ";" + z + ";" + name;
    }

    public CustomTNT getCustomTNT(TNTPrimed tntPrimed) {
        for (Map.Entry<Block, CustomTNT> entry : cache.entrySet()) {

            TNTPrimed tntPrime = entry.getValue().getEntity();
            if (tntPrime == null) continue;
            if (tntPrime.equals(tntPrimed)) return entry.getValue();
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

    public void setMetaDataPlacedBlock(String data, Block block) {
        block.setMetadata(data, new FixedMetadataValue(TNTRegions.getInstance(), data));
    }

    public String isTNTBlock(Block block) {
        if (!block.getType().equals(Material.TNT)) return null;
        return isTNTMeta(block);
    }

    public CustomTNT getCacheTNT(Block block){
        return cache.getOrDefault(block, null);
    }

    public String isTNTMeta(Block block) {
        for (CustomTNT customTNT : tnts)
            if (block.hasMetadata(customTNT.getName())) return customTNT.getName();

        return "";
    }

    public void remove(Block block) {
        cache.remove(block);
    }

    public void remove(CustomTNT customTNT) {
        List<Block> forRemove = new ArrayList<>();

        for (Map.Entry<Block, CustomTNT> entry : cache.entrySet()) {
            CustomTNT oldTnt = entry.getValue();
            if (oldTnt.getEntity().equals(customTNT.getEntity())) forRemove.add(entry.getKey());
        }

        for (Block block : forRemove) remove(block);
    }

    public void put(Block block, CustomTNT customTNT){
        cache.put(block, customTNT);
    }


}

