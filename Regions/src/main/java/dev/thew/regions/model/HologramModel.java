package dev.thew.regions.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import dev.thew.regions.Regions;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@Getter
public class HologramModel {

    private double appendY;
    private boolean enableTitleItem;
    private List<String> list;

    public void execute(@NonNull Region region) {
        Location hologramLocation = region.getBaseLocation().clone().add(0.5, appendY, 0.5);

        Hologram hologram = HolographicDisplaysAPI.get(Regions.getInstance()).createHologram(hologramLocation);

        region.setHologram(hologram);
    }

    public void reload(@NonNull Region region) {

        Hologram hologram = region.getHologram();
        if (hologram == null) execute(region);

        region.setHoloHidden(false);
        setLines(region);
    }

    private void setLines(@NonNull Region region) {
        Hologram hologram = region.getHologram();
        hologram.getLines().clear();
        RegionType regionType = region.getRegionType();

        if (enableTitleItem) {
            hologram.getLines().appendItem(new ItemStack(regionType.material()));
        }

        for (String line : list) {
            if (line.equalsIgnoreCase("none") || line.equalsIgnoreCase("null") || line.isEmpty()){
                hologram.getLines().appendText(null);
                continue;
            }

            String nextLine = line.replace("{region.owner}", region.getOwner())
                    .replace("{current.endurance}", String.valueOf(region.getEndurance()))
                    .replace("{max.endurance}", String.valueOf(regionType.endurance()))
                    .replace("{region.size.render}", region.renderSize())
                    .replace("{region.id}", region.getId())
                    .replace("{region.x}", region.getX())
                    .replace("{region.y}", region.getY())
                    .replace("{region.z}", region.getZ())
                    .replace("{region.radius}", String.valueOf(region.getRegionType().radius()))
                    .replace("&", "§");

            hologram.getLines().appendText(nextLine);
        }
    }

    public void hide(@NonNull Region region) {
        Hologram hologram = region.getHologram();
        if (hologram == null) return;

        region.setHoloHidden(true);
        hologram.getLines().clear();
    }

}
