package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
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

public class HunterEye implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    private final List<Entity> entitiesHunterEye = new ArrayList<>();


    public HunterEye(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castHuntersEye(Player player, Entity entity, boolean noMana){
        int manaCost = 20;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(archeryListener.subtractMana(player, manaCost)){
                arrow.setCustomName("huntereye");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
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

    public List<Entity> getEntitiesHunterEye() {
        return entitiesHunterEye;
    }
}
