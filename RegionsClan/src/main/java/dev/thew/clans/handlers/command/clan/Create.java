package dev.thew.clans.handlers.command.clan;

import dev.thew.clans.handlers.command.SubCommand;
import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.model.Clan;
import org.bukkit.entity.Player;

public class Create extends SubCommand {
    public Create() {
        super("create");
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
