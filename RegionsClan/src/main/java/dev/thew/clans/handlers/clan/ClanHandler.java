package dev.thew.clans.handlers.clan;

import dev.thew.clans.handlers.Handler;
import dev.thew.clans.model.Clan;
import org.bukkit.entity.Player;

public interface ClanHandler extends Handler {
    Clan getClan(Player player);
}
