package me.cuna.paramalegends.boss.summon;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class NatureSummonListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public boolean isFightOccuring = false;

    public NatureSummonListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //Listen for summoning item usage
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().hasItemMeta()){
            ItemMeta meta = event.getItem().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.COLOR_CHAR+"aMysterious Ooze")){
                if(event.getAction().equals(Action.RIGHT_CLICK_AIR)){
                    event.setCancelled(true);
                    return;
                }
                if(!isFightOccuring){
                    //Check if player is nearby the mysterious sludge
                    if(altarCheck(event.getPlayer())) {
                        //check if all players are nearby
                        for(Player player : Bukkit.getOnlinePlayers()){
                            if(player.getLocation().distance(event.getPlayer().getLocation()) > 100){
                                Bukkit.broadcastMessage(ChatColor.RED+"All players must be nearby to begin.");
                                event.setCancelled(true);
                                return;
                            }
                        }
                        Bukkit.broadcastMessage(ChatColor.GREEN+"The cries of living beings echo around you.");
                        isFightOccuring = true;
                        plugin.natureFightListener.bossFight(event.getPlayer().getWorld(), event.getPlayer().getLocation());
                    } else event.getPlayer().sendMessage(ChatColor.COLOR_CHAR+"aMysterious Ooze"+ChatColor.GRAY+" can only be used nearby the "+ChatColor.GOLD+"Mysterious Sludge"+ChatColor.GRAY+".");
                } else event.getPlayer().sendMessage(ChatColor.COLOR_CHAR+"aMysterious Ooze"+ChatColor.GRAY+" cannot be used during a fight.");
                event.setCancelled(true);
            }
        }
    }

    public boolean altarCheck(Player player){
        List<Entity> entities = player.getNearbyEntities(3,3,3);
        for(Entity e : entities){
            if(e instanceof ArmorStand){
                if(e.getCustomName() != null && e.getCustomName().equals(ChatColor.COLOR_CHAR+"aMysterious Sludge")){
                    return true;
                }
            }
        }
        return false;
    }

    public void setFightOccuring(boolean isFightOccuring){
        this.isFightOccuring = isFightOccuring;
    }

    public boolean isFightOccuring() {
        return this.isFightOccuring;
    }
}
