package id.cuna.ParamaLegends.Command;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandLectrum implements CommandExecutor {

    private final ParamaLegends plugin;
    private final int sceneLength = 400;
    public DataManager data;

    public CommandLectrum(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){
                player.sendMessage(ChatColor.RED+"Usage: /lectrum [balance/pay]");
                return true;
            } else {
                if(args[0].equalsIgnoreCase("balance")){
                    player.sendMessage(ChatColor.GOLD+"Your Lectrum: " + data.getConfig().getString("players." + player.getUniqueId().toString() + ".lectrum"));
                } else if(args[0].equals("pay")){
                    if(args.length == 3){
                        try {
                            int amount = Integer.parseInt(args[2]);
                            Player receiver = Bukkit.getPlayer(args[1]);
                            if(receiver != null){
                                int senderLectrum = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum");
                                if(senderLectrum < amount){
                                    player.sendMessage(ChatColor.RED+"Not enough lectrum!");
                                } else {
                                    int receiverLectrum = data.getConfig().getInt("players." + receiver.getUniqueId().toString() + ".lectrum");
                                    senderLectrum -= amount;
                                    receiverLectrum += amount;
                                    data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", senderLectrum);
                                    data.getConfig().set("players." + receiver.getUniqueId().toString() + ".lectrum", receiverLectrum);
                                    data.saveConfig();
                                    player.sendMessage(ChatColor.GOLD+"Successfully sent " + amount + " lectrum to " + args[1] + ".");
                                    receiver.sendMessage(ChatColor.GOLD+"Received " + amount + " lectrum from " + player.getName() + ".");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED+"Specified player not found.");
                            }
                        } catch(NumberFormatException nfe) {
                            player.sendMessage(ChatColor.RED+"Invalid amount.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED+"Usage: /lectrum pay [player] [amount]");
                    }
                } else {
                    player.sendMessage(ChatColor.RED+"Usage: /lectrum [balance/pay]");
                }
            }
            return true;
        }
        return false;
    }


}
