package com.tinnyspoon.lottobox.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.tinnyspoon.lottobox.inventories.EditInv;
import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ParseName;

public class EditCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }

        // get the selected crate name
        String crateName = ParseName.parseCrateName(args);
        if (crateName == null) return false;

        EditInv.openInv(player, crateName);
        return true;
    }
}
