package com.tinnyspoon.lottobox;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import com.tinnyspoon.lottobox.commands.DisplayCmd;
import com.tinnyspoon.lottobox.commands.New;
import com.tinnyspoon.lottobox.commands.SetCmd;
import com.tinnyspoon.lottobox.handler.InvOpen;
import com.tinnyspoon.lottobox.loot.LootItem;
import com.tinnyspoon.lottobox.loot.LootTable;
import com.tinnyspoon.lottobox.utils.PersistentData;

public class LottoBox extends JavaPlugin {
    
    @Override
    public void onEnable() {
        PersistentData.setPlugin(this);
        LootItem.setDataFolder(getDataFolder());
        LootTable.setDataFolder(getDataFolder());

        this.registerCommand("lbnew", new New(this.getDataFolder()));
        this.registerCommand("lbset", new SetCmd(this.getDataFolder()));
        this.registerCommand("lbdisplay", new DisplayCmd());

        this.getServer().getPluginManager().registerEvents(new InvOpen(this.getDataFolder()), this);
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
