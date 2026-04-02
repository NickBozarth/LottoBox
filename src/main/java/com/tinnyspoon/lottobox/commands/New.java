package com.tinnyspoon.lottobox.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.lottobox.utils.Config;
import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ParseName;

public class New implements CommandExecutor {

    private static Config cratesConfig = Configs.cratesConfig;

    // public New() {
    //     this.cratesConfig = Configs.cratesConfig;
    // }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String crateName = ParseName.parseCrateName(args);
        if (crateName == null) return false;

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command");
            return true;
        }


        if (cratesConfig.config.contains("Crates." + crateName)) {
            sender.sendMessage("Crate [" + crateName + "§r] already exists.");
            return true;
        }

        // String configSectionName = ;
        cratesConfig.config.set("Crates." + crateName + ".key-material", "TRIPWIRE_HOOK");
        cratesConfig.config.set("Crates." + crateName + ".items", Arrays.asList());
        // ConfigurationSection sec = cratesConfig.config.getConfigurationSection(crateName + ".items.My Item");
        // sec.set("weight", 50);
        // sec.set("display-item", player.getInventory().getItemInMainHand());
        // sec.set("item", "...");
        // sec.set("command", "say hello");
        // sec.set("commands", Arrays.asList("say hello", "msg <player> hello"));
        
        cratesConfig.save();

        sender.sendMessage("Created new crate " + crateName);

        return true;
    }

};