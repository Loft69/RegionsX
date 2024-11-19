package dev.thew.clans.handlers;

import java.util.HashMap;

public class HandlerService implements Handler{
    private static final HashMap<String, Handler> handlers = new HashMap<>();

    public void load() {

        loadHandler();
    }

    public void shutdown() {
        for (Handler handler : handlers.values()) handler.shutdown();
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
