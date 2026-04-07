package com.tinnyspoon.lottobox.inventories;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.tinnyspoon.lottobox.utils.Configs;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class DeleteAreYouSureInv {
    public static void openInv(Player player, ItemStack editItem, ItemStack selectedItem) {
        Inventory inv = Bukkit.createInventory(player, 45, "Are you sure");
        ItemStack[] fillItems = new ItemStack[45];
        Arrays.fill(fillItems, ItemCreator.getFillItem());
        inv.setContents(fillItems);

        inv.setItem(13, editItem);
        inv.setItem(31, selectedItem);

        ItemStack cancelItem = ItemCreator.itemStackWithName(Material.RED_DYE, "CANCEL");
        ItemStack confirmItem = ItemCreator.itemStackWithName(Material.LIME_DYE, "CONFIRM DELETE");

        inv.setItem(20, cancelItem);
        inv.setItem(24, confirmItem);



        player.closeInventory();
        player.openInventory(inv);
    }

    public static void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (clickedItem.getType() == Material.RED_DYE) {
            ItemStack editItem = event.getView().getTopInventory().getItem(13);
            event.getWhoClicked().getInventory().addItem(editItem);
            EditItemInv.openInv(player, editItem);
            return;
        }

        if (clickedItem.getType() == Material.LIME_DYE) {
            handleConfirm(event, player);
            return;
        }
    }

    public static void handleConfirm(InventoryClickEvent event, Player player) {
        ItemStack editItem = event.getInventory().getItem(13);
        ItemStack selectedItem = event.getInventory().getItem(31);
        if (editItem == null || selectedItem == null) return;

        String crateName = PersistentData.getItemString(editItem, "crate-name");
        Integer itemIndex = PersistentData.getItemData(editItem, "item-index", PersistentDataType.INTEGER);
        String itemType = PersistentData.getItemString(selectedItem, "type");
        Integer winIndex = PersistentData.getItemData(selectedItem, "index", PersistentDataType.INTEGER);
        if (crateName == null || itemIndex == null || itemType == null || winIndex == null) return;

        boolean isItem = itemType.equals("Item");
        boolean isCommand = itemType.equals("Command");
        if (!isItem && !isCommand) return;

        String sectionString = (isItem) ? "items" : "commands";

        List<Map<?, ?>> items = Configs.cratesConfig.config.getMapList("Crates." + crateName + ".items");
        Map<?, ?> item;
        try { item = items.get(itemIndex); }
        catch (IndexOutOfBoundsException e) { return; }
        
        Object winObject = item.get("win");
        if (winObject == null) return;
        if (!(winObject instanceof Map<?, ?> winMap)) return ;
        Object sectionObject = winMap.get(sectionString);
        if (sectionObject == null) return;
        if (!(sectionObject instanceof List<?> sectionList)) return;
        sectionList.remove(winIndex.intValue());
        Configs.cratesConfig.config.set("Crates." + crateName + ".items", items);
        Configs.cratesConfig.save();

        EditItemInv.openInv(player, editItem);
    }
}
