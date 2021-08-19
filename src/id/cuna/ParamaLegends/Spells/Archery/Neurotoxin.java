package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class Neurotoxin implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    public Neurotoxin(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castNeurotoxin(Player player, Entity entity, boolean noMana){
        int manaCost = 50;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(archeryListener.subtractMana(player, manaCost)){
                arrow.setCustomName("neurotoxin");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getCustomName() != null && arrow.getCustomName().equals("neurotoxin")){
                if(!archeryListener.getEntitiesPoisoned().contains(event.getEntity())){
                    archeryListener.getEntitiesPoisoned().add(event.getEntity());
                    BukkitTask neurotoxin = Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
                        if(event.getEntity() instanceof LivingEntity && arrow.getShooter() instanceof Player){
                            ((LivingEntity) event.getEntity()).damage(2.016, (Entity) arrow.getShooter());
                            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1));
                        }
                    }, 20, 20);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        neurotoxin.cancel();
                        archeryListener.getEntitiesPoisoned().remove(event.getEntity());
                    }, 162);
                }
            }
        }
    }

}
