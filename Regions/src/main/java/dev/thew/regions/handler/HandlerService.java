package dev.thew.regions.handler;

import dev.thew.regions.Regions;
import dev.thew.regions.databases.DatabaseManager;
import dev.thew.regions.handler.service.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;

public class HandlerService {

    private static final HashMap<String, Handler> handlers = new HashMap<>();

    public void load() {
        if (!new File(Regions.getInstance().getDataFolder(), "config.yml").exists()) Regions.getInstance().saveDefaultConfig();
        FileConfiguration config = Regions.getInstance().getConfig();

        RegionTypeHandler regionTypeHandler = new RegionTypeService();
        addHandler(regionTypeHandler);

        RegionHandler regionHandler = new RegionService(regionTypeHandler);
        addHandler(regionHandler);

        Handler hologramHandler = new HologramService();
        addHandler(hologramHandler);

        String url = config.getString("database-url");
        DatabaseManager.load(regionHandler, url);

        MenuHandler menuHandler = new MenuService();
        addHandler(menuHandler);

        Handler bypassHandler = new BypassService();
        addHandler(bypassHandler);

        Handler blockObserverHandler = new BlocksObserverService(regionHandler, regionTypeHandler, menuHandler);
        addHandler(blockObserverHandler);

        Handler protectionHandler = new ProtectionService(regionHandler);
        addHandler(protectionHandler);

        Handler visitorsHandler = new VisitorsService(regionHandler);
        addHandler(visitorsHandler);

        loadHandler();
    }

    public void shutdown() {
        for (Handler handler : handlers.values()) handler.shutdown();
        DatabaseManager.shutDown();
    }

    private void loadHandler() {
        for (Handler handler : handlers.values()) handler.load();
    }

    public static <T extends Handler> T getHandler(Class<T> handlerClass) {
        String name = handlerClass.getSimpleName();

        Handler handler = handlers.get(name);
        return handlerClass.cast(handler);
    }

    public void addHandler(Handler handler) {
        handlers.put(handler.getClass().getSimpleName(), handler);
    }

}
