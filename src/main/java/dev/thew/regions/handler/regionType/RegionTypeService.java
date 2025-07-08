package dev.thew.regions.handler.regionType;

import dev.thew.regions.Regions;
import dev.thew.regions.model.HologramModel;
import dev.thew.regions.model.RegionType;
import dev.thew.regions.model.WhoHide;
import dev.thew.regions.utils.FileUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegionTypeService implements RegionTypeHandler {
    NamespacedKey isCustomFlag = null;
    final List<RegionType> regionTypes = new ArrayList<>();

    @Override
    public void load() {
        isCustomFlag = new NamespacedKey(Regions.getInstance(), "regions_custom");

        YamlConfiguration configuration = FileUtils.loadConfiguration("regionTypes.yml");

        ConfigurationSection typesSection = configuration.getConfigurationSection("types");
        assert typesSection != null;

        for (String typeId : typesSection.getKeys(false)) {
            ConfigurationSection typeSection = typesSection.getConfigurationSection(typeId);
            assert typeSection != null;

            String materialString = typeSection.getString("material");
            int radius = typeSection.getInt("radius");
            boolean isCustomBlock = typeSection.getBoolean("custom_block");
            String hexColor = typeSection.getString("color");
            int endurance = typeSection.getInt("endurance");

            assert materialString != null;
            Material material = Material.valueOf(materialString.toUpperCase());
            List<String> worlds = typeSection.getStringList("worlds");

            List<String> holoLines = typeSection.getStringList("holo.lines");
            boolean enabledTitleItem = typeSection.getBoolean("holo.titleItem");
            boolean enabledHide = typeSection.getBoolean("holo.hide.canHide");
            WhoHide whoHide = WhoHide.valueOf(typeSection.getString("holo.hide.canUse"));
            double appendY = typeSection.getDouble("holo.appendY");
            HologramModel hologramModel = new HologramModel(appendY, enabledTitleItem, holoLines);
            boolean canExplode = typeSection.getBoolean("canExplode");
            boolean isClanType = typeSection.getBoolean("isClanType");

            RegionType regionType = new RegionType(typeId,
                    material,
                    radius,
                    isCustomBlock,
                    endurance,
                    hexColor,
                    worlds,
                    hologramModel,
                    enabledHide,
                    whoHide,
                    canExplode,
                    isClanType);
            regionTypes.add(regionType);
        }
    }

    @Override
    public void shutdown() {}

    @Override
    public RegionType getType(String regionTypeId) {

        for (RegionType type : regionTypes)
            if (type.id().equals(regionTypeId)) return type;

        throw new RuntimeException("Region type with id='" + regionTypeId + "' not found!");
    }

    @Override
    public RegionType getType(ItemStack itemStack) {

        for (RegionType regionType : regionTypes) {
            Material material = regionType.material();

            if (material.equals(itemStack.getType()) && (!regionType.isCustomBlock() || isCustomBlock(itemStack))) return regionType;
        }

        return null;
    }

    @Override
    public boolean isCustomBlock(ItemStack itemStack) {

        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        assert isCustomFlag != null;

        return dataContainer.has(isCustomFlag, PersistentDataType.STRING);
    }

    @Override
    public boolean isRegionBlock(Block block) {
        Material material = block.getType();

        for (RegionType regionType : regionTypes)
            if (regionType.material().equals(material)) return false;

        return true;
    }

    @Override
    public NamespacedKey getCustomKey() {
        return isCustomFlag;
    }
}
