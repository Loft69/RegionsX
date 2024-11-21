package dev.thew.regions.handler.regionType;

import dev.thew.regions.handler.Handler;
import dev.thew.regions.model.RegionType;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface RegionTypeHandler extends Handler {

    RegionType getType(String regionTypeId);
    RegionType getType(ItemStack itemStack);
    boolean isCustomBlock(ItemStack itemStack);
    boolean isRegionBlock(Block block);
    NamespacedKey getCustomKey();

}
