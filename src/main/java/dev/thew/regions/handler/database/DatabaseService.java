package dev.thew.regions.handler.database;

import dev.thew.regions.handler.regionType.RegionTypeHandler;
import dev.thew.regions.handler.settings.SettingsHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import dev.thew.regions.handler.database.databases.RegionDatabase;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.logging.Level;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class DatabaseService implements DatabaseHandler {
    HashMap<String, Database> databases = new HashMap<>();
    SettingsHandler settingsHandler;
    RegionTypeHandler regionTypeHandler;

    @SneakyThrows
    public void load() {
        String url = settingsHandler.getDatabaseURL();

        Database reputationDatabase = new RegionDatabase(regionTypeHandler, url);
        registerDatabase(reputationDatabase);
    }

    @SneakyThrows
    private void registerDatabase(Database databaseClass) {
        databases.put(databaseClass.getClass().getSimpleName(), databaseClass);

        Bukkit.getLogger().log(Level.INFO, "База данных успешно подключена");
    }

    @SneakyThrows
    public void shutdown() {
        databases.values().forEach(Database::close);
    }

    public <T extends Database> T getDatabase(Class<T> databaseClass) {

        String name = databaseClass.getSimpleName();

        if (!databases.containsKey(name))
            throw new RuntimeException("Database '" + name + "' not found!");

        Database database = databases.get(name);
        return databaseClass.cast(database);
    }


}
