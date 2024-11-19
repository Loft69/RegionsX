package dev.thew.clans.handlers.command.clan;

import dev.thew.clans.handlers.command.SubCommand;
import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.model.Access;
import dev.thew.clans.model.Clan;
import dev.thew.clans.model.Result;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class Sethome extends SubCommand {

    public Sethome() {
        super("sethome", true, List.of(Access.MANAGER, Access.OWNER));
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Message execute(@NonNull Player player, @NonNull Clan clan, String[] args) {
        if (args.length > 0) return new Message("Неправильный синтаксис!");

        Location homeLocation = player.getLocation();
        Result result = clan.setHomeLocation(homeLocation);
        if (result == Result.FAILED) {
            return new Message(" Основную локацию для клана можно менять не быстрее, чем каждые 30 минут");
        }

        return new Message(" Локация успешно изменена!");
    }
}
