package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class WorldLevelSet implements CommandExecutor {

    public DataManager data;

    public WorldLevelSet(final ParamaLegends plugin){
        data = plugin.getData();
    }

    public void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Usage: /worldlevelset <level>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            sendUsage(sender);
        } else {
            int level;
            try{
                level = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                sender.sendMessage(ChatColor.RED+"Invalid level");
                return true;
            }
            data.getConfig().set("world.level", level);
            data.saveConfig();

        }
        return true;
    }


}
