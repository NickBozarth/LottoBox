package com.tinnyspoon.lottobox.inventories;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.ParseName;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class EditInv {
    public static void openInv(Player player, String crateName) {
        // create inv
        Inventory inv = Bukkit.createInventory(player, 54, "Edit " + crateName);

        // populate w/ crate's loot pool
        LootTable crateLootTable = LootTable.fromName(crateName);
        if (crateLootTable == null) {
            player.sendMessage("Crate [" + crateName + "] does not exist");
            return;
        }
        ItemStack displayItems[] = crateLootTable.getEditItems().toArray(ItemStack[]::new);
        inv.setContents(displayItems); 

        // open inv on player
        player.openInventory(inv);
    }

    public static void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTopInventory() != event.getClickedInventory()) return;
        ItemStack eventItem = event.getCurrentItem();
        event.setCancelled(true);
        EditItemInv.openInv(player, eventItem);
    }
}
