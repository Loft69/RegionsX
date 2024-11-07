package dev.thew.tntregions.model;

import dev.thew.tntregions.TNTRegions;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class CustomTNT {

    private final String name;
    private final double yield;
    private final int fuseTicks;
    private final boolean isGlowing;
    private final int damage;
    private final String itemName;
    private final List<String> lore;
    private final boolean setCorners;
    private final Material material;
    private final boolean isFire;
    private final int radius;
    private final int fireTicks;
    private TNTPrimed entity;

    public boolean haveDamage(){
        return this.damage > 0;
    }

    public ItemStack getItem(){
        ItemStack item = new ItemStack(Material.TNT);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.itemName));
        meta.setLore(lore);

        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(TNTRegions.getInstance().getTntService().getNamespacedKey(), PersistentDataType.STRING, this.name);

        item.setItemMeta(meta);
        return item;
    }

    public List<Player> getNearPlayers(){
        if (this.entity == null) return Collections.emptyList();
        Location loc = this.entity.getLocation();

        World world = loc.getWorld();
        if (world == null) return Collections.emptyList();

        List<Player> nearPlayers = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()){
            Location playerLocation = player.getLocation();
            if (!loc.getWorld().equals(playerLocation.getWorld())) continue;

            if (playerLocation.distance(loc) < this.radius)
                nearPlayers.add(player);

        }

        return nearPlayers;
    }

    public void spawnEntity(Player initializer, Block block) {

        World world = initializer.getWorld();
        TNTPrimed tntPrimed = (TNTPrimed) world.spawnEntity(block.getLocation().clone().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);

        tntPrimed.setFuseTicks(fuseTicks);
        tntPrimed.setGlowing(isGlowing);
        tntPrimed.setYield((float) yield);

        entity = tntPrimed;
    }

}
