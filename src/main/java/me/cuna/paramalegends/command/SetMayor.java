package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SetMayor implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public SetMayor(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Usage: /setmayor <player>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            sendUsage(sender);
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null){
                data.getConfig().set("mayoruuid", player.getUniqueId().toString());
                data.saveConfig();
            } else {
                sender.sendMessage(ChatColor.RED + "Player not found");
            }
        }
        return true;
    }


}
