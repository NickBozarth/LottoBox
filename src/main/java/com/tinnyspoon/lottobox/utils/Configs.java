package com.tinnyspoon.lottobox.utils;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Configs {
    public static Config cratesConfig;
    public static Config locationsConfig;
    
    private static File dataFolder;


    public static void loadDataFolder(File dataFolder) {
        Configs.dataFolder = dataFolder;

        Configs.cratesConfig = new Config(dataFolder, "crates.yml");
        Configs.locationsConfig = new Config(dataFolder, "locations.yml");
    }

    public static void refresh() {
        Configs.loadDataFolder(dataFolder);
    }
}
