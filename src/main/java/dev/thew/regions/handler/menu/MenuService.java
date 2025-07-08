package dev.thew.regions.handler.menu;

import dev.thew.regions.Regions;
import dev.thew.regions.menu.RegionMenu;
import dev.thew.regions.menu.StaticMenu;
import dev.thew.regions.model.Region;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class MenuService implements Listener, MenuHandler {
    @Override
    public void load() {
        Regions instance = Regions.getInstance();
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @Override
    public void shutdown() {}

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof StaticMenu staticMenu) {

            event.setCancelled(true);

            if (event.getClickedInventory() == staticMenu.getInventory())
                staticMenu.onClick(event);
        }
    }

    @Override
    public void open(Player player, Region region) {
        RegionMenu regionMenu = new RegionMenu(region);
        regionMenu.open(player);
    }
}
