package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

public class Blast implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    public Blast(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castBlast(Player player, Entity entity, boolean noMana){
        int manaCost = 60;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(archeryListener.subtractMana(player, manaCost)){
                arrow.setCustomName("blast");
            }
        }
    }

    //Deal when projectile hits block
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow && (projectile.getCustomName() != null)){
            Arrow arrow = (Arrow) projectile;
            if ("blast".equals(arrow.getCustomName())) {
                explodeArrow(arrow);
                event.setCancelled(true);
                arrow.remove();
            }
        }
    }

    public void explodeArrow(Arrow arrow){
        if(arrow.getShooter() instanceof Player){
            List<Entity> entities = arrow.getNearbyEntities(2,2,2);
            for(Entity hit : entities){
                if(hit instanceof LivingEntity && !(hit instanceof Player)){
                    ((LivingEntity) hit).damage(8.016, (Player) arrow.getShooter());
                }
            }
            arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.5f);
            arrow.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, arrow.getLocation(), 8, 0.5, 0.5, 0.5, 0);
        }
    }
}
