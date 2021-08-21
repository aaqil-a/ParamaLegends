package id.cuna.ParamaLegends.Command;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandLectrumSet implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public CommandLectrumSet(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
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
                data.getConfig().set("players."+player.getUniqueId().toString()+".lectrum", lectrum);
                data.saveConfig();
            } else {
                sender.sendMessage(ChatColor.RED + "Player not found");
            }
        }
        return true;
    }


}
