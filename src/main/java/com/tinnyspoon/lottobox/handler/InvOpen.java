package com.tinnyspoon.lottobox.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class InvOpen implements Listener {

    private File locationConfigFile;
    private FileConfiguration locationConfig;

    public InvOpen(File dataFolder) {
        locationConfigFile = new File(dataFolder, "location.yml");
        locationConfig = YamlConfiguration.loadConfiguration(locationConfigFile);
    }


    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof @SuppressWarnings("unused") Player player)) {
            return;
        }

        @Nullable String settingCrateName = PersistentData.getPlayerString(player, "setting-crate");

        if (settingCrateName != null && !settingCrateName.equalsIgnoreCase("None")) {
            handleSetCrate(event, player, settingCrateName);
            return;
        }

        handleCrateOpen(event);
    }


    private void handleSetCrate(InventoryOpenEvent event, Player player, String crateName) {
        if (event.getInventory().getHolder() instanceof BlockState blockstate) {
            event.setCancelled(true);

            Location loc = blockstate.getLocation();
            String locString = String.format("%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            if (locationConfig.contains(locString)) {
                player.sendMessage("Crate already exists there");
                PersistentData.removePlayerString(player, "setting-crate");
                return;
            }

            locationConfig.set(locString, crateName);

            try {
                locationConfig.save(locationConfigFile);
                player.sendMessage("Successfully created crate [" + crateName + "]");
            } catch (IOException e) {
                player.sendMessage("Failed to set crate");
            }

            PersistentData.removePlayerString(player, "setting-crate");
        }
    }

    
    public void handleCrateOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof BlockState blockState) {
            Location loc = blockState.getLocation();
            String locString = String.format("%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    
            String crateName = locationConfig.getString(locString);
            if (crateName == null) return;
            event.getPlayer().sendMessage("Opened " + crateName);

            LootTable lootTable = LootTable.fromName(crateName);
            ArrayList<LootItem> lootPool = lootTable.genLootPool();

            for (LootItem item : lootPool) {
                Bukkit.broadcastMessage(item.itemName);
            }

            event.setCancelled(true);
        }
    }
}
