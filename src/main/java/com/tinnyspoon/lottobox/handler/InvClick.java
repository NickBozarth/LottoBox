package com.tinnyspoon.lottobox.handler;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.ItemCreator;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class InvClick implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();

        if (title.equals("LottoBox") && event.getClickedInventory() == event.getView().getTopInventory()) {
            event.setCancelled(true);
        }

        else if (title.equals("Edit Item")) {
            handleEditItemClick();
        }

        else if (title.startsWith("Edit")) {
            String ret = handleEditClick(event, event.getView().getTitle().substring(5));
            Bukkit.broadcastMessage("Returned with ret " + ret);
        }
    }

    private String handleEditClick(InventoryClickEvent event, String crateName) {
        if (!(event.getWhoClicked() instanceof Player player)) return "not a player";
        if (event.getView().getTopInventory() != event.getClickedInventory()) return "not top";

        LootTable lootTable = LootTable.fromName(crateName);
        if (lootTable == null) return "no loot table";
        if (event.getCurrentItem() == null) return "no item";
        Integer itemIndex = PersistentData.getItemData(event.getCurrentItem(), "item-index", PersistentDataType.INTEGER);
        if (itemIndex == null) return "no index";
        event.setCancelled(true);

        LootItem lootItem = lootTable.getLootItem(itemIndex);

        Inventory inv = Bukkit.createInventory(player, 54, "Edit Item");
        ItemStack[] fillItems = new ItemStack[54];
        Arrays.fill(fillItems, ItemCreator.getFillItem());
        inv.setContents(fillItems);

        inv.setItem(13, lootItem.getEditItem(lootTable.getTotalWeight()));
        inv.setItem(27, ItemCreator.itemStackWithName(Material.RED_DYE, "Delete Item"));
        inv.setItem(35, ItemCreator.itemStackWithName(Material.LIME_DYE, "Add Item"));
        inv.setItem(36, ItemCreator.itemStackWithName(Material.RED_DYE, "Delete Command"));
        inv.setItem(44, ItemCreator.itemStackWithName(Material.LIME_DYE, "Add Command"));

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

        if (lootItem.winCommands.size() == 1) 
            inv.setItem(40, ItemCreator.itemStackWithName(Material.SPRUCE_SIGN, lootItem.winCommands.get(0)));
        else {
            for (int i = 0; i < 5; i++) {
                if (i >= lootItem.winCommands.size()) break;
                String command = lootItem.winCommands.get(i);
                ItemStack commandItem = ItemCreator.itemStackWithName(Material.SPRUCE_SIGN, "/" + command);
                ItemCreator.setLore(commandItem, Arrays.asList("Click to edit command"));
                inv.setItem(i + 38, commandItem);
            }
        }


        player.closeInventory();
        player.openInventory(inv);

        return "Success";
    }

    private void handleEditItemClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() != event.getClickedInventory()) return;
        
    }

}
