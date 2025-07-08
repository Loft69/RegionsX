package dev.thew.regions.utils;

public class Message {
    public static String COMMAND_NOT_FOUND;
    public static String ILLEGAL_SYNTAX;
    public static String CANT_ADD_YOURSELF;
    public static String OFFLINE_PLAYER;
    public static String NOT_YOUR_REGION;
    public static String PLAYER_ALREADY_IN_REGION;
    public static String SUCCESS_ADD_REGION;
    public static String CANT_LIST_OTHER_REGIONS;
    public static String PLAYER_NOT_MEMBER;
    public static String SUCCESS_REMOVE_FROM_REGION;
    public static String U_NOT_OWNER;
    public static String U_NOT_MEMBER;
    public static String REGIONS_CROSSING;
    public static String ERROR_PREFIX;

    static {
        ILLEGAL_SYNTAX = ConfigReader.get("illegal-syntax");
        CANT_ADD_YOURSELF = ConfigReader.get("cant-add-yourself");
        OFFLINE_PLAYER = ConfigReader.get("offline-player");
        NOT_YOUR_REGION = ConfigReader.get("not-your-region");
        PLAYER_ALREADY_IN_REGION = ConfigReader.get("player-already-in-region");
        SUCCESS_ADD_REGION = ConfigReader.get("success-add-region");
        CANT_LIST_OTHER_REGIONS = ConfigReader.get("can-list-other-regions");
        PLAYER_NOT_MEMBER = ConfigReader.get("player-not-member");
        SUCCESS_REMOVE_FROM_REGION = ConfigReader.get("success-remove-region");
        COMMAND_NOT_FOUND = ConfigReader.get("command-not-found");
        U_NOT_OWNER = ConfigReader.get("u-not-owner");
        U_NOT_MEMBER = ConfigReader.get("u-not-member");
        REGIONS_CROSSING = ConfigReader.get("regions-crossing");
        ERROR_PREFIX = ConfigReader.get("error-prefix");
    }
}