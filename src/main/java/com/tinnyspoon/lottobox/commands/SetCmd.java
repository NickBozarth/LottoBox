package com.tinnyspoon.lottobox.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.tinnyspoon.lottobox.utils.ParseName;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class SetCmd implements CommandExecutor {

    private File cratesConfigFile;
    private FileConfiguration cratesConfig;

    public SetCmd(File dataFolder) {
        cratesConfigFile = new File(dataFolder, "crates.yml");
        cratesConfig = YamlConfiguration.loadConfiguration(cratesConfigFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return false;
        }

        String crateName = ParseName.parseCrateName(args);
        if (crateName == null) return false;

        if (!cratesConfig.contains(crateName)) {
            player.sendMessage("Crate [" + crateName + "] does not exist");
            return true;
        }

        PersistentData.setPlayerString(player, "setting-crate", crateName);
        player.sendMessage("Setting crate [" + crateName + "]");
        return true;
    }
    
}