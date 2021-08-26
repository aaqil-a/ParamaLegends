package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.ArrowParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

public class Blast implements ArrowParama, Listener {

    private final int manaCost = 60;

    public Blast(ParamaLegends plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void shootArrow(PlayerParama player, Entity entity){
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(player.subtractMana(manaCost)){
                arrow.setCustomName("blast");
            }
        }
    }

    //Deal when projectile hits block or entity
    @EventHandler
    public void effectSpell(ProjectileHitEvent event){
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

    public int getManaCost(){
        return manaCost;
    }
}
