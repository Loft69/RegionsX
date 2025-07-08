package dev.thew.regions.utils;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.List;

public class InventoryUtils {

    private InventoryUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static @NonNull ItemStack createItem(Material material, String display, List<String> lore) {
        return createItem(new ItemStack(material), display, lore);
    }

    public static @NonNull ItemStack createItem(@NonNull ItemStack icon, String display, List<String> lore) {
        ItemMeta iconMta = icon.getItemMeta();
        assert iconMta != null;
        iconMta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        iconMta.setDisplayName(display);

        if (lore != null) iconMta.setLore(lore);

        icon.setItemMeta(iconMta);
        return icon;
    }

}
