package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DestinySet implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public DestinySet(final ParamaLegends plugin){
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
                ClassGameType type = switch(args[1].toLowerCase()){
                    case "swordsmanship" -> ClassGameType.SWORDSMAN;
                    case "archery" -> ClassGameType.ARCHERY;
                    case "magic" -> ClassGameType.MAGIC;
                    case "reaper" -> ClassGameType.REAPER;
                    default -> null;
                };
                if(type != null){
                    plugin.getPlayerParama(player).setClassLevel(type, level);
                    plugin.getPlayerParama(player).setClassExp(type, 0);
                } else {
                    sendDestinyNames(sender);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Player not found");
            }
        }
        return true;
    }


}
