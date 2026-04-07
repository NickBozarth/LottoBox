package com.tinnyspoon.lottobox.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class AddItemInv {
    public static void openInv(Player player, ItemStack editItem) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.ANVIL, "Add Item to Loot Pool");
        inv.setItem(0, editItem);


        player.closeInventory();
        player.openInventory(inv);
    }

    public static void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClick().isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() == event.getView().getTopInventory()) {
            if (event.getSlot() == 0) event.setCancelled(true);
            if (event.getSlot() == 2 && event.getCurrentItem() != null) {
                LootItem lootItem = LootItem.loadItemFromEditItem(inv.getItem(0));
                if (lootItem == null) return;
                lootItem.winItems.add(inv.getItem(1));
                LootTable lootTable = LootTable.fromEditItem(inv.getItem(0));
                lootTable.updateLootItemInConfig(lootItem);
                player.closeInventory();
                EditItemInv.openInv(player, inv.getItem(2));
            }

            Bukkit.getScheduler().runTask(PersistentData.plugin, () -> Bukkit.broadcastMessage(onPrepare(inv)));
        } 
    }

    public static String onPrepare(Inventory anvil) {
        Bukkit.broadcastMessage("Preparing anvil");


        ItemStack editItem = anvil.getItem(0);
        ItemStack newItem = anvil.getItem(1);

        if (editItem == null || newItem == null) {
            anvil.setItem(2, null);
            return "null";
        }

        LootItem lootItem = LootItem.loadItemFromEditItem(editItem);
        if (lootItem == null) {
            anvil.setItem(2, null);
            return "loot";
        }

        lootItem.winItems.add(newItem);
        anvil.setItem(2, lootItem.getEditItem(-1));
        return "Not not success";
    }
}
