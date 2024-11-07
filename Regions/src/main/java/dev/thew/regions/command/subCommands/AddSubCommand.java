package dev.thew.regions.command.subCommands;

import dev.thew.regions.Regions;
import dev.thew.regions.command.SubCommand;
import dev.thew.regions.handler.HandlerService;
import dev.thew.regions.handler.service.RegionService;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.RegionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AddSubCommand extends SubCommand {

    @Override
    public String description() {
        return " /base add §7ник §8- §fДобавить игрока в регион";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1){
            Regions.sendError(player, "Неверный синтаксис команды! Используйте §f/base help");
            return;
        }

        String nickname = args[0];

        if (nickname.equalsIgnoreCase(player.getName())){
            Regions.sendError(player, "Вы не можете добавлять самого себя");
            return;
        }

        Player addPlayer = Bukkit.getPlayerExact(nickname);

        if (addPlayer == null){
            Regions.sendError(player, "Игрок оффлайн");
            return;
        }

        RegionHandler regionsService = HandlerService.getHandler(RegionService.class);

        Region region = regionsService.getRegion(player.getLocation());

        if (region == null){
            Regions.sendError(player, "Вы должны стоять в своём регионе");
            return;
        }

        if (!region.isOwner(player.getName())){
            Regions.sendError(player, "Вы должны стоять в своём регионе");
            return;
        }

        if (region.isMember(nickname)){
            Regions.sendError(player, "Игрок уже в регионе");
            return;
        }

        region.addMember(nickname);
        player.sendMessage(" §fИгрок §x§c§9§f§c§6§0" + nickname + "§f добавлен в регион");

    }
}
