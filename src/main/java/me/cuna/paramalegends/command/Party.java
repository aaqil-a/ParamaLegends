package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Party implements CommandExecutor {

    public DataManager data;
    private ParamaLegends plugin;

    public Party(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /party [create/invite/leave/kick/info/accept]");
    }
    public void sendUsageInvite(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /party invite [player]");
    }
    public void sendUsageKick(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /party kick [player]");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            PlayerParama playerParama = plugin.getPlayerParama(player);
            if(args.length == 0){
                sendUsage(player);
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "invite" -> {
                    if(args.length == 2){
                        Player invited = Bukkit.getPlayer(args[1]);
                        PlayerParama playerParamaInvited = plugin.getPlayerParama(invited);
                        if(invited != null){
                            if(playerParama.hasParty()){
                                if(playerParamaInvited.hasParty()){
                                    player.sendMessage(ChatColor.RED + invited.getName() + " is already in a party.");
                                } else {
                                    player.sendMessage(ChatColor.GREEN + invited.getName() + " has been invited to your party.");
                                    playerParama.getParty().invite(playerParamaInvited, playerParama);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You are not in a party.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Player not found.");
                        }
                    } else {
                        sendUsageInvite(player);
                    }
                }
                case "create" -> {
                    if(playerParama.hasParty()){
                        player.sendMessage(ChatColor.RED + "You are already in a party.");
                    } else {
                        plugin.partyManager.createParty(playerParama);
                        player.sendMessage(ChatColor.GREEN + "Successfully created a party.");
                    }
                }
                case "leave" -> {
                    if(playerParama.hasParty()){
                        player.sendMessage(ChatColor.GREEN + "You have left your party.");
                        playerParama.getParty().leave(playerParama);
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    }
                }
                case "kick" -> {
                    if(args.length == 2){
                        Player kicked = Bukkit.getPlayer(args[1]);
                        if(kicked != null){
                            if(kicked.equals(player)){
                                player.sendMessage(ChatColor.RED + "You cannot kick yourself.");
                            } else if(playerParama.hasParty()){
                                playerParama.getParty().kick(plugin.getPlayerParama(kicked));
                            } else {
                                player.sendMessage(ChatColor.RED + "You are not in a party.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Player not found.");
                        }
                    } else {
                        sendUsageKick(player);
                    }
                }
                case "info" -> {
                    if(playerParama.hasParty()){
                        StringBuilder members = new StringBuilder();
                        for(PlayerParama member : playerParama.getParty().getMembers()){
                            members.append(" ").append(member.getPlayer().getName());
                        }
                        player.sendMessage(ChatColor.GOLD + "Your Party:" + members);
                    } else {
                        player.sendMessage(ChatColor.RED + "You are not in a party.");
                    }
                }
                case "accept" -> {
                    if(playerParama.hasParty()){
                        player.sendMessage(ChatColor.RED + "You are already in a party.");
                    } else if(!playerParama.hasPartyInvited()){
                        player.sendMessage(ChatColor.RED + "You are not invited to a party.");
                    } else {
                        playerParama.setParty(playerParama.getPartyInvited());
                        playerParama.setPartyInvited(null);
                        for(PlayerParama member : playerParama.getParty().getMembers()){
                            member.getPlayer().sendMessage(ChatColor.GREEN + player.getName() + " has joined your party.");
                        }
                    }
                }
                default -> sendUsage(player);
            }
        }
        return true;
    }


}
