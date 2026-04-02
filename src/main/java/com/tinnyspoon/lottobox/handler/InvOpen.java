package com.tinnyspoon.lottobox.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.tinnyspoon.lottobox.LottoBox;
import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ParseName;
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
            if (!Configs.cratesConfig.config.contains("Crates." + crateName)) {
                Bukkit.getLogger().log(Level.WARNING, "Crate [" + crateName + "] at [" + locString + "] does not exist");
                return null;
            }



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
                player.sendMessage("Successfully created crate [" + crateName + "§r]");
            }

            PersistentData.removePlayerString(player, "setting-crate");
        }
    }

    
    public void handleCrateOpen(InventoryOpenEvent event, Player player, String crateName) {
        player.sendMessage("Opened " + crateName);
        ItemStack key = player.getInventory().getItemInMainHand();
        key.setAmount(key.getAmount() - 1);

        LootTable lootTable = LootTable.fromName(crateName);
        if (lootTable == null) return;
        
        ArrayList<LootItem> lootPool = lootTable.genLootPool(150);

        Inventory inv = Bukkit.createInventory(player, 27, "LottoBox");
        player.openInventory(inv);
        rollItems(inv, PersistentData.plugin, player, lootPool);
    }


    private void fillEmptyWith() {

    }

    private void rollItems(Inventory lootboxinv, JavaPlugin plugin, Player player, ArrayList<LootItem> lootPool) {
        long spinSpeed = Configs.cratesConfig.config.getLong("spin-speed", 1L);
        long spinDelay = Configs.cratesConfig.config.getLong("spin-delay", 0L);
        List<ItemStack> fillItems = Configs.cratesConfig.config.getStringList("fill-items")
            .stream()
            .map(ParseName::stringToFillerItem)
            .filter(material -> material != null)
            .toList();

        new BukkitRunnable() {
            int index = 0;
            int sinceLastSkip = 0;
            int fillItemsIndex = 0;
            boolean skipping = false;

            @Override
            public void run() {

                for (int i = 0; i < 9; i++) {
                    lootboxinv.setItem(i + 9, lootPool.get(index + i).displayItem);
                }

                if (fillItems.size() != 0 && index % 5 == 0) {
                    ItemStack fillItem = fillItems.get(fillItemsIndex);
                    for (int i = 0; i < 9; i++) {
                        lootboxinv.setItem(i, fillItem);
                        lootboxinv.setItem(18 + i, fillItem);
                    }
                    fillItemsIndex++;
                    if (fillItemsIndex >= fillItems.size()) fillItemsIndex = 0;
                }


                if (lootboxinv.getViewers().size() == 1) {
                    lootPool.get(lootPool.size() - 5).win(player);
                    this.cancel();
                    return;
                }

                if (index >= lootPool.size() * 15 / 16) skipping = sinceLastSkip++ != 5;
                else if (index >= lootPool.size() * 7 / 8) skipping = sinceLastSkip++ != 3;
                else if (index >= lootPool.size() * 2 / 3) skipping = sinceLastSkip++ != 1;
                if (index >= lootPool.size()-9) {
                    winItem(lootboxinv, plugin, player, lootPool.get(lootPool.size() - 5));
                    this.cancel();
                    return;
                };
                
                if (!skipping) {
                    index++;
                    sinceLastSkip = 0;
                }
            }
        }.runTaskTimer(plugin, spinDelay, spinSpeed);
    }

    private void winItem(Inventory lootboxinv, JavaPlugin plugin, Player player, LootItem winItem) {
        long winDelay = Configs.cratesConfig.config.getLong("win-delay", 100L);
        String fillItemString = Configs.cratesConfig.config.getString("win-screen-item", "BLACK_STAINED_GLASS_PANE");
        ItemStack fillItem = ParseName.stringToFillerItem(fillItemString, Material.BLACK_STAINED_GLASS_PANE);

        for (int i = 0; i < 9; i++) {
            lootboxinv.setItem(i,      fillItem);
            lootboxinv.setItem(i + 18, fillItem);
            if (i != 4) lootboxinv.setItem(i + 9, fillItem);
        }



        new BukkitRunnable() {
            @Override 
            public void run() {
                winItem.win(player);

                if (lootboxinv.getViewers().contains(player)) player.closeInventory();
            }
        }.runTaskLater(plugin, winDelay);
    }
}
