package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.ArrowParama;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ViperBite implements Listener, ArrowParama {

    private final ParamaLegends plugin;
    private final int manaCost = 15;
    private final int damage = 1;
    private final int duration = 162;

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
            if(arrow.getCustomName() != null && arrow.getCustomName().equals("viperbite") && arrow.getShooter() instanceof Player){
                Entity hit = event.getEntity();
                if(!hit.hasMetadata("POISONPARAMA")){
                    hit.setMetadata("POISONPARAMA",new FixedMetadataValue(plugin,"POISONPARAMA"));
                    PlayerParama player = plugin.getPlayerParama((Player) arrow.getShooter());
                    player.addTask("VIPERBITE"+event.getEntity().getUniqueId().toString(),
                        Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
                            if(event.getEntity() instanceof Damageable && arrow.getShooter() instanceof Player){
                                ((Damageable) event.getEntity()).damage(damage+0.016, (Player) arrow.getShooter());
                                event.getEntity().setVelocity(new Vector(0,0,0));
                            }
                        }, 20, 20));
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        player.cancelTask("VIPERBITE"+event.getEntity().getUniqueId().toString());
                        hit.removeMetadata("POISONPARAMA", plugin);
                    }, duration);
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
