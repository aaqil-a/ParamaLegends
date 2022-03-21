package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ArcheryListener;
import me.cuna.paramalegends.spell.ArrowParama;
import net.royawesome.jlibnoise.module.modifier.Abs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BloodRain implements Listener, ArrowParama {

    private final ParamaLegends plugin;
    private final int manaCost = 100;
    private final int cooldown = 700;

    public BloodRain(ParamaLegends plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void shootArrow(PlayerParama playerParama, Entity entity){
      if(!playerParama.checkCooldown(this)){
          playerParama.addToCooldown(this);
          if(entity instanceof AbstractArrow){
              AbstractArrow arrow = (AbstractArrow) entity;
              arrow.setMetadata("bloodArrow", new FixedMetadataValue(plugin, true));
          }
          Bukkit.getScheduler().runTaskLater(plugin, ()->{
              if(playerParama.checkCooldown(this)){
                  plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Blood Rain");
                  playerParama.removeFromCooldown(this);
              }
          }, cooldown);
      }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if(event.getEntity().hasMetadata("bloodArrow")){
            if(event.getEntity().getShooter() instanceof Player){
                Player player = (Player) event.getEntity().getShooter();
                PlayerParama playerParama = plugin.playerManager.getPlayerParama(player);
                Location location = event.getEntity().getLocation();
                playerParama.addTask("BLOODARROW", Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(Entity entity : Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 3.5, 3.5, 3.5)){
                        if(entity instanceof LivingEntity){
                            LivingEntity hit = (LivingEntity) entity;
                            hit.damage(6, player);
                        }
                    }
                }, 0, 20));
                Random rand = new Random();
                playerParama.addTask("BLOODARROWEFFECT", Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(int x = -3; x<4; x++){
                        for(int z = -3; z<4; z++){
                            if(rand.nextInt(5)==0)
                            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.FALLING_LAVA, location.clone().add(x,2,z), 4, 0.5, 0, 0.5);
                        }
                    }
                }, 0, 2));
                playerParama.addTask("BLOODARROWEFFECT2", Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(int x = -3; x<4; x++){
                        for(int z = -3; z<4; z++){
                            if(rand.nextInt(3)==0)
                                Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.VILLAGER_ANGRY, location.clone().add(x,2.3,z), 4, 0.5, 0, 0.5);
                        }
                    }
                }, 0, 10));
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    playerParama.cancelTask("BLOODARROW");
                    playerParama.cancelTask("BLOODARROWEFFECT");
                    playerParama.cancelTask("BLOODARROWEFFECT2");
                }, 201);
            }

        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown(){
        return cooldown;
    }
}
