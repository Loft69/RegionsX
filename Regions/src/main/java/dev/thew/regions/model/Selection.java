package dev.thew.regions.model;

import lombok.Data;
import org.bukkit.Location;

@Data
public class Selection {

    private final Location min;
    private final Location max;

    public Selection(Location location, RegionType regionType) {

        int radius = regionType.radius();

        min = location.clone().add(-radius, -radius, -radius);
        max = location.clone().add(radius, radius, radius);
    }
}
