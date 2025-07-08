package dev.thew.regions.handler;

public class RegionsAPI {
    public static <T extends Handler> T getHandler(Class<T> handlerClass){
        return HandlerService.getHandler(handlerClass);
    }
}
