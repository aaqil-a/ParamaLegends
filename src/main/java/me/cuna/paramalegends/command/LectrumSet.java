package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class LectrumSet implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public LectrumSet(final ParamaLegends plugin){
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
