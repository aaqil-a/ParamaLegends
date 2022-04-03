package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Pay implements CommandExecutor {

    public DataManager data;
    private ParamaLegends plugin;

    public Pay(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /pay [player] [amount]");
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){
                sendUsage(player);
            } else {
                if (args.length == 2) {
                    try {
                        int amount = Integer.parseInt(args[1]);
                        Player receiver = Bukkit.getPlayer(args[0]);
                        if (receiver != null) {
                            int senderLectrum = plugin.getPlayerParama(player).getLectrum();
                            if (senderLectrum < amount) {
                                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
                            } else if(amount < 1) {
                                player.sendMessage(ChatColor.RED + "Invalid amount.");
                            } else {
                                plugin.getPlayerParama(receiver).addLectrum(amount);
                                plugin.getPlayerParama(player).removeLectrum(amount);
                                player.sendMessage(ChatColor.GOLD + "Successfully sent " + amount + " lectrum to " + args[0] + ".");
                                receiver.sendMessage(ChatColor.GOLD + "Received " + amount + " lectrum from " + player.getName() + ".");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Specified player not found.");
                        }
                    } catch (NumberFormatException nfe) {
                        player.sendMessage(ChatColor.RED + "Invalid amount.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED+"Usage: /lectrum pay [player] [amount]");
                }
            }
        }
        return true;
    }


}
