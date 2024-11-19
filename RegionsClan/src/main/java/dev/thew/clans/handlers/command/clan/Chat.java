package dev.thew.clans.handlers.command.clan;

import dev.thew.clans.handlers.command.SubCommand;
import org.bukkit.entity.Player;

public class Chat extends SubCommand {

    public Chat(){
        super("chat");
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public void execute(Player player, String[] args) {

    }
}
