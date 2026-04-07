package com.tinnyspoon.lottobox.inventories;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.ParseName;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class EditItemInv {
    public static void openInv(Player player, ItemStack item) {
        if (item == null) return;
        String crateName = PersistentData.getItemString(item, "crate-name");
        if (crateName == null) return;
        LootTable lootTable = LootTable.fromName(crateName);
        if (lootTable == null) return;
        Integer itemIndex = PersistentData.getItemData(item, "item-index", PersistentDataType.INTEGER);
        if (itemIndex == null) return;

        LootItem lootItem = lootTable.getLootItem(itemIndex);

        EditItemInv.openInv(player, lootItem, lootTable);
    }

    public static void openInv(Player player, LootItem lootItem, LootTable lootTable) {
        Inventory inv = Bukkit.createInventory(player, 54, "Edit Item");
        ItemStack[] fillItems = new ItemStack[54];
        Arrays.fill(fillItems, ItemCreator.getFillItem());
        inv.setContents(fillItems);

        inv.setItem(13, lootItem.getEditItem(lootTable.getTotalWeight()));
        inv.setItem(27, ItemCreator.itemStackWithName(Material.RED_DYE, "Delete Item"));
        inv.setItem(35, ItemCreator.itemStackWithName(Material.LIME_DYE, "Add Item"));
        inv.setItem(36, ItemCreator.itemStackWithName(Material.RED_DYE, "Delete Command"));
        inv.setItem(44, ItemCreator.itemStackWithName(Material.LIME_DYE, "Add Command"));
        inv.setItem(49, ItemCreator.itemStackWithName(Material.BARRIER, "Go Back"));

        List<ItemStack> winItems = lootItem.getWinItemStacks();
        int lastItemIndexShown = Integer.min(5, winItems.size());
        ItemStack nextItemArrow = ItemCreator.itemStackWithName(Material.TIPPED_ARROW, "Next Item Page");
        ItemCreator.setArrowType(nextItemArrow, PotionType.OOZING);
        ItemCreator.setLore(nextItemArrow, Arrays.asList("Showing items [0 - " + lastItemIndexShown + "]"));
        ItemStack prevItemArrow = ItemCreator.itemStackWithName(Material.TIPPED_ARROW, "Previous Item Page");
        ItemCreator.setArrowType(prevItemArrow, PotionType.HEALING);
        ItemCreator.setLore(prevItemArrow, Arrays.asList("Showing items [0 - " + lastItemIndexShown + "]"));
        inv.setItem(28, prevItemArrow);
        inv.setItem(34, nextItemArrow);

        if (winItems.size() == 1) inv.setItem(31, winItems.get(0));
        else {
            for (int i = 0; i < 5; i++) {
                if (i >= winItems.size()) break;
                ItemStack item = winItems.get(i);
                inv.setItem(i + 29, item);
            }
        }


        int lastCommandIndexShown = Integer.min(5, lootItem.winCommands.size());
        ItemStack nextCommandArrow = ItemCreator.itemStackWithName(Material.TIPPED_ARROW, "Next Command Page");
        ItemCreator.setArrowType(nextCommandArrow, PotionType.OOZING);
        ItemCreator.setLore(nextCommandArrow, Arrays.asList("Showing items [0 - " + lastCommandIndexShown + "]"));
        ItemStack prevCommanArrow = ItemCreator.itemStackWithName(Material.TIPPED_ARROW, "Previous Item Page");
        ItemCreator.setArrowType(prevCommanArrow, PotionType.HEALING);
        ItemCreator.setLore(prevCommanArrow, Arrays.asList("Showing items [0 - " + lastItemIndexShown + "]"));
        inv.setItem(37, prevCommanArrow);
        inv.setItem(43, nextItemArrow);

        AtomicInteger commandIndex = new AtomicInteger();
        List<ItemStack> winCommandItems = lootItem.winCommands.stream()
            .limit(5)
            .map(command -> {
                ItemStack commandItem = ItemCreator.itemStackWithName(Material.SPRUCE_SIGN, "/" + command);
                ItemCreator.setLore(commandItem, Arrays.asList(LootItem.separator, "Click to edit command", LootItem.separator));
                PersistentData.setItemString(commandItem, "type", "Command");
                PersistentData.setItemData(commandItem, "index", commandIndex.getAndIncrement(), PersistentDataType.INTEGER);         
                return commandItem;
            })
            .toList();

        if (winCommandItems.size() == 1) 
            inv.setItem(40, winCommandItems.get(0));
        else {
            for (int i = 0; i < 5; i++) {
                if (i >= lootItem.winCommands.size()) break;
                inv.setItem(i + 38, winCommandItems.get(i));
            }
        }


        player.closeInventory();
        player.openInventory(inv);
    }


    public static void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            event.getWhoClicked().sendMessage("Only players can do this");
            return;
        }

        if (event.getView().getTopInventory() != event.getClickedInventory()) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        String itemName = ParseName.getItemStackName(clickedItem);
        Inventory inv = event.getClickedInventory();


        if (isDeleteItem(clickedItem)) {
            handleDeleteItem(inv, itemName);
            return;
        }

        if (isDeleting(inv, clickedItem)) {
            handleDelete(player, inv, clickedItem);
            return;
        }

        if (isGoBack(clickedItem)) {
            handleGoBack(player, inv);
        }


        ItemStack firstItem = inv.getItem(0);
        if (firstItem != null && firstItem.getType() == Material.RED_STAINED_GLASS_PANE) {            
            EditItemInv.replaceItemsInInv(inv, Material.RED_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE);
        }
    }

    private static boolean isDeleteItem(ItemStack item) { return item.getType() == Material.RED_DYE; }

    private static void handleDeleteItem(Inventory inv, String deleteItemName) {
        replaceItemsInInv(inv, Material.BLACK_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, deleteItemName);
        return;
    }




    private static boolean isDeleting(Inventory inv, ItemStack item) {
        ItemStack firstItem = inv.getItem(0);
        if (firstItem == null || firstItem.getType() != Material.RED_STAINED_GLASS_PANE) return false;
        if (item.getType() == Material.RED_STAINED_GLASS_PANE) return false;

        String deleteType = PersistentData.getItemString(firstItem, "type");
        String itemDeleteType = PersistentData.getItemString(item, "type");
        
        Bukkit.broadcastMessage("itemDeleteType = " + itemDeleteType);
        Bukkit.broadcastMessage("deleteType = " + deleteType);



        return itemDeleteType != null && 
               deleteType != null && 
               deleteType.endsWith(itemDeleteType);
    }

    private static void handleDelete(Player player, Inventory inv, ItemStack item) {
        ItemStack editItem = inv.getItem(13);
        DeleteAreYouSureInv.openInv(player, editItem, item);
    }   

    
    private static boolean isGoBack(ItemStack item) {
        return item != null && item.getType() == Material.BARRIER;
    }

    private static void handleGoBack(Player player, Inventory inv) {
        ItemStack editItem = inv.getItem(13);
        if (editItem == null) return;

        String crateName = PersistentData.getItemString(editItem, "crate-name");
        if (crateName == null) return;
        EditInv.openInv(player, crateName);
    }







    

    private static void replaceItemsInInv(Inventory inv, Material oldType, Material newType) { 
        replaceItemsInInv(inv, oldType, newType, null); 
    }
    private static void replaceItemsInInv(Inventory inv, Material oldType, Material newType, @Nullable String newPersistentString) {
        ItemStack[] newItems = Arrays.stream(inv.getContents())
            .map(item -> {
                if (item.getType() == oldType) {
                    ItemStack newItem = ItemCreator.itemStackWithName(newType, " ");
                    if (newPersistentString != null) PersistentData.setItemString(newItem, "type", newPersistentString);
                    return newItem;
                } else {
                    return item;
                }
            }).toArray(ItemStack[]::new);

        inv.setContents(newItems);
    }
}
