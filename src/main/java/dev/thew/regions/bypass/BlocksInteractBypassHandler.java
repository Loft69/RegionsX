package dev.thew.regions.bypass;

import dev.thew.regions.model.BypassHandler;
import dev.thew.regions.model.BypassResult;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlocksInteractBypassHandler implements BypassHandler {
    List<Material> blockedMaterial = new ArrayList<>();

    public BlocksInteractBypassHandler() {
        blockedMaterial.add(Material.CHEST);
        blockedMaterial.add(Material.BARREL);
        blockedMaterial.add(Material.FURNACE);
        blockedMaterial.add(Material.BLAST_FURNACE);
        blockedMaterial.add(Material.HOPPER);
        blockedMaterial.add(Material.TRAPPED_CHEST);
        blockedMaterial.add(Material.COMPARATOR);
        blockedMaterial.add(Material.REPEATER);
        blockedMaterial.add(Material.ITEM_FRAME);
    }

    @Override
    public BypassResult handle(Event rawEvent) {
        if (!(rawEvent instanceof PlayerInteractEvent event)) return BypassResult.IGNORE;

        Block block = event.getClickedBlock();
        if (block == null) return BypassResult.IGNORE;

        Material material = block.getType();
        String materialString = material.toString().toLowerCase();

        boolean blockedMaterial = this.blockedMaterial.contains(material);

        if (materialString.contains("door") || materialString.contains("gate") || materialString.contains("trapdoor")) return BypassResult.CANCEL;
        if (blockedMaterial) return BypassResult.CANCEL;

        return BypassResult.PASS;
    }
}
