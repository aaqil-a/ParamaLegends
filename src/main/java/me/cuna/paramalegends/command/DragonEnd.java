package me.cuna.paramalegends.command;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class DragonEnd implements CommandExecutor {

    public ParamaLegends plugin;

    public DragonEnd(final ParamaLegends plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       plugin.dragonFightListener.loseFight();
       return true;
    }


}
