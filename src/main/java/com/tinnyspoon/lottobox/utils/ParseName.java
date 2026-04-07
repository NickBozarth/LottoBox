package com.tinnyspoon.lottobox.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
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





    private static @Nullable ItemStack materialToFillItem(@Nullable Material material) {
        if (material == null) return null;

        ItemStack fillItem = new ItemStack(material, 1);
        ItemMeta fillItemMeta = fillItem.getItemMeta();
        if (fillItemMeta == null) return fillItem;
        fillItemMeta.setItemName("");
        fillItem.setItemMeta(fillItemMeta);
        return fillItem;
    }

    public static @Nullable ItemStack stringToFillerItem(@Nullable String materialName) {
        if (materialName == null) return null;

        Material fillItemMaterial;
        try {
            fillItemMaterial = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }

        return ParseName.materialToFillItem(fillItemMaterial);
    }

    public static ItemStack stringToFillerItem(@Nullable String materialName, Material defaultMaterial) {
        ItemStack fillerItem = ParseName.stringToFillerItem(materialName);
        return (fillerItem != null) ? fillerItem : ParseName.materialToFillItem(defaultMaterial);
    }
}
