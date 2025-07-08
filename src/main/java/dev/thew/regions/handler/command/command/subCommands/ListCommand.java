package dev.thew.regions.handler.command.command.subCommands;

import dev.thew.regions.Regions;
import dev.thew.regions.handler.command.command.BaseCommand;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.utils.Message;
import dev.thew.regions.utils.TextUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;

public final class ListCommand extends BaseCommand {

    public ListCommand(RegionHandler regionHandler) {
        super("list", regionHandler);
    }

    @Override
    public String description() {
        return " /base list §8- §fСписок регионов";
    }

    @Override
    public void execute(@NonNull Player player, Region region, String[] args) {

        String targetName = player.getName();

        if (args.length == 1) {

            if (!player.hasPermission("regions.command.list.others")) {
                Regions.sendError(player, Message.CANT_LIST_OTHER_REGIONS);
                return;
            }

            targetName = args[0];
        }

        List<Region> playerRegions = getPlayerRegions(targetName);
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
        for (Region rg : playerRegions) {

            boolean isOwner = rg.isOwner(targetName);

            String roleView = isOwner ? "Владелец" : "Участник";
            String locationView = TextUtils.renderLocation(rg.getBaseLocation());
            String typeView = rg.renderSize();

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
