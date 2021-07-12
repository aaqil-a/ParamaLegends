package id.cuna.ParamaLegends.ClassListener;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class ClassListener implements Listener {

    private final ParamaLegends plugin;
    private DataManager data;
    private final HashMap<Player, Integer> playerClassLevel = new HashMap<Player, Integer>();
    private final String playerClass;

    public ClassListener(final ParamaLegends plugin, ClassType classType){
        this.plugin = plugin;
        data = plugin.getData();

        playerClass = switch(classType){
            case ARCHERY -> "Archery";
            case MAGIC -> "Magic";
            case SWORDSMAN -> "Sworsdmanship";
            case REAPER -> "Reaper";
        };
    }

    //Change player's level in playerClassLevel hashmap when leveling up
    public void levelUp(Player player){
        int curLevel = playerClassLevel.get(player);
        playerClassLevel.replace(player, curLevel+1);
    }

    //Get player's class level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        int classLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + "."+playerClass.toLowerCase());
        playerClassLevel.put(player, classLevel);
    }

    // Determine if player magic level is high enough to cast a spell
    public boolean checkLevel(Player player, int level){
        if(playerClassLevel.get(player) < level){
            player.sendMessage(ChatColor.GRAY + "You do not understand how to use this spell yet.");
            return false;
        } else {
            return true;
        }
    }

    // Determine if player has enough mana to cast a spell and subtract mana if possible
    public boolean subtractMana(Player player, int manaCost){
        int currMana = player.getLevel();
        if(manaCost <= currMana){
            player.setLevel(currMana - manaCost);
            return true;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "Not enough mana.");
            return false;
        }
    }

    public void sendCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.GRAY + " is on cooldown.");
    }
    public void sendNoLongerCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }
    public void sendOutOfRangeMessage(Player player){
        player.sendMessage(ChatColor.GRAY + "Out of range to cast spell.");
    }

    public HashMap<Player, Integer> getPlayerLevel() {
        return playerClassLevel;
    }


}
