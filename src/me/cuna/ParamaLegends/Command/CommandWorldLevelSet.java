package me.cuna.ParamaLegends.Command;

import me.cuna.ParamaLegends.DataManager;
import me.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandWorldLevelSet implements CommandExecutor {

    public DataManager data;

    public CommandWorldLevelSet(final ParamaLegends plugin){
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
