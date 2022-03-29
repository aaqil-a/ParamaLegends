package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Coords implements CommandExecutor {

    private final ParamaLegends plugin;

    public Coords(final ParamaLegends plugin){
        this.plugin = plugin;
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player){
            Location location = player.getLocation();
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + " is at " + location.getWorld().getName() + ": " + (int) location.getX() +", " + (int) location.getY() + ", " + (int) location.getZ());
        }
        return true;
    }


}
