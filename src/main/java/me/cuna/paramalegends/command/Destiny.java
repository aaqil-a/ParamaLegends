package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Destiny implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public Destiny(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            plugin.wiseOldManListener.createGui(player);
            player.openInventory(plugin.wiseOldManListener.gui.get(player));
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Your destiny unravels before you.");
        }
        return false;
    }


}
