package com.tinnyspoon.lottobox.handler;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.tinnyspoon.lottobox.utils.Configs;

public class BlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        String locString = String.format("%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        String crateName = Configs.locationsConfig.config.getString(locString);

        if (crateName != null) {
            Configs.locationsConfig.config.set(locString, null);
            Configs.locationsConfig.save();
            event.getPlayer().sendMessage("Broke crate [" + crateName + "§r]");
        }
    }
}
