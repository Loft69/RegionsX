package dev.thew.regions.command;

import org.bukkit.entity.Player;

public abstract class SubCommand {

    public abstract String description();

    public abstract void execute(Player player, String[] args);

    public String getDescription(){
        return description();
    }

}
