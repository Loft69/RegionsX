package dev.thew.clans.handlers.message;

import dev.thew.clans.Clans;
import dev.thew.clans.model.Clan;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message {

    private String output = null;
    private Reason reason = null;

    public Message(String output){
        this.output = output;
    }

    public Message(Reason reason){
        this.reason = reason;
    }

    public void execute(@NonNull Player player, Clan clan, String... args){
        boolean isMessage = output != null;
        boolean isReason = reason != null;

        String playerMessage = "";

        if (isMessage)
            playerMessage = ChatColor.translateAlternateColorCodes('&', playerMessage);
        else if (isReason)
            playerMessage = ChatColor.translateAlternateColorCodes('&', reason.finallyText(Clans.getInstance().getConfig(), player, clan, args));


        boolean success = !playerMessage.isEmpty();
        if (success) player.sendMessage(playerMessage);
    }

    public static class Empty extends Message{
        public Empty() {
            super("");
        }
    }

}
