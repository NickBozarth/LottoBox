package com.tinnyspoon.lottobox.loot;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.ParseName;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class LootItem {
    public static String separator = "----------------";

    public @NotNull String itemName;
    public @NotNull String crateName;
    public @NotNull ItemStack displayItem;
    public int index;
    public int weight;
    public @NotNull List<Object> winItems = new ArrayList<>();
    public @NotNull List<String> winCommands = new ArrayList<>();
    private boolean disableWinMessage;

    public static @Nullable LootItem loadItemFromEditItem(ItemStack editItem) {
        if (editItem == null) return null;

        String crateName = PersistentData.getItemString(editItem, "crate-name");
        Integer itemIndex = PersistentData.getItemData(editItem, "item-index", PersistentDataType.INTEGER);
        if (crateName == null || itemIndex == null) return null;

        List<Map<?, ?>> items = Configs.cratesConfig.config.getMapList("Crates." + crateName + ".items");
        Map<?, ?> itemMap;
        try { itemMap = items.get(itemIndex); }
        catch (IndexOutOfBoundsException e) { return null; }

        return loadItemFromMap(crateName, itemMap, itemIndex);
    }

    public static @Nullable LootItem loadItemFromMap(String crateName, Map<?, ?> itemMap, int index) {
        LootItem item = new LootItem();
        item.crateName = crateName;
        item.index = index;
        
        
        Object itemNameObject = itemMap.get("name");
        if (itemNameObject != null && itemNameObject instanceof String itemName) {
            item.itemName = itemName;
        } else {
            item.itemName = "Item name not found";
        }

        Object displayItemObject = itemMap.get("display-item");
        if (displayItemObject == null || !(displayItemObject instanceof ItemStack displayItem) || displayItem.getItemMeta() == null) {
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
        item.winItems.addAll(winItems);

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

        item.disableWinMessage = itemMap.containsKey("disable-win-message");
        


        return item;
    }

    private void winItem(Player player) {
        for (Object itemObject : this.winItems) {
            if (itemObject instanceof ItemStack winItem) {
                player.getInventory().addItem(winItem);
            } 
            else if (itemObject instanceof String winItemString && winItemString.equals("display-item")) {
                player.getInventory().addItem(this.displayItem);
            } 
            else {
                Bukkit.getLogger().log(Level.SEVERE, "[" + this.crateName + "." + this.itemName + "] contains win item that is unable to be loaded");
            }
        }
    }


    private void winCommands(Player player) {
        for (String command : this.winCommands) {
            command = command.replace("<player>", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }



    public void win(Player player) {
        if (!this.disableWinMessage) player.sendMessage("You won [" + this.itemName + "]");

        winItem(player);
        winCommands(player);
    }


    private @Nullable String getWinItemString(Object winItem) {
        Bukkit.broadcastMessage(this.itemName);

        if (winItem instanceof String itemString && itemString.equals("display-item")) {
            return "- Display Item";
        }
        if (winItem instanceof ItemStack itemStack) {
            return "- " + ParseName.getItemStackName(itemStack);
        }

        return null;
    }


    public ItemStack getEditItem(int totalWeight) {
        ItemStack editItem = this.displayItem.clone();
        ItemMeta editItemMeta = editItem.getItemMeta();
        List<String> editItemLore = editItemMeta.getLore();
        if (editItemLore == null) editItemLore = new ArrayList<>();
        else editItemLore.add("");
        editItemLore.add(LootItem.separator);

        List<String> winItemStrings = this.winItems.stream()
            .map(item -> this.getWinItemString(item))
            // .filter(item -> item != null)
            .map(item -> (item != null) ? item : "NULL")
            .toList();

        if (winItemStrings.size() != 0) {
            editItemLore.add("Gives Items:");
            editItemLore.addAll(winItemStrings);
            editItemLore.add("");
        }

        if (this.winCommands.size() != 0) {
            editItemLore.add("Runs Commands:");
            List<String> winCommandStrings = this.winCommands.stream()
                .map(command -> "- " + command)
                .toList();
            editItemLore.addAll(winCommandStrings);
            editItemLore.add("");
        }

        editItemLore.add("Diable Win Message: " + Boolean.toString(this.disableWinMessage));
        editItemLore.add(LootItem.separator);
        editItemMeta.setLore(editItemLore);
        editItem.setItemMeta(editItemMeta);
        PersistentData.setItemData(editItem, "item-index", this.index, PersistentDataType.INTEGER);
        PersistentData.setItemString(editItem, "crate-name", this.crateName);

        return editItem;
    }

    public List<ItemStack> getWinItemStacks(int beginOffset) {
        AtomicInteger index = new AtomicInteger();

        return this.winItems.stream()
            .skip(beginOffset)
            .map(item -> {
                if (item instanceof ItemStack itemStack) return itemStack.clone();
                else if (item instanceof String itemString && itemString.equals("display-item")) return this.displayItem.clone();
                return null;
            })
            .filter(item -> item != null)
            .map(item -> { 
                PersistentData.setItemString(item, "type", "Item"); 
                PersistentData.setItemData(item, "index", index.getAndIncrement(), PersistentDataType.INTEGER);         
                return item; 
            })
            .toList();
    }

    public Map<?, ?> getAsMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.itemName);
        map.put("weight", this.weight);

        Map<String, Object> winMap = new HashMap<>();
        winMap.put("items", this.winItems);
        winMap.put("commands", this.winCommands);

        map.put("win", winMap);

        map.put("display-item", this.displayItem);

        return map;
    }
}
