package com.tinnyspoon.lottobox.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InvClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("LottoBox") && event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);
        }
    }
}
