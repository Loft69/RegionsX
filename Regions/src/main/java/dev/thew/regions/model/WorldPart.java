package dev.thew.regions.model;

import org.bukkit.Location;
import org.bukkit.World;

public record WorldPart(World world, int x, int z) {

    public static WorldPart of(Location location) {

        World world = location.getWorld();

        int x = location.getBlockX() / 5000;
        int z = location.getBlockZ() / 5000;

        return new WorldPart(world, x, z);
    }
}
