package com.tinnyspoon.lottobox.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ParseName;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class SetCmd implements CommandExecutor {

    private Config cratesConfig = Configs.cratesConfig;

    // public SetCmd() {
    //     this.cratesConfig = Configs.cratesConfig;
    // }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return false;
        }

        String crateName = ParseName.parseCrateName(args);
        if (crateName == null) return false;

        if (!cratesConfig.config.contains("Crates." + crateName)) {
            player.sendMessage("Crate [" + crateName + "§r] does not exist");
            return true;
        }

        PersistentData.setPlayerString(player, "setting-crate", crateName);
        player.sendMessage("Setting crate [" + crateName + "§r]");
        return true;
    }
    
}