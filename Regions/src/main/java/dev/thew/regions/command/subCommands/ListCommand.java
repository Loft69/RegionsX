package dev.thew.regions.command.subCommands;

import dev.thew.regions.Regions;
import dev.thew.regions.command.SubCommand;
import dev.thew.regions.handler.HandlerService;
import dev.thew.regions.handler.service.RegionService;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.RegionHandler;
import dev.thew.regions.utils.TextUtils;
import org.bukkit.entity.Player;

import java.util.List;

public class ListCommand extends SubCommand {

    @Override
    public String description() {
        return " /base list §8- §fСписок регионов";
    }

    @Override
    public void execute(Player player, String[] args) {

        String targetName = player.getName();

        if (args.length == 1) {

            if (!player.hasPermission("regions.command.list.others")) {
                Regions.sendError(player, "У вас нет прав для просмотра чужих приватов");
                return;
            }

            targetName = args[0];
        }

        RegionHandler regionsService = HandlerService.getHandler(RegionService.class);
        List<Region> playerRegions = regionsService.getPlayerRegions(targetName);

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(" ").append("\n");
        messageBuilder.append(" §fПриваты игрока §e").append(targetName).append(" §x§c§9§f§c§6§0↓§f").append("\n");
        messageBuilder.append(" ").append("\n");

        if (playerRegions.isEmpty()){
            messageBuilder.append(" §eТут пусто :(");
            player.sendMessage(messageBuilder.toString());
            return;
        }

        int i = 1;
        for (Region region : playerRegions) {

            boolean isOwner = region.isOwner(targetName);

            String roleView = isOwner ? "Владелец" : "Участник";
            String locationView = TextUtils.renderLocation(region.getBaseLocation());
            String typeView = region.renderSize();

            messageBuilder
                    .append(" §x§c§9§f§c§6§0").append(i++).append(".").append("\n")
                    .append("   §fРазмер ").append(typeView).append("\n")
                    .append("   §fЛокация §7").append(locationView).append("\n")
                    .append("   §fРоль §7").append(roleView).append("\n")
                    .append(" \n");
        }

        messageBuilder.append(" ").append("\n");
        player.sendMessage(messageBuilder.toString());
    }
}
