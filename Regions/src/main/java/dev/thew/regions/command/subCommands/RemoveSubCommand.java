package dev.thew.regions.command.subCommands;

import dev.thew.regions.Regions;
import dev.thew.regions.command.SubCommand;
import dev.thew.regions.handler.HandlerService;
import dev.thew.regions.handler.service.RegionService;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.RegionHandler;
import dev.thew.regions.utils.Message;
import org.bukkit.entity.Player;

public class RemoveSubCommand extends SubCommand {

    @Override
    public String description() {
        return " /base remove §7ник §8- §fУдалить игрока из региона";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1){
            Regions.sendError(player, Message.ILLEGAL_SYNTAX);
            return;
        }

        RegionHandler regionsService = HandlerService.getHandler(RegionService.class);

        Region region = regionsService.getRegion(player.getLocation());

        if (region == null){
            Regions.sendError(player, Message.NOT_YOUR_REGION);
            return;
        }

        if (!region.getRegionType().useCommand()) return;
        String nickname = args[0];

        if (nickname.equalsIgnoreCase(player.getName())){
            Regions.sendError(player, Message.CANT_ADD_YOURSELF);
            return;
        }

        if (!region.isOwner(player.getName())){
            Regions.sendError(player, Message.NOT_YOUR_REGION);
            return;
        }

        if (!region.isMember(nickname)){
            Regions.sendError(player, Message.PLAYER_NOT_MEMBER);
            return;
        }

        region.removeMember(nickname);
        player.sendMessage(Message.SUCCESS_REMOVE_FROM_REGION);
    }
}
