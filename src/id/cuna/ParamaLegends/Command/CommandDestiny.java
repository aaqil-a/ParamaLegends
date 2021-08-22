package id.cuna.ParamaLegends.Command;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandDestiny implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public CommandDestiny(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            plugin.wiseOldManListener.createGui(player);
            player.openInventory(plugin.wiseOldManListener.gui.get(player));
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Your destiny unravels before you.");
//            Player player = (Player) sender;
//            player.sendMessage(ChatColor.GOLD +"Your Destiny");
//            int level = data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".swordsmanship");
//            player.sendMessage(ChatColor.GRAY +"Swordsmanship: " + level);
//            if(level<10) player.sendMessage(ChatColor.DARK_GRAY +"EXP to level up: " + (xpNeededSwordsman[level]-data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".swordsmanshipexp")));
//            level = data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".archery");
//            player.sendMessage(ChatColor.GRAY +"Archery: " + level);
//            if(level<10) player.sendMessage(ChatColor.DARK_GRAY +"EXP to level up: " + (xpNeeded[level]-data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".archeryexp")));
//            level = data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".magic");
//            player.sendMessage(ChatColor.GRAY +"Magic: " + level);
//            if(level<10) player.sendMessage(ChatColor.DARK_GRAY +"EXP to level up: " + (xpNeeded[level]-data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".magicexp")));
//            level = data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".reaper");
//            player.sendMessage(ChatColor.GRAY +"Reaper: " + level);
//            if(level<10) player.sendMessage(ChatColor.DARK_GRAY +"EXP to level up: " + (xpNeeded[level]-data.getConfig().getInt("players."+ player.getUniqueId().toString() + ".reaperexp")));
        }
        return false;
    }


}
