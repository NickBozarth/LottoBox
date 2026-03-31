package com.tinnyspoon.lottobox.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class PersistentData {
    private static JavaPlugin plugin;

    public static void setPlugin(JavaPlugin plugin) {
        PersistentData.plugin = plugin;
    }

    public static void setPlayerString(Player player, String key, String value) {
        NamespacedKey nskey = new NamespacedKey(PersistentData.plugin, key);
        player.getPersistentDataContainer().set(nskey, PersistentDataType.STRING, value);
    }

    public static @Nullable String getPlayerString(Player player, String key) {
        NamespacedKey nskey = new NamespacedKey(PersistentData.plugin, key);
        return player.getPersistentDataContainer().get(nskey, PersistentDataType.STRING);
    }

    public static void removePlayerString(Player player, String key) {
        NamespacedKey nskey = new NamespacedKey(PersistentData.plugin, key);
        player.getPersistentDataContainer().remove(nskey);
    }
}
