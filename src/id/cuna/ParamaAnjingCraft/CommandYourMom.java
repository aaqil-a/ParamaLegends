package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;

public class CommandYourMom implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            return false;
        }
        Bukkit.broadcastMessage(sender.getName() + " is doing " + args[0] + "'s mom!");

        return true;
    }


}
