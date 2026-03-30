package com.tinnyspoon.lottobox;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.lottobox.commands.New;

public class LottoBox extends JavaPlugin {
    
    @Override
    public void onEnable() {
        this.registerCommand("lbnew", new New());
    }


    private void registerCommand(String name, @NotNull CommandExecutor exexutor) {
        PluginCommand cmd = this.getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(exexutor);
        } else {
            this.getLogger().severe("Failed to register command [" + name + "]");
        }
    }
}
