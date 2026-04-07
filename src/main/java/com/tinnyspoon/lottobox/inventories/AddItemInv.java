package com.tinnyspoon.lottobox.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryView.Property;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.PersistentData;

import net.kyori.adventure.text.Component;

public class AddItemInv {
    public static void openInv(Player player, ItemStack editItem) {
        InventoryView view = player.openAnvil(null, true);
        PersistentData.setItemString(editItem, "adding-to", "item");
        view.setItem(0, editItem);
    }

    public static void onClick(InventoryClickEvent event) {
        Bukkit.broadcastMessage("STRING" + (event.getInventory() instanceof AnvilInventory));

        Inventory inv = event.getInventory();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClick().isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            if (event.getSlot() == 0) {
                LootItem lootItem = LootItem.loadItemFromEditItem(event.getCurrentItem());
                if (lootItem != null) event.setCursor(lootItem.getEditItem(-1));
                event.setCancelled(true);
            }

            if (event.getSlot() == 2 && event.getCurrentItem() != null) {
                LootItem lootItem = LootItem.loadItemFromEditItem(inv.getItem(0));
                if (lootItem == null) return;
                lootItem.winItems.add(inv.getItem(1));
                LootTable lootTable = LootTable.fromEditItem(inv.getItem(0));
                lootTable.updateLootItemInConfig(lootItem);
                player.closeInventory();
                EditItemInv.openInv(player, inv.getItem(2));
            }
        } 
    }

    public static void onPrepare(PrepareAnvilEvent event) {
        Bukkit.broadcastMessage("Preparing anvil");

        AnvilInventory anvil = event.getInventory();


        ItemStack editItem = anvil.getItem(0);
        ItemStack newItem = anvil.getItem(1);

        if (editItem == null || newItem == null) {
            event.setResult(null);
            return;
        }

        LootItem lootItem = LootItem.loadItemFromEditItem(editItem);
        if (lootItem == null) {
            event.setResult(null);
            return;
        }

        lootItem.winItems.add(newItem);
        event.setResult(lootItem.getEditItem(-1));
        return;
    }
}
