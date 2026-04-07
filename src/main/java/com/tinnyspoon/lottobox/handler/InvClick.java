package com.tinnyspoon.lottobox.handler;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import com.tinnyspoon.lottobox.inventories.AddCommandInv;
import com.tinnyspoon.lottobox.inventories.AddItemInv;
import com.tinnyspoon.lottobox.inventories.DeleteAreYouSureInv;
import com.tinnyspoon.lottobox.inventories.EditInv;
import com.tinnyspoon.lottobox.inventories.EditItemInv;
import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.ParseName;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class InvClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.equals("LottoBox") && event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);
        }

        else if (title.equals("Edit Item")) {
            EditItemInv.onClick(event);
            return;
        }

        else if (title.startsWith("Edit")) {
            EditInv.onClick(event);
            return;
        }

        else if (title.equals("Are you sure")) {
            DeleteAreYouSureInv.onClick(event);
            return;
        }




        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return;
        String addingTo = PersistentData.getItemString(firstItem, "adding-to");
        if (addingTo == null) return;

        
        if (addingTo.equals("item")) {
            AddItemInv.onClick(event);
            return;
        }

        else if (addingTo.equals("command")) {
            AddCommandInv.onClick(event);
            return;
        }
    }
}
