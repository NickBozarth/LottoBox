package com.tinnyspoon.lottobox.handler;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.tinnyspoon.lottobox.utils.PersistentData;

public class BlockPlace implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (PersistentData.itemHasKey(item, "crate-key")) event.setCancelled(true);
    }
}
