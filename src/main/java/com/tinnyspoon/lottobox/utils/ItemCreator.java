package com.tinnyspoon.lottobox.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public class ItemCreator {
    public static ItemStack itemStackWithName(Material mat, String displayName) {
        ItemStack item = new ItemStack(mat);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;
        itemMeta.setDisplayName(displayName);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getFillItem() {
        return ItemCreator.itemStackWithName(Material.BLACK_STAINED_GLASS_PANE, " ");
    }

    public static void setLore(ItemStack item, List<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
    }

    public static void addLore(ItemStack item, List<String> lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;
        List<String> oldLore = itemMeta.getLore();
        if (oldLore == null) oldLore = new ArrayList<>();
        else oldLore.add(" ");
        oldLore.addAll(lore);

        itemMeta.setLore(oldLore);
        item.setItemMeta(itemMeta);
    }

    public static void setArrowType(ItemStack item, PotionType type) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null || !(itemMeta instanceof PotionMeta potionMeta)) return;
        potionMeta.setBasePotionType(type);
        potionMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        potionMeta.setDisplayName(itemMeta.getItemName());
        item.setItemMeta(potionMeta);
    }
}
