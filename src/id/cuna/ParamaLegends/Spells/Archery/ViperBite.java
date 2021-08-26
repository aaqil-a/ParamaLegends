package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.ArrowParama;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class ViperBite implements Listener, ArrowParama {

    private final ParamaLegends plugin;
    private final int manaCost = 15;
    private final HashMap<Entity, BukkitTask> poisonTasks = new HashMap<>();

    public ViperBite(ParamaLegends plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void shootArrow(PlayerParama player, Entity entity){
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(player.subtractMana(manaCost)){
                arrow.setCustomName("viperbite");
            }
        }
    }

    @EventHandler
    public void hitArrow(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Damageable){
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getCustomName() != null && arrow.getCustomName().equals("viperbite")){
                if(!plugin.archeryListener.getEntitiesPoisoned().contains(event.getEntity())){
                    plugin.archeryListener.getEntitiesPoisoned().add(event.getEntity());
                    poisonTasks.put(event.getEntity(), Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
                        if(event.getEntity() instanceof Damageable && arrow.getShooter() instanceof Player){
                            ((Damageable) event.getEntity()).damage(1.016, (Player) arrow.getShooter());
                            event.getEntity().setVelocity(new Vector(0,0,0));
                        }
                    }, 20, 20));
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        poisonTasks.get(event.getEntity()).cancel();
                        plugin.archeryListener.getEntitiesPoisoned().remove(event.getEntity());
                    }, 162);
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
