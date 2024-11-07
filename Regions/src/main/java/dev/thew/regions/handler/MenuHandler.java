package dev.thew.regions.handler;

import dev.thew.regions.model.Region;
import org.bukkit.entity.Player;

public interface MenuHandler extends Handler {

    void open(Player player, Region region);

}
