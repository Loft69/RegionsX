package dev.thew.regions.utils;

import org.bukkit.Location;
import org.bukkit.World;

public class TextUtils {

    private TextUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String renderLocation(Location location) {
        World world = location.getWorld();
        assert  world != null;

        String worldName = world.getName();
        String coords = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();

        return  coords + " §8(§7" + worldName + "§8)";
    }
}
