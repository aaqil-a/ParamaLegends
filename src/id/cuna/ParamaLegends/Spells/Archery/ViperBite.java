package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
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

public class ViperBite implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    public ViperBite(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castViperBite(Player player, Entity entity, boolean noMana){
        int manaCost = 15;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(archeryListener.subtractMana(player, 15)){
                arrow.setCustomName("viperbite");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getCustomName() != null && arrow.getCustomName().equals("viperbite")){
                if(!archeryListener.getEntitiesPoisoned().contains(event.getEntity())){
                    archeryListener.getEntitiesPoisoned().add(event.getEntity());
                    BukkitTask poison = Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
                        if(event.getEntity() instanceof Damageable && arrow.getShooter() instanceof Player){
                            ((Damageable) event.getEntity()).damage(1.016, (Player) arrow.getShooter());
                            event.getEntity().setVelocity(new Vector(0,0,0));
                        }
                    }, 20, 20);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        poison.cancel();
                        archeryListener.getEntitiesPoisoned().remove(event.getEntity());
                    }, 162);
                }
            }
        }
    }
}
