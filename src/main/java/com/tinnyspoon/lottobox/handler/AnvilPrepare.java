package com.tinnyspoon.lottobox.handler;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import com.tinnyspoon.lottobox.inventories.AddCommandInv;
import com.tinnyspoon.lottobox.inventories.AddItemInv;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class AnvilPrepare implements Listener {
    @EventHandler
    public static void onPrepare(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        if (firstItem == null) return;
        String addingTo = PersistentData.getItemString(firstItem, "adding-to");
        if (addingTo == null) return;

        if (addingTo.equals("item")) {
            AddItemInv.onPrepare(event);
        }

        else if (addingTo.equals("command")) {
            AddCommandInv.onPrepare(event);
        }
    }
}
