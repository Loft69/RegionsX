package dev.thew.regions.handler.visual;

import dev.thew.regions.model.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class VisualizationService {

    public static void renderCorners(Player player, Region region) {

        Location min = region.getMinLocation();
        Location max = region.getMaxLocation();

        double x1 = min.getX();
        double y1 = min.getY();
        double z1 = min.getZ();

        double x2 = max.getX();
        double y2 = max.getY();
        double z2 = max.getZ();

        for (double x = x1; x <= x2; x++) {
            spawnParticle(player, x, y1, z1);
            spawnParticle(player, x, y1, z2);
            spawnParticle(player, x, y2, z1);
            spawnParticle(player, x, y2, z2);
        }

        for (double y = y1; y <= y2; y++) {
            spawnParticle(player, x1, y, z1);
            spawnParticle(player, x1, y, z2);
            spawnParticle(player, x2, y, z1);
            spawnParticle(player, x2, y, z2);
        }

        for (double z = z1; z <= z2; z++) {
            spawnParticle(player, x1, y1, z);
            spawnParticle(player, x1, y2, z);
            spawnParticle(player, x2, y1, z);
            spawnParticle(player, x2, y2, z);
        }
    }

    public static void setCorners(Region region, Material material) {

        Location min = region.getMinLocation();
        Location max = region.getMaxLocation();

        World world = min.getWorld();
        assert world != null;

        int x1 = min.getBlockX();
        int y1 = min.getBlockY();
        int z1 = min.getBlockZ();

        int x2 = max.getBlockX();
        int y2 = max.getBlockY();
        int z2 = max.getBlockZ();

        for (int x = x1; x <= x2; x++) {
            setBlock(world, x, y1, z1, material);
            setBlock(world, x, y1, z2, material);
            setBlock(world, x, y2, z1, material);
            setBlock(world, x, y2, z2, material);
        }

        for (int y = y1; y <= y2; y++) {

            setBlock(world, x1, y, z1, material);
            setBlock(world, x1, y, z2, material);
            setBlock(world, x2, y, z1, material);
            setBlock(world, x2, y, z2, material);
        }

        for (int z = z1; z <= z2; z++) {

            setBlock(world, x1, y1, z, material);
            setBlock(world, x1, y2, z, material);
            setBlock(world, x2, y1, z, material);
            setBlock(world, x2, y2, z, material);
        }
    }

    private static void setBlock(World world, int x, int y, int z, Material material) {
        world.getBlockAt(x, y, z).setType(material);
    }

    private static void spawnParticle(Player player, double x, double y, double z) {
        player.spawnParticle(Particle.BLOCK_MARKER, x + 0.5, y + 0.5, z + 0.5, 1);
    }
}
