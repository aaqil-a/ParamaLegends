package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class Tip implements CommandExecutor {

    public DataManager data;
    private final ParamaLegends plugin;
    private final List<Player> tipCooldown = new ArrayList<>();

    public Tip(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /tip <player> <amount>");
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){
                sendUsage(player);
            } else {
                int amount = 1;
                if(args.length == 2){
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch (NumberFormatException nfe) {
                        player.sendMessage(ChatColor.RED + "Invalid amount.");
                        return true;
                    }
                }
                if (args.length == 1 || args.length == 2) {
                        Player receiver = Bukkit.getPlayer(args[0]);
                        if (receiver != null) {
                            int senderLectrum = plugin.playerManager.getPlayerParama(player).getLectrum();
                            if(tipCooldown.contains(player)){
                                player.sendMessage(ChatColor.RED+"Tip is on cooldown.");
                            } else if (senderLectrum < amount) {
                                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
                            } else if(amount < 1){
                                player.sendMessage(ChatColor.RED + "Invalid amount.");
                            } else {
                                plugin.playerManager.getPlayerParama(receiver).addLectrum(amount);
                                plugin.playerManager.getPlayerParama(player).removeLectrum(amount);
                                Bukkit.broadcastMessage(ChatColor.YELLOW+player.getName()+ ChatColor.GOLD+" tipped " + ChatColor.YELLOW+ receiver.getName() + ChatColor.GOLD+" "+amount+" lectrum.");
                                //play tip sound
                                for(Player online : Bukkit.getOnlinePlayers()){
                                    online.playSound(online.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
                                }

                                //add player to cooldown
                                tipCooldown.add(player);
                                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                    tipCooldown.remove(player);
                                }, 600);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Specified player not found.");
                        }

                } else {
                    sendUsage(player);
                }
            }
        }
        return true;
    }


}
