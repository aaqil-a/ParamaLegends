package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.ArrowParama;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Objects;

public class Retreat implements Listener, ArrowParama {

    private final ParamaLegends plugin;
    private final int manaCost = 70;

    public Retreat(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void shootArrow(PlayerParama playerParama, Entity entity){
        if(entity instanceof Arrow){
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                if(!player.hasMetadata("RETREATPARAMA")) {
                    double oldSpeed = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(oldSpeed*1.1);
                    player.setMetadata("RETREATPARAMA", new FixedMetadataValue( plugin, oldSpeed));
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        Bukkit.broadcastMessage(player.getMetadata("RETREATPARAMA").toString());
                        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(oldSpeed);
                        player.removeMetadata("RETREATPARAMA", plugin);
                    }, 65);
                }
                entity.setCustomName("retreat");
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    Arrow arrow2 = player.launchProjectile(Arrow.class, entity.getVelocity());
                    arrow2.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    arrow2.setDamage(((Arrow) entity).getDamage()*0.6);
                    arrow2.setBounce(false);
                    Vector velocity = player.getLocation().getDirection().setY(0).normalize();
                    player.setVelocity(velocity.multiply(-1).setY(0.3));
                }, 5);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getCustomName() != null && arrow.getCustomName().equals("retreat")){
                Entity entity = event.getEntity();
                if(entity instanceof LivingEntity){
                    LivingEntity hit = (LivingEntity) entity;
                    int oldTicks = hit.getMaximumNoDamageTicks();
                    hit.setMaximumNoDamageTicks(0);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        hit.setMaximumNoDamageTicks(oldTicks);
                    }, 10);
                }
            }
        }
    }
    public int getManaCost(){
        return manaCost;
    }

}
