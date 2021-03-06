package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Lectrum implements CommandExecutor {

    public DataManager data;
    private ParamaLegends plugin;

    public Lectrum(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /lectrum [balance/pay]");
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){
                sendUsage(player);
            } else {
                switch (args[0].toLowerCase()) {
                    case "balance" ->
                        player.sendMessage(ChatColor.GOLD + "Your Lectrum: " + plugin.getPlayerParama(player).getLectrum());
                    case "pay" -> {
                        if (args.length == 3) {
                            try {
                                int amount = Integer.parseInt(args[2]);
                                Player receiver = Bukkit.getPlayer(args[1]);
                                if (receiver != null) {
                                    int senderLectrum = plugin.getPlayerParama(player).getLectrum();
                                    if (senderLectrum < amount) {
                                        player.sendMessage(ChatColor.RED + "Not enough lectrum!");
                                    } else if(amount < 1) {
                                        player.sendMessage(ChatColor.RED + "Invalid amount.");
                                    } else {
                                        plugin.getPlayerParama(receiver).addLectrum(amount);
                                        plugin.getPlayerParama(player).removeLectrum(amount);
                                        player.sendMessage(ChatColor.GOLD + "Successfully sent " + amount + " lectrum to " + args[1] + ".");
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
                    default -> sendUsage(player);
                }
            }
        }
        return true;
    }


}
