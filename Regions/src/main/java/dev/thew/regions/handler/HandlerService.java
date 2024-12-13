package dev.thew.regions.handler;

import dev.thew.regions.Regions;
import dev.thew.regions.handler.database.DatabaseHandler;
import dev.thew.regions.handler.database.DatabaseService;
import dev.thew.regions.handler.bypass.BypassService;
import dev.thew.regions.handler.command.CommandService;
import dev.thew.regions.handler.hologram.HologramService;
import dev.thew.regions.handler.menu.MenuHandler;
import dev.thew.regions.handler.menu.MenuService;
import dev.thew.regions.handler.observer.BlocksObserverService;
import dev.thew.regions.handler.protect.ProtectionService;
import dev.thew.regions.handler.region.RegionHandler;
import dev.thew.regions.handler.region.RegionService;
import dev.thew.regions.handler.regionType.RegionTypeHandler;
import dev.thew.regions.handler.regionType.RegionTypeService;
import dev.thew.regions.handler.settings.SettingsHandler;
import dev.thew.regions.handler.settings.SettingsService;
import dev.thew.regions.handler.visit.VisitorsService;

import java.util.HashMap;

public class HandlerService {

    private static final HashMap<String, Handler> handlers = new HashMap<>();

    public void load(Regions instance) {
        SettingsHandler settingsHandler = new SettingsService(instance);
        addHandler(settingsHandler);

        RegionTypeHandler regionTypeHandler = new RegionTypeService();
        addHandler(regionTypeHandler);

        DatabaseHandler databaseHandler = new DatabaseService(settingsHandler, regionTypeHandler);
        databaseHandler.load();

        RegionHandler regionHandler = new RegionService(databaseHandler);
        addHandler(regionHandler);

        Handler hologramHandler = new HologramService();
        addHandler(hologramHandler);

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

        Handler commandHandler = new CommandService(instance, regionHandler, regionTypeHandler);
        addHandler(commandHandler);

        loadHandlers();
    }

    public void shutdown() {
        for (Handler handler : handlers.values()) handler.shutdown();
    }

    private void loadHandlers() {
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
