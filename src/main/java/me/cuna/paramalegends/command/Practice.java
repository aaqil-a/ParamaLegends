package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;


public class Practice implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public Practice(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasMetadata("PRACTICE")){
                player.removeMetadata("PRACTICE", plugin);
                player.sendMessage(ChatColor.GREEN+"You have disabled practice mode.");
            } else {
                player.sendMessage(ChatColor.GREEN+"You have enabled practice mode.");
                player.setMetadata("PRACTICE", new FixedMetadataValue(plugin, "PRACTICE"));
            }
        }
        return true;
    }


}
