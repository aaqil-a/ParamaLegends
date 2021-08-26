package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.ArrowParama;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Retreat implements Listener, ArrowParama {

    private final ParamaLegends plugin;
    private final int manaCost = 70;

    private final List<Player> playersRetreatBoosted = new ArrayList<>();


    public Retreat(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void shootArrow(PlayerParama playerParama, Entity entity){
        if(entity instanceof Arrow){
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                if(!playersRetreatBoosted.contains(player)) {
                    double oldSpeed = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(oldSpeed*1.1);
                    playersRetreatBoosted.add(player);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(oldSpeed);
                        playersRetreatBoosted.remove(player);
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
