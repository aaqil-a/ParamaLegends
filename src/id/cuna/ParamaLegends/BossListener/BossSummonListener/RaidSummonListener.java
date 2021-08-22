package id.cuna.ParamaLegends.BossListener.BossSummonListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RaidSummonListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public boolean isRaidOccuring = false;

    public RaidSummonListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Listen for summoning item usage
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().hasItemMeta()){
            ItemMeta meta = event.getItem().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals("§6Esoteric Pearl")){
                if(event.getAction().equals(Action.RIGHT_CLICK_AIR)){
                    event.setCancelled(true);
                    return;
                }
                if(!isRaidOccuring){
                    if(event.getPlayer().getWorld().getTime() < 13000 || event.getPlayer().getWorld().getTime() > 23000){
                        event.getPlayer().sendMessage("§6Esoteric Pearl"+ChatColor.GRAY+" can only be used at night.");
                    } else {
                        //Check if player is nearby the occult altar
                        if(occultAltarCheck(event.getPlayer())) {
                            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"Shrieks and cries can be heard in the distance.");
                            isRaidOccuring = true;
                            event.getPlayer().getWorld().setTime(14000);
                            plugin.raidFightListener.raidFight(event.getPlayer().getWorld());
                        } else event.getPlayer().sendMessage("§6Esoteric Pearl"+ChatColor.GRAY+" can only be used nearby the §6Occult Altar"+ChatColor.GRAY+".");
                    }
                } else event.getPlayer().sendMessage("§6Esoteric Pearl"+ChatColor.GRAY+" cannot be used during a raid.");
                event.setCancelled(true);
            }
        }
    }

    public boolean occultAltarCheck(Player player){
        List<Entity> entities = player.getNearbyEntities(3,3,3);
        for(Entity e : entities){
            if(e instanceof ArmorStand){
                if(e.getCustomName() != null && e.getCustomName().equals("§6Occult Altar")){
                    return true;
                }
            }
        }
        return false;
    }

    public void setRaidOccuring(boolean isRaidOccuring){
        this.isRaidOccuring = isRaidOccuring;
    }

}
