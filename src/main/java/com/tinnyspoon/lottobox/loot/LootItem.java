package com.tinnyspoon.lottobox.loot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;

public class LootItem {


    public @NotNull String itemName;
    public @NotNull ItemStack displayItem;
    public int weight;
    public @NotNull List<ItemStack> winItems = new ArrayList<>();
    public @NotNull List<String> winCommands = new ArrayList<>();

    public static @Nullable LootItem loadItem(String crateName, Map<?, ?> itemMap) {
        LootItem item = new LootItem();
        
        
        Object itemNameObject = itemMap.get("name");
        if (itemNameObject != null && itemNameObject instanceof String itemName) {
            item.itemName = itemName;
        } else {
            item.itemName = "Item name not found";
        }

        Object displayItemObject = itemMap.get("display-item");
        if (displayItemObject == null || !(displayItemObject instanceof ItemStack displayItem)) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to retrieve [" + crateName + "] [" + item.itemName + "].display-item");
            return null;
        }
        item.displayItem = displayItem;

        Object weightObject = itemMap.get("weight");
        if (weightObject == null || !(weightObject instanceof Integer weight)) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to retrieve [" + crateName + "] [" + item.itemName + "].weight");
            return null;
        }
        item.weight = weight;

        Object winObject = itemMap.get("win");
        if (winObject == null || !(winObject instanceof Map<?, ?> winMap)) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to retrieve [" + crateName + "] [" + item.itemName + "].win");
            return null;
        }

        Object winItemsObject = winMap.get("items");
        if (winItemsObject == null || !(winItemsObject instanceof List<?> winItems)) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to retrieve [" + crateName + "] [" + item.itemName + "].win.items");
            return null;
        }

        for (Object winItemObject : winItems) {
            if (winItemObject instanceof ItemStack winItem) {
                item.winItems.add(winItem);
            } 
            else if (winItemObject instanceof String winItemString && winItemString.equals("display-item")) {
                item.winItems.add(displayItem);
            } 
            else {
                Bukkit.getLogger().log(Level.SEVERE, "[" + crateName + "] contains win item that is unable to be loaded");
            }
        }

        Object winCommandsObject = winMap.get("commands");
        if (winCommandsObject == null || !(winCommandsObject instanceof List<?> winCommands)) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to retrieve [" + crateName + "] [" + item.itemName + "].win.items");
            return null;
        }

        for (Object winCommandObject : winCommands) {
            if (winCommandObject instanceof String winCommand) {
                item.winCommands.add(winCommand);
            } 
            else {
                Bukkit.getLogger().log(Level.SEVERE, "[" + crateName + "] contains win command that is unable to be loaded");
            }
        }
        


        return item;
    }


    private void winItem(Player player) {
    }


    private void winCommands(Player player) {
    }



    public void win(Player player) {
        winItem(player);
        winCommands(player);
    }
}
