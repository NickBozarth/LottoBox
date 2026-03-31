package com.tinnyspoon.lottobox.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// public record Config(File configFile, FileConfiguration config) {
//     public static Config newConfig(File dataFolder, String filename) {
//         File configFile = new File(dataFolder, filename);
//         FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
//         return new Config(configFile, config);
//     }
// }


public class Config {
    private File configFile;
    private String filename;    
    public FileConfiguration config;

    public Config(File dataFolder, String filename) {
        File configFile = new File(dataFolder, filename);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        this.configFile = configFile;
        this.filename = filename;
        this.config = config;
    }


    public boolean save() {
        try {
            this.config.save(this.configFile);
            return true;
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save [" + "" + "] to [" + this.configFile.getPath() + "]");
            return false;
        }
    }    
}
