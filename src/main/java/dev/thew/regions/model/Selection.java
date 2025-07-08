package dev.thew.regions.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Selection {
    Location min;
    Location max;

    public Selection(Location location, RegionType regionType) {

        int radius = regionType.radius();

        min = location.clone().add(-radius, -radius, -radius);
        max = location.clone().add(radius, radius, radius);
    }
}
