package dev.thew.regions.databases.databases;

import lombok.SneakyThrows;
import dev.thew.regions.databases.Database;
import dev.thew.regions.model.Region;
import dev.thew.regions.handler.RegionHandler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RegionDatabase extends Database {

    private final RegionHandler regionsService;

    public RegionDatabase(RegionHandler regionsService, String url) {
        super(url);
        this.regionsService = regionsService;
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
            Region region = regionsService.fromJson(input.getString("data"));
            regions.add(region);
        }

        return regions;
    }

}