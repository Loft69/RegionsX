package dev.thew.clans.handlers.command.clan;

import dev.thew.clans.handlers.command.SubCommand;
import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.model.Access;
import dev.thew.clans.model.Clan;
import lombok.NonNull;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.awt.font.FontRenderContext;
import java.util.List;

public class Rename extends SubCommand {

    public Rename() {
        super("rename", true, List.of(Access.OWNER));
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Message execute(@NonNull Player player, @NonNull Clan clan, String[] args) {
        if (args.length == 0) return new Message("Неправильный синтаксис!");

        StringBuilder newNameBuilder = new StringBuilder();
        for (String s : args) newNameBuilder.append(s).append(" ");

        String newName = newNameBuilder.toString().trim();
        clan.setName(newName);
        return new Message(" Название успешно изменено");
    }
}
