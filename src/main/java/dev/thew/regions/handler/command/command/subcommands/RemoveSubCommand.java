package dev.thew.regions.handler.command.command.subcommands;

import dev.thew.regions.Regions;
import dev.thew.regions.handler.command.command.BaseCommand;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.utils.Message;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class RemoveSubCommand extends BaseCommand {

    public RemoveSubCommand(RegionHandler regionHandler) {
        super("remove", regionHandler);
    }

    @Override
    public String description() {
        return " /base remove §7ник §8- §fУдалить игрока из региона";
    }

    @Override
    public void execute(@NonNull Player player, Region region, String[] args) {
        if (args.length != 1){
            Regions.sendError(player, Message.ILLEGAL_SYNTAX);
            return;
        }

        if (region == null){
            Regions.sendError(player, Message.NOT_YOUR_REGION);
            return;
        }

        if (region.getRegionType().isClanType()) return;
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
