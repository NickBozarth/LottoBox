package com.tinnyspoon.lottobox.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class GiveCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) return false;

        // /lbgive Spuwun Crate Name 1
        // /lbgive Spuwun Crate Name 1
        
        Player targettedPlayer = Bukkit.getPlayer(args[0]);
        if (targettedPlayer == null || !targettedPlayer.isOnline()) {
            sender.sendMessage("Player must be online to recieve keys");
            return true;
        }
        String crateName = getCrateName(args);
        if (!isValidCrate(crateName)) {
            sender.sendMessage("Failed to find crate [" + crateName + "§r]");
            return true;
        }
        Integer amount = getAmount(args);
        if (!isValidAmount(amount)) {
            sender.sendMessage("Valid amounts are 1-64. Entered " + amount.toString());
            return true;
        }

        ItemStack key = genKeyStack(crateName, amount);
        targettedPlayer.getInventory().addItem(key);
        
        return true;    
    }

    private String getCrateName(String[] args) {
        return Arrays.stream(args)
            .skip(1)
            .limit(args.length - 2)
            .collect(Collectors.joining(" "));
    }

    private boolean isValidCrate(String crateName) {
        if (crateName == null) return false;
        return Configs.cratesConfig.config.contains("Crates." + crateName);
    }

    private Integer getAmount(String[] args) {
        return Integer.parseInt(args[args.length-1]);
    }

    private boolean isValidAmount(Integer amount) {
        if (amount == null) return false;
        if (amount < 1 || amount > 64) return false;
        return true;
    }

    private ItemStack genKeyStack(String crateName, int amount) {
        String keyMaterialString = Configs.cratesConfig.config.getString("Crates." + crateName + ".key-material", "TRIPWIRE_HOOK");
        Material keyMaterial;
        try {
            keyMaterial = Material.valueOf(keyMaterialString);
        } catch (IllegalArgumentException e) {
            keyMaterial = Material.TRIPWIRE_HOOK;
        }
        ItemStack key = new ItemStack(keyMaterial, amount);
        
        PersistentData.setItemString(key, "crate-key", crateName);
        ItemMeta keyMeta = key.getItemMeta();
        if (keyMeta != null) {
            keyMeta.setItemName(crateName + " crate key");
            key.setItemMeta(keyMeta);
        }

        return key;
    }
}
