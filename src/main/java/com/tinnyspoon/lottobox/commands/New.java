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


        if (!(sender instanceof Player player)) return false;


        if (cratesConfig.config.contains(crateName)) {
            sender.sendMessage("Crate [" + crateName + "] already exists.");
            return true;
        }

        // String configSectionName = ;
        cratesConfig.config.set(crateName + ".key-material", "TRIPWIRE_HOOK");
        cratesConfig.config.createSection(crateName + ".items.My Item");
        ConfigurationSection sec = cratesConfig.config.getConfigurationSection(crateName + ".items.My Item");
        sec.set("weight", 50);
        sec.set("display-item", "DIRT");
        // sec.set("item.name", "DIRT");
        sec.set("item", player.getInventory().getItemInMainHand());
        // sec.set("item.quantity", 64);
        sec.set("command", "say hello");
        sec.set("commands", Arrays.asList("say hello", "msg <player> hello"));
        
        cratesConfig.save();

        sender.sendMessage("Created new crate " + crateName);

        return true;
    }

};