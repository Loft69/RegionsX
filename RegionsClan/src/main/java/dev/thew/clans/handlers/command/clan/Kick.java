package dev.thew.clans.handlers.command.clan;

import dev.thew.clans.handlers.command.SubCommand;
import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.model.Access;
import dev.thew.clans.model.Clan;
import dev.thew.clans.model.Result;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;

public class Kick extends SubCommand {
    public Kick() {
        super("kick", true, List.of(Access.MANAGER, Access.OWNER));
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Message execute(@NonNull Player player, @NonNull Clan clan, String[] args) {
        if (args.length == 0) return new Message("Неправильный синтаксис!");

        String playerName = args[0];
        if (player.getName().equalsIgnoreCase(playerName)) return new Message(" Вы не можете исключить себя");

        boolean isManager = clan.isManager(playerName) == Result.SUCCESS;
        if (isManager && clan.isManager(player.getName()) == Result.SUCCESS) return new Message("Вы не можете исключать менеджеров");

        clan.kick(playerName);
        return new Message(" Игрок успешно исключен");
    }

}
