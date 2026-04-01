package com.tinnyspoon.lottobox.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tinnyspoon.lottobox.utils.Configs;

public class RefreshCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Configs.refresh();
        return true;
    }
    
}
