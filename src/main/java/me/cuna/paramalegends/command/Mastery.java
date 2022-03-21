package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Mastery implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public Mastery(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    public void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Usage: /mastery <spellname>");
    }

    public void sendSpellNames(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"You have not begun mastering this spell.");
        sender.sendMessage(ChatColor.RED+"Valid spells: DragonBreath, FlingEarth, Gust, Ignite, IllusoryOrb, LifeDrain, Nova, SummonLightning, VoicesofTheDamned");
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length > 0){
                String spell = args[0];
                int level = plugin.playerManager.getPlayerParama(player).getMasteryLevel(spell);
                if(level>0){
                    player.sendMessage(ChatColor.GOLD+spell.substring(0,1).toUpperCase()+spell.substring(1).toLowerCase()+" Mastery Level: "+level);
                } else {
                    sendSpellNames(sender);
                }
            } else {
                sendUsage(sender);
            }
        }
        return true;
    }


}
