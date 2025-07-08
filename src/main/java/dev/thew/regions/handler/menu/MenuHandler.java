package dev.thew.regions.handler.menu;

import dev.thew.regions.handler.Handler;
import dev.thew.regions.model.Region;
import org.bukkit.entity.Player;

public interface MenuHandler extends Handler {
    void open(Player player, Region region);
}
