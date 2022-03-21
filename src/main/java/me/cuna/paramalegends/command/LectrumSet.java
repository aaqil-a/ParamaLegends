package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class LectrumSet implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public LectrumSet(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Usage: /lectrumset <player> <lectrum>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            sendUsage(sender);
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null){
                int lectrum;
                try{
                    lectrum = Integer.parseInt(args[1]);
                } catch(NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED+"Invalid lectrum");
                    return true;
                }
                plugin.playerManager.getPlayerParama(player).setLectrum(lectrum);
                plugin.leaderboard.updateNetWorth(player.getUniqueId().toString());
            } else {
                sender.sendMessage(ChatColor.RED + "Player not found");
            }
        }
        return true;
    }


}
