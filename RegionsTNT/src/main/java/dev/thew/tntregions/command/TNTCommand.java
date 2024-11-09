package dev.thew.tntregions.command;

import dev.thew.tntregions.model.CustomTNT;
import dev.thew.tntregions.service.TNTService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

@AllArgsConstructor
public class TNTCommand implements TabExecutor {

    private final TNTService tntService;

    @Override
    public boolean onCommand(@NonNull CommandSender sender,@NonNull Command command,@NonNull String s,String @NonNull [] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage(tntService.getCache().size() + " ");
                return true;
            }
            if (args.length != 1) return false;
            String name = args[0];
            CustomTNT customTNT = tntService.getCustomTNT(name);
            if (customTNT == null) {
                player.sendMessage(" Кастомный ТНТ не найден!");
                return false;
            }

            PlayerInventory playerInventory = player.getInventory();
            playerInventory.addItem(customTNT.getItem().clone()).forEach((x, y) -> player.getWorld().dropItem(player.getLocation(), y));
        } else {
            if (args.length != 2) return false;
            String playerName = args[0];
            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) {
                sender.sendMessage(" Игрок не найден!");
                return false;
            }

            String name = args[1];
            CustomTNT customTNT = tntService.getCustomTNT(name);
            if (customTNT == null) {
                player.sendMessage(" Кастомный ТНТ не найден!");
                return false;
            }

            PlayerInventory playerInventory = player.getInventory();
            playerInventory.addItem(customTNT.getItem().clone()).forEach((x, y) -> player.getWorld().dropItem(player.getLocation(), y));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender,@NonNull Command command,@NonNull String s, String @NonNull [] args) {
        if (!(sender instanceof Player)) return null;
        if (args.length == 1) return tntService.getIds();
        return null;
    }
}
