package com.tinnyspoon.lottobox.loot;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;

public class LootTable {

    private @NotNull Material keyMaterial;
    private ArrayList<LootItem> lootItems = new ArrayList<>();
    private ArrayList<Integer> weights = new ArrayList<>();
    private String crateName;
    private static Config cratesConfig = Configs.cratesConfig;

    public static @Nullable LootTable fromName(String crateName) {
        LootTable table = new LootTable();
        table.crateName = crateName;

        Bukkit.broadcastMessage("Getting crate [" + crateName + "]");
        ConfigurationSection thisSection = LootTable.cratesConfig.config.getConfigurationSection(crateName);
        if (thisSection == null) {
            Bukkit.getLogger().log(Level.SEVERE, "CRATE [" + crateName + "] does not exist");
            return null;
        }

        String keyMaterialString = thisSection.getString("key-material", "TRIPWIRE_HOOK");
        try {
            table.keyMaterial = Material.valueOf(keyMaterialString);
        } catch (Exception e) {
            table.keyMaterial = Material.TRIPWIRE_HOOK;
        }

        for(String itemDisplayName : thisSection.getConfigurationSection("items").getKeys(false)) {
            LootItem item = LootItem.loadItem(crateName, itemDisplayName);
            if (item == null) continue;
            table.lootItems.add(item);
        }

        return table;
    }


    private int getTotalWeight() {
        int totalWeight = 0;

        for (LootItem item : this.lootItems) {
            this.weights.add(item.weight);
            totalWeight += item.weight;
        }

        return totalWeight;
    }


    private LootItem genItem(int totalWeight) {
        int randweight = ThreadLocalRandom.current().nextInt(0, totalWeight);
        for (int i = 0; i < this.weights.size(); i++) {
            if (randweight < this.weights.get(i)) return this.lootItems.get(i);
            randweight -= this.weights.get(i);
        }

        return null;
    }


    public ArrayList<LootItem> genLootPool() {
        int totalWeight = this.getTotalWeight();

        if (this.weights.size() == 0) {
            Bukkit.getLogger().log(Level.SEVERE, "Loot pool for crate [" + this.crateName + "] cannot be empty");
            return new ArrayList<>();
        }


        ArrayList<LootItem> lootPool = new ArrayList<>(); 
        
        for (int i = 0; i < 27; i++) {
            lootPool.add(this.genItem(totalWeight));
        }

        return lootPool;
    }
}
