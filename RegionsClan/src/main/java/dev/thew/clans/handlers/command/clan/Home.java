package dev.thew.clans.handlers.command.clan;

import dev.thew.clans.handlers.command.SubCommand;
import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.model.Access;
import dev.thew.clans.model.Clan;
import org.bukkit.entity.Player;

import java.util.List;

public class Home extends SubCommand {
    public Home() {
        super("home", true, List.of(Access.MANAGER, Access.OWNER, Access.MEMBER));
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Message execute(Player player, Clan clan, String[] args) {

        return null;
    }
}
