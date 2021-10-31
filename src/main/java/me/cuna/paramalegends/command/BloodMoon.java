package me.cuna.paramalegends.command;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class BloodMoon implements CommandExecutor {

    public ParamaLegends plugin;

    public BloodMoon(final ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Usage: /bloodmoon <start/stop>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            sendUsage(sender);
        } else {
            if(args[0].equalsIgnoreCase("start")){
                if(plugin.bloodMoonSummonListener.isBloodMoonOccuring()){
                    sender.sendMessage(ChatColor.RED+"A blood moon is currently occuring.");
                } else {
                    plugin.bloodMoonListener.bossFight(Bukkit.getWorld("world"));
                }
            } else if(args[0].equalsIgnoreCase("stop")){
                if(plugin.bloodMoonSummonListener.isBloodMoonOccuring()){
                    plugin.bloodMoonListener.endFight(false);
                } else {
                    sender.sendMessage(ChatColor.RED+"No blood moon is currently occuring.");
                }
            } else {
                sendUsage(sender);
            }
        }
        return true;
    }


}
