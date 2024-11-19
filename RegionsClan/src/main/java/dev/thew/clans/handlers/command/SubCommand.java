package dev.thew.clans.handlers.command;

import dev.thew.clans.handlers.message.Message;
import dev.thew.clans.model.Access;
import dev.thew.clans.model.Clan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.PrimitiveIterator;


@AllArgsConstructor
public abstract class SubCommand {

    @Getter
    private final String sub;
    @Getter
    private final boolean clanRequired;
    private final List<Access> accesses;

    public abstract String description();

    public abstract Message execute(Player player, Clan clan, String[] args);

    public String getDescription(){
        return description();
    }

    public boolean accessContains(Access access){
        return accesses.contains(access);
    }

}
