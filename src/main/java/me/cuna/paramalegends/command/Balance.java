package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Balance implements CommandExecutor {

    public DataManager data;
    private ParamaLegends plugin;

    public Balance(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /bal");
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length != 0){
                sendUsage(player);
            } else {
                player.sendMessage(ChatColor.GOLD + "Your Lectrum: " + plugin.getPlayerParama(player).getLectrum());
                }
            }
        return true;
    }

}
