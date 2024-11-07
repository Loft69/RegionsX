package dev.thew.regions.databases;

import lombok.SneakyThrows;
import dev.thew.regions.databases.databases.RegionDatabase;
import dev.thew.regions.handler.RegionHandler;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.logging.Level;

public class DatabaseManager {

    private static final HashMap<String, Database> databases = new HashMap<>();

    @SneakyThrows
    public static void load(RegionHandler service, String url) {
        Database reputationDatabase = new RegionDatabase(service, url);
        registerDatabase(reputationDatabase);
    }

    @SneakyThrows
    private static void registerDatabase(Database databaseClass) {
        databases.put(databaseClass.getClass().getSimpleName(), databaseClass);

        Bukkit.getLogger().log(Level.INFO, "База данных успешно подключена");
    }

    @SneakyThrows
    public static void shutDown() {

        for (Database database : databases.values())
            database.close();


        databases.clear();
    }

    public static <T extends Database> T getDatabase(Class<T> databaseClass) {

        String name = databaseClass.getSimpleName();

        if (!databases.containsKey(name))
            throw new RuntimeException("Database '" + name + "' not found!");

        Database database = databases.get(name);
        return databaseClass.cast(database);
    }
}
