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
import org.bukkit.inventory.view.AnvilView;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class AddCommandInv {
    public static void openInv(Player player, ItemStack editItem) {
        AnvilView view = (AnvilView)player.openAnvil(null, true);
        PersistentData.setItemString(editItem, "adding-to", "command");
        view.setItem(0, editItem);
        view.setItem(1, ItemCreator.itemStackWithName(Material.SPRUCE_SIGN, "Command Name"));
    }

    public static void onClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getClick().isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        if (!(event.getView() instanceof AnvilView anvilView)) {
            return;
        }
        
        if (event.getSlot() == 2) {
            ItemStack outItem = anvilView.getTopInventory().getResult();
            String commandString = anvilView.getTopInventory().getRenameText();
            LootItem lootItem = LootItem.loadItemFromEditItem(outItem);
            LootTable lootTable = LootTable.fromEditItem(outItem);
            if (commandString == null || lootItem == null || lootTable == null) return;
            lootItem.winCommands.add(commandString);
            lootTable.updateLootItemInConfig(lootItem);
            EditItemInv.openInv(player, outItem);
        }

    }

    public static void onPrepare(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();

        String commandText = inv.getRenameText();

        ItemStack secondItem = inv.getFirstItem();
        LootItem outItem = LootItem.loadItemFromEditItem(secondItem);
        if (outItem == null) return;
        outItem.winCommands.add(commandText);
        event.setResult(outItem.getEditItem(-1));
    }
}
