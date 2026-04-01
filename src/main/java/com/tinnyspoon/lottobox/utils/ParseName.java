package com.tinnyspoon.lottobox.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    private static String capitalizeWords(String str) {
        return Arrays.stream(str.split("\\s+"))
                 .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                 .collect(Collectors.joining(" "));
    }

    public static @Nullable String getItemStackName(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return null;

        if (itemMeta.hasDisplayName()) {
            return itemMeta.getDisplayName();
        } else {
            return capitalizeWords(item.getType().name().replace('_', ' '));
        }
    }
}
