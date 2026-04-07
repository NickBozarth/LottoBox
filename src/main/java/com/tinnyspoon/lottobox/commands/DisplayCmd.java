package com.tinnyspoon.lottobox.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ParseName;

public class DisplayCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // sender.sendMessage();
        if (args.length == 0) {
            sender.sendMessage("invalid args");
            return false;
        }

        String argsString = String.join(" ", args);

        sender.sendMessage("RUNNING QUERY [" + argsString + "]");

        Object ret = Configs.cratesConfig.config.get(argsString);
        if (ret == null) sender.sendMessage("RET NULL");
        else sender.sendMessage("RET CLASS " + ret.getClass().getName());

        return true;
    }
}
