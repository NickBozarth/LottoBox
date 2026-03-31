package com.tinnyspoon.lottobox.utils;

import org.jetbrains.annotations.Nullable;

public class ParseName {
    public static @Nullable String parseCrateName(String args[]) {
        if (args.length == 0) return null;

        String crateName = "";
        for (String arg : args) {
            crateName += arg + " ";
        }
        crateName = crateName.strip();
        return crateName;
    }
}
