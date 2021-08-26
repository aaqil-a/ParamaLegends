package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.ArrowParama;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class HunterEye implements Listener, ArrowParama {

    private final ParamaLegends plugin;
    private final int manaCost = 20;
    private final List<Entity> entitiesHunterEye = new ArrayList<>();

    public HunterEye(ParamaLegends plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void shootArrow(PlayerParama player, Entity entity){
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(player.subtractMana(manaCost)){
                arrow.setCustomName("huntereye");
            }
        }
    }

    @EventHandler
    public void hitArrow(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getCustomName() != null && arrow.getCustomName().equals("huntereye")){
                if(!entitiesHunterEye.contains(event.getEntity())){
                    if(event.getEntity() instanceof LivingEntity && arrow.getShooter() instanceof Player){
                        entitiesHunterEye.add(event.getEntity());
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
                        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                            entitiesHunterEye.remove(event.getEntity());
                        }, 200);
                    }
                }
            }
        }
    }


    public int getManaCost(){
        return manaCost;
    }

    public List<Entity> getEntitiesHunterEye() {
        return entitiesHunterEye;
    }
}
