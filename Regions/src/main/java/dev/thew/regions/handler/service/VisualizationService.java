package dev.thew.regions.handler.service;

import dev.thew.regions.model.Region;
import org.bukkit.Location;
import org.bukkit.Particle;
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

    private static void spawnParticle(Player player, double x, double y, double z) {
        player.spawnParticle(Particle.HEART, x + 0.5, y + 0.5, z + 0.5, 1);
    }
}
