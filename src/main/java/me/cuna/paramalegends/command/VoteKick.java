package me.cuna.paramalegends.command;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class VoteKick implements CommandExecutor {

    private final ParamaLegends plugin;
    public final HashMap<Player, Integer> playerVotesMap = new HashMap<>();

    public VoteKick(final ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void sendUsage(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /votekick [player]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            if(args.length == 0){
                sendUsage(player);
            } else {
                Player kicked = Bukkit.getPlayer(args[0]);
                if(kicked != null){
                    //need more than half to kick a player
                    int votesNeeded = Bukkit.getOnlinePlayers().size()/2+1;

                    if(playerVotesMap.containsKey(kicked)){
                        playerVotesMap.put(kicked, playerVotesMap.get(kicked)+1);
                        if(playerVotesMap.get(kicked) >= votesNeeded){
                            //kick
                            playerVotesMap.remove(kicked);
                            kicked.kickPlayer(ChatColor.RED+"You have been voted off the server!");
                            Bukkit.broadcastMessage(ChatColor.GOLD+kicked.getName() + " has been kicked.");
                        } else {
                            //broadcast message
                            Bukkit.broadcastMessage(ChatColor.GOLD+sender.getName() + " has voted to kick " + kicked.getName() + " " + playerVotesMap.get(kicked) +"/" + votesNeeded + " votes to kick.");
                        }
                    } else {
                        //initiate vote kick
                        //check if only one player on server
                        if(votesNeeded == 1){
                            kicked.kickPlayer(ChatColor.RED+"You voted yourself off the server!");
                        } else {
                            playerVotesMap.put(kicked, 1);
                            Bukkit.broadcastMessage(ChatColor.GOLD+sender.getName() + " has begun a vote to kick " + kicked.getName() + ". 1/" + votesNeeded + " votes to kick.");

                            //vote kick time out
                            plugin.getPlayerParama(kicked).addTask("VOTEKICK",
                                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                    if(playerVotesMap.containsKey(kicked)){
                                        Bukkit.broadcastMessage(ChatColor.GOLD+"Vote to kick " + kicked.getName() + " has ended.");
                                        playerVotesMap.remove(kicked);
                                    }
                                }, 600));
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED+"Specified player not found.");
                }
            }
        }
        return true;
    }


}
