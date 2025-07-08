package dev.thew.regions.model;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public record RegionType(String id, Material material, int radius, boolean isCustomBlock, int endurance,
                         String hexColor, List<String> worlds, HologramModel hologramModel, boolean canHide,
                         WhoHide whoHide, boolean canExplode, boolean isClanType) {
    public String render() {
        return String.format("%s₪ §f%s блоков", hexColor, radius);
    }

    public boolean containsWorld(String world) {
        return worlds.contains(world);
    }

    public ItemStack getItemStack(NamespacedKey key) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        if (isCustomBlock){
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            persistentDataContainer.set(key, PersistentDataType.STRING, "regions_custom");
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
