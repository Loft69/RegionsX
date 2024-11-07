package dev.thew.tntregions.model;

import dev.thew.tntregions.TNTRegions;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

@Getter
public class CustomTNT {

    private final String name;
    private final double yield;
    private final int fuseTicks;
    private final boolean isGlowing;
    private final int damage;
    private final String itemName;
    private final List<String> lore;
    private TNTPrimed entity = null;

    public CustomTNT(String key, double yield, int fuseTicks, boolean setGlowing, int damage, String itemName, List<String> lore) {
        this.name = key;
        this.yield = yield;
        this.fuseTicks = fuseTicks;
        this.isGlowing = setGlowing;
        this.damage = damage;
        this.itemName = itemName;
        this.lore = lore;
    }

    public boolean haveDamage(){
        return this.damage > 0;
    }

    public ItemStack getItem(){
        ItemStack item = new ItemStack(Material.TNT);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(itemName);
        meta.setLore(lore);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(TNTRegions.getInstance().getTntService().getNamespacedKey(), PersistentDataType.STRING, this.name);

        item.setItemMeta(meta);
        return item;
    }

    public void spawnEntity(Player initializer, Block block) {

        World world = initializer.getWorld();
        TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(block.getLocation().clone().add(0.5, 1, 0.5), EntityType.PRIMED_TNT);

        tntPrimed.setFuseTicks(fuseTicks);
        tntPrimed.setGlowing(isGlowing);
        tntPrimed.setYield((float) yield);

        entity = tntPrimed;
    }

}
