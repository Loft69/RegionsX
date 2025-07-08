package dev.thew.regions.handler.command.command.subcommands;

import dev.thew.regions.Regions;
import dev.thew.regions.handler.command.command.BaseCommand;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.utils.Message;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class AddSubCommand extends BaseCommand {
    public AddSubCommand(RegionHandler regionHandler) {
        super("add", regionHandler);
    }

    @Override
    public String description() {
        return " /base add §7ник §8- §fДобавить игрока в регион";
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

        Player addPlayer = Bukkit.getPlayerExact(nickname);

        if (addPlayer == null){
            Regions.sendError(player, Message.OFFLINE_PLAYER);
            return;
        }



        if (!region.isOwner(player.getName())){
            Regions.sendError(player, Message.NOT_YOUR_REGION);
            return;
        }

        if (region.isMember(nickname)){
            Regions.sendError(player, Message.PLAYER_ALREADY_IN_REGION);
            return;
        }

        region.addMember(nickname);
        player.sendMessage(Message.SUCCESS_ADD_REGION.replace("{player}", nickname));

    }
}
