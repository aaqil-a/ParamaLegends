package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandRemoveAltar implements CommandExecutor {

    public DataManager data;
    private ParamaLegends plugin;
    private final List<String> customAltarNames = new ArrayList<>();

    public CommandRemoveAltar(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        customAltarNames.add(ChatColor.COLOR_CHAR+"6Occult Altar");
        customAltarNames.add(ChatColor.COLOR_CHAR+"aMysterious Sludge");
        customAltarNames.add(ChatColor.COLOR_CHAR+"0Indestructible Stone");
        customAltarNames.add(ChatColor.COLOR_CHAR+"6Your Destiny");
        customAltarNames.add(ChatColor.COLOR_CHAR+"6Odd Wares");
        customAltarNames.add(ChatColor.COLOR_CHAR+"5Ancient Tomes");
        customAltarNames.add(ChatColor.COLOR_CHAR+"4Reaper Grindstone");
        customAltarNames.add(ChatColor.COLOR_CHAR+"2Dull Anvil");
        customAltarNames.add(ChatColor.COLOR_CHAR+"aFletcher's Table");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            for(Entity entity : player.getNearbyEntities(1, 1, 1)){
                if(entity instanceof ArmorStand){
                    ArmorStand armorStand = (ArmorStand) entity;
                    if(customAltarNames.contains(armorStand.getCustomName())){
                        armorStand.remove();
                    }
                }
            }
        }
        return true;
    }
}
