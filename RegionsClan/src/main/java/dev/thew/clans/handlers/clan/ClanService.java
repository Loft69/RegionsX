package dev.thew.clans.handlers.clan;

import dev.thew.clans.model.Clan;
import dev.thew.clans.model.Result;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClanService implements ClanHandler{

    private final List<Clan> clans = new ArrayList<>();

    @Override
    public void load() {

    }

    @Override
    public Clan getClan(Player player){
        for (Clan clan : clans)
            if (clan.contains(player.getName()) == Result.SUCCESS) return clan;

        return null;
    }



    @Override
    public void shutdown() {

    }


}
