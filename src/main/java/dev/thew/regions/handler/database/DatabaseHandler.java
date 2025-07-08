package dev.thew.regions.handler.database;

import dev.thew.regions.handler.Handler;

public interface DatabaseHandler extends Handler {
    <T extends Database> T getDatabase(Class<T> databaseClass);
}
