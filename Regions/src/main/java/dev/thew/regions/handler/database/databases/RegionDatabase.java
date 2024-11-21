package dev.thew.regions.handler.database.databases;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.thew.regions.handler.regionType.RegionTypeHandler;
import dev.thew.regions.model.RegionType;
import lombok.SneakyThrows;
import dev.thew.regions.handler.database.Database;
import dev.thew.regions.model.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class RegionDatabase extends Database {

    private final RegionTypeHandler regionTypeHandler;

    public RegionDatabase(RegionTypeHandler regionTypeHandler, String url) {
        super(url);
        this.regionTypeHandler = regionTypeHandler;
    }

    @Override
    public void checkTables() {
        push("CREATE TABLE IF NOT EXISTS regions.regions(id varchar(256), data text, UNIQUE(id));", true);
    }

    public void save(Region region) {
        push("INSERT INTO regions(id, data) VALUES(?, ?) ON DUPLICATE KEY UPDATE data = ?;", false, region.getId(), region.toJson(), region.toJson());
    }

    public void delete(Region region) {
        push("DELETE FROM regions WHERE id=?;", true, region.getId());
    }

    @SneakyThrows
    public List<Region> startLoad(){
        List<Region> regions = new ArrayList<>();
        ResultSet input = pushWithReturn("SELECT * FROM regions;");
        if (input == null) return regions;

        while (input.next()) {
            Region region = fromJson(input.getString("data"));
            regions.add(region);
        }

        return regions;
    }

    public Region fromJson(String json) {

        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();

        Location location = getLocationFromJSON(jsonObject.get("id").getAsString());

        String regionTypeId = jsonObject.get("regionTypeId").getAsString();
        RegionType regionType = regionTypeHandler.getType(regionTypeId);

        String owner = jsonObject.get("owner").getAsString();
        int endurance = jsonObject.get("endurance").getAsInt();
        String id = jsonObject.get("id").getAsString();

        JsonArray members = jsonObject.get("members").getAsJsonArray();
        boolean holoHidden = jsonObject.get("holoHidden").getAsBoolean();

        List<String> memberList = new ArrayList<>();
        for (JsonElement jsonElement : members) memberList.add(jsonElement.getAsString());

        return new Region(id, location, regionType, owner, null, endurance, memberList, holoHidden);
    }

    private Location getLocationFromJSON(String raw) {
        String[] split = raw.split(":");
        String world = split[0];
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[2]);
        int z = Integer.parseInt(split[3]);
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

}