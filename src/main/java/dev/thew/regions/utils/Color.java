package dev.thew.regions.utils;

import lombok.NonNull;

public class Color {

    public static @NonNull String apply(@NonNull String input) {
        return input.replace("&", "§");
    }
}