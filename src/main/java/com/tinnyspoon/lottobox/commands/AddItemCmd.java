package com.tinnyspoon.lottobox.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ParseName;

public class AddItemCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this command");
            return true;
        }

        String crateName = ParseName.parseCrateName(args);
        if (crateName == null) return false;

        ConfigurationSection crateSec = Configs.cratesConfig.config.getConfigurationSection("Crates." + crateName);
        if (crateSec == null) {
            sender.sendMessage("Cannot find crate with name [" + crateName + "§r]");
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta itemInHandMeta = itemInHand.getItemMeta();
        if (itemInHandMeta == null || itemInHand.getType() == Material.AIR) {
            sender.sendMessage("You must be holding an item to add it to the loot pool");
            return true;
        }
        String itemName = ParseName.getItemStackName(itemInHand);

        List<Map<?, ?>> items = crateSec.getMapList("items");
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("name", itemName);
        newItem.put("display-item", itemInHand);
        newItem.put("weight", 1);
        Map<String, Object> winSection = new HashMap<>();
        winSection.put("items", Arrays.asList("display-item"));
        winSection.put("commands", Arrays.asList());
        newItem.put("win", winSection);
        items.add(newItem);
        crateSec.set("items", items);
        if (Configs.cratesConfig.save()) {
            sender.sendMessage("Successfully added [" + itemName + "§r] to crate [" + crateName + "§r]");
        }

        return true;
    }
}
