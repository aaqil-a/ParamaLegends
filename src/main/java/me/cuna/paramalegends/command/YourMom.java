package me.cuna.paramalegends.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class YourMom implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            return false;
        }
        Bukkit.broadcastMessage(sender.getName() + " is doing " + args[0] + "'s mom!");

        return true;
    }


}
