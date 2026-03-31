package com.tinnyspoon.lottobox.loot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

public class LootItem {
    private static File cratesConfigFile;
    private static FileConfiguration cratesConfig;

    public static void setDataFolder(File dataFolder) {
        LootItem.cratesConfigFile = new File(dataFolder, "crates.yml");
        LootItem.cratesConfig = YamlConfiguration.loadConfiguration(cratesConfigFile);
    }


    private ConfigurationSection itemSection;
    public String itemName;
    public @NotNull Material displayItem;
    public @NotNull int weight;

    public static @Nullable LootItem loadItem(String crateName, String itemName) {
        LootItem item = new LootItem();
        item.itemName = itemName;

        

        item.itemSection = LootItem.cratesConfig.getConfigurationSection(crateName + ".items." + itemName);
        if (item.itemSection == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to fetch crates.yml [" + crateName + ".items." + itemName + "]");

            for (String key : LootItem.cratesConfig.getKeys(true)) {
                Bukkit.getLogger().log(Level.SEVERE, key);
            }

            return null;
        }


        try {
            item.displayItem = Material.valueOf(item.itemSection.getString("display-item", "DIRT"));
        } catch (IllegalArgumentException e) {
            item.displayItem = Material.DIRT;
        }

        item.weight = item.itemSection.getInt("weight", 0);



        return item;
    }


    private void winItem(Player player) {
        String itemString = this.itemSection.getString("item.name");
        if (itemString == null) return;
        int itemQuantity = this.itemSection.getInt("item.quantity", 1);

        try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "give " + player.getName() + " " + itemString + " " + itemQuantity);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create item [" + itemString + "]");
        }
    }


    private void winCommands(Player player) {
        ArrayList<String> commands = new ArrayList<>();
        
        String commandString = this.itemSection.getString("command");
        if (commandString != null) commands.add(commandString);

        List<String> commandsList = this.itemSection.getStringList("commands");
        if (commandsList != null) commands.addAll(commandsList);

        for (String command: commands) {
            Bukkit.broadcastMessage("Executing command [" + command + "]");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }



    public void win(Player player) {
        winItem(player);
        winCommands(player);
    }
}
