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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class InvOpen implements Listener {

    public Config locationsConfig = Configs.locationsConfig;

    // public InvOpen() {
    //     this.locationsConfig = Configs.locationsConfig;
    // }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof @SuppressWarnings("unused") Player player)) {
            return;
        }

        String settingCrateName = getSetCrateName(player);
        if (settingCrateName != null) {
            handleSetCrate(event, player, settingCrateName);
            return;
        }

        String openCrateName = getOpenCrateName(event, player);
        if (openCrateName != null) {
            handleCrateOpen(event, player, openCrateName);
            return;
        }
    }
    
    
    private @Nullable String getSetCrateName(Player player) {
        String settingCrateName = PersistentData.getPlayerString(player, "setting-crate");
        if (settingCrateName == null || settingCrateName.equalsIgnoreCase("None")) {
            return null;
        } else {
            return settingCrateName;
        }
    }

    private @Nullable String getOpenCrateName(InventoryOpenEvent event, Player player) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof BlockState blockState) {
            Location loc = blockState.getLocation();
            String locString = String.format("%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            String crateName = locationsConfig.config.getString(locString);

            if (crateName == null) return null;

            event.setCancelled(true);

            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            String crateKeyName = PersistentData.getItemString(mainHandItem, "crate-key");
            if (crateKeyName == null) {
                player.sendMessage("You must have a key to open this crate");
                return null;
            }
            if (!crateKeyName.equals(crateName)) {
                player.sendMessage("Incorrect key for this crate");
                return null;
            };

            return crateName;
        }
        return null;
    }




    private void handleSetCrate(InventoryOpenEvent event, Player player, String crateName) {
        if (event.getInventory().getHolder() instanceof BlockState blockstate) {
            event.setCancelled(true);

            Location loc = blockstate.getLocation();
            String locString = String.format("%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            if (locationsConfig.config.contains(locString)) {
                player.sendMessage("Crate already exists there");
                PersistentData.removePlayerString(player, "setting-crate");
                return;
            }

            locationsConfig.config.set(locString, crateName);

            if (locationsConfig.save()) {
                player.sendMessage("Successfully created crate [" + crateName + "]");
            }

            PersistentData.removePlayerString(player, "setting-crate");
        }
    }

    
    public void handleCrateOpen(InventoryOpenEvent event, Player player, String crateName) {
        player.sendMessage("Opened " + crateName);
        ItemStack key = player.getInventory().getItemInMainHand();
        key.setAmount(key.getAmount() - 1);

        LootTable lootTable = LootTable.fromName(crateName);
        ArrayList<LootItem> lootPool = lootTable.genLootPool();

        for (LootItem item : lootPool) {
            Bukkit.broadcastMessage(item.itemName);
        }
    }
}
