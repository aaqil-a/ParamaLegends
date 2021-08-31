package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.ArrowParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

public class Blast implements ArrowParama, Listener {

    private final int manaCost = 60;
    private final int blastDamage = 8;
    private final ParamaLegends plugin;

    public Blast(ParamaLegends plugin){
        this.plugin = plugin;
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
            }
        }
    }

    public void explodeArrow(Arrow arrow){
        if(arrow.getShooter() instanceof Player){
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                List<Entity> entities = arrow.getNearbyEntities(2,2,2);
                for(Entity hit : entities){
                    if(hit instanceof Mob || hit instanceof Player){
                        ((LivingEntity) hit).damage(blastDamage+0.016, (Player) arrow.getShooter());
                    }
                }
            }, 5);
            arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.5f);
            arrow.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, arrow.getLocation(), 8, 0.5, 0.5, 5d, 0);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
