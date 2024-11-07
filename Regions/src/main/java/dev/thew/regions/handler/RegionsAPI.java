package dev.thew.regions.handler;

public interface RegionsAPI {

    static <T extends Handler> T getHandler(Class<T> handlerClass) { // API METHOD
        Handler handler = HandlerService.getHandler(handlerClass);
        return handlerClass.cast(handler);
    }

}
