package com.tinnyspoon.lottobox.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.ParseName;

public class DisplayCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        
        String crateName = ParseName.parseCrateName(args);
        if (crateName == null) return false;

        LootTable table = LootTable.fromName(crateName);
        // table.win(player);

        return true;
    }
}
