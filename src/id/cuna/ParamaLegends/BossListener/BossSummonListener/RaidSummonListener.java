package id.cuna.ParamaLegends.BossListener.BossSummonListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BoundingBox;

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
                        //Check if player inside safe zone
                        if(safeZoneCheck(event.getPlayer())) {
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

    public boolean safeZoneCheck(Player player){
        double startX = data.getConfig().getDouble("world.startX");
        double startZ = data.getConfig().getDouble("world.startZ");
        BoundingBox safeZoneBox = new BoundingBox(startX+10, 0, startZ+10, startX-10, 256, startZ-10);
        return safeZoneBox.contains(player.getLocation().toVector());
    }

    public boolean isRaidOccuring() {
        return isRaidOccuring;
    }

    public void setRaidOccuring(boolean isRaidOccuring){
        this.isRaidOccuring = isRaidOccuring;
    }

}
