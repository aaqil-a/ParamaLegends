package me.cuna.ParamaLegends.Command;

import me.cuna.ParamaLegends.DataManager;
import me.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CommandRemoveAltar implements CommandExecutor {

    public DataManager data;
    private ParamaLegends plugin;
    private final List<String> customAltarNames = new ArrayList<>();

    public CommandRemoveAltar(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        customAltarNames.add("§6Occult Altar");
        customAltarNames.add("§aMysterious Sludge");
        customAltarNames.add("§0Indestructible Stone");
        customAltarNames.add("§6Your Destiny");
        customAltarNames.add("§6Odd Wares");
        customAltarNames.add("§5Ancient Tomes");
        customAltarNames.add("§4Reaper Grindstone");
        customAltarNames.add("§2Dull Anvil");
        customAltarNames.add("§aFletcher's Table");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            List<Entity> entities = player.getNearbyEntities(1, 1, 1).stream().toList();
            for(Entity entity : entities){
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
