package com.tinnyspoon.lottobox.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;

public class Delete implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("what" + args.length);
            return false;
        }

        String crateName = args[0];
        Config cratesConfig = Configs.cratesConfig;
        if (!cratesConfig.config.contains(crateName)) {
            sender.sendMessage("Crate [" + crateName + "] does not exist");
            return true;
        }

        cratesConfig.config.set(crateName, null);
        cratesConfig.save();
        sender.sendMessage("Deleted crate [" + crateName + "]");

        return true;
    }
}
