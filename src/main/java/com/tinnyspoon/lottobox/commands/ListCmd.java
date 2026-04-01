package com.tinnyspoon.lottobox.commands;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tinnyspoon.lottobox.utils.Configs;

public class ListCmd implements CommandExecutor {
    
    @Override 
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Set<String> crateNames = Configs.cratesConfig.config.getKeys(false);
        sender.sendMessage("Server currently has " + crateNames.size() + " crates");

        int i = 0;
        for (String crateName : crateNames) {
            sender.sendMessage("Crate #" + ++i + ": [" + crateName + "§r]");
        }

        return true;
    }
}
