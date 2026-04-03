package com.tinnyspoon.lottobox.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ParseName;

public class Delete implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String crateName = ParseName.parseCrateName(args);
        if (crateName == null) return false;

        Config cratesConfig = Configs.cratesConfig;
        if (!cratesConfig.config.contains("Crates." + crateName)) {
            sender.sendMessage("Crate [" + crateName + "§r] does not exist");
            return true;
        }

        cratesConfig.config.set("Crates." + crateName, null);
        cratesConfig.save();
        sender.sendMessage("Deleted crate [" + crateName + "§r]");

        return true;
    }
}
