package dev.thew.regions.menu;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaticMenu implements InventoryHolder {
    Inventory inventory;
    HashMap<Integer, OnClick> onClickList = new HashMap<>();

    public StaticMenu(int size, String title) {
        inventory = Bukkit.createInventory(this, size, title);
    }

    public void setItem(int slot, ItemStack itemStack, OnClick onClick) {
        inventory.setItem(slot, itemStack);

        onClickList.put(slot, onClick);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @NonNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        OnClick onClick = onClickList.getOrDefault(slot, null);
        if (onClick == null) return;

        onClick.run(event);
    }
}
