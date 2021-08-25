package id.cuna.ParamaLegends.Command;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandDestinySet implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public CommandDestinySet(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Usage: /destinyset <player> <destiny> <level>");
    }

    public void sendDestinyNames(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Valid destiny names: Swordsmanship, Archery, Reaper, Magic");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 3){
            sendUsage(sender);
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null){
                int level;
                try{
                    level = Integer.parseInt(args[2]);
                } catch(NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED+"Invalid level");
                    return true;
                }
                switch(args[1].toLowerCase()){
                    case "swordsmanship", "archery", "magic", "reaper" -> {
                        data.getConfig().set("players."+player.getUniqueId().toString()+"."+args[1].toLowerCase(), level);
                        data.getConfig().set("players."+player.getUniqueId().toString()+"."+args[1].toLowerCase()+"exp", 0);
                        data.saveConfig();
                    }
                    default -> sendDestinyNames(sender);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Player not found");
            }
        }
        return true;
    }


}