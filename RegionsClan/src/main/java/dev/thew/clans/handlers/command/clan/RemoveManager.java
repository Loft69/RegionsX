package dev.thew.clans.handlers.command.clan;

import dev.thew.clans.handlers.command.SubCommand;
import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.model.Access;
import dev.thew.clans.model.Clan;
import dev.thew.clans.model.Result;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;

public class RemoveManager extends SubCommand {
    public RemoveManager() {
        super("removeManager", true, List.of(Access.OWNER));
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Message execute(@NonNull Player player, @NonNull Clan clan, String[] args) {
        if (args.length != 1) return new Message("Неправильный синтаксис!");

        String playerName = args[0];
        if (clan.isManager(playerName) == Result.FAILED) return new Message(" Игрок не является менеджером!");

        Result removeResult = clan.removeManager(playerName);
        Result addResult = clan.addMember(playerName);
        if (removeResult == Result.FAILED || addResult == Result.FAILED) return new Message(" Возникла ошибка, обратитесь к администратору");
        return new Message(" Игрок успешно понижен до участника");
    }
}
