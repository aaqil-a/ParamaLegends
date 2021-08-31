package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.ArrowParama;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class Neurotoxin implements Listener, ArrowParama {

    private final ParamaLegends plugin;
    private final int manaCost = 50;
    private final int damage = 2;
    private final int duration = 162;

    public Neurotoxin(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void shootArrow(PlayerParama player, Entity entity){
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(player.subtractMana(manaCost)){
                arrow.setCustomName("neurotoxin");
            }
        }
    }

    @EventHandler
    public void hitArrow(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getCustomName() != null && arrow.getCustomName().equals("neurotoxin") && arrow.getShooter() instanceof Player){
                Entity hit = event.getEntity();
                if(!hit.hasMetadata("POISONPARAMA")){
                    hit.setMetadata("POISONPARAMA",new FixedMetadataValue(plugin,"POISONPARAMA"));
                    PlayerParama player = plugin.getPlayerParama((Player) arrow.getShooter());
                    player.addTask("NEUROTOXIN"+event.getEntity().getUniqueId().toString(),
                            Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                                ((LivingEntity) event.getEntity()).damage(damage+0.016, (Entity) arrow.getShooter());
                                ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1));
                                event.getEntity().setVelocity(new Vector(0,0,0));
                            }, 20, 20));
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        player.cancelTask("NEUROTOXIN"+event.getEntity().getUniqueId().toString());
                        hit.removeMetadata("POISONPARAMA",plugin);
                    }, duration);
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getDuration(){return duration;}

}
