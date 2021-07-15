package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerrifyingCruelty implements Listener {

    private final ParamaLegends plugin;
    private final SwordsmanListener swordsmanListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<Entity> entitiesTerrified = new ArrayList<Entity>();

    public TerrifyingCruelty(ParamaLegends plugin, SwordsmanListener swordsmanListener){
        this.plugin = plugin;
        this.swordsmanListener = swordsmanListener;
    }

    public void castTerrifyingCruelty(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            swordsmanListener.sendCooldownMessage(player, "Terrifying Cruelty");
        } else {
            if(swordsmanListener.subtractMana(player, 200)){
                List<Entity> entities = player.getNearbyEntities(3.5,3,3.5);
                player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation().add(0,1,0), 8, 0.5, 0.5, 0.5, 0);
                for(Entity hit : entities){
                    if(hit instanceof Damageable && !(hit instanceof Player) && !(hit instanceof ArmorStand)){
                        BukkitTask hitEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            hit.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, hit.getLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);
                        }, 20, 20);
                        plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                        entitiesTerrified.add(hit);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            entitiesTerrified.remove(hit);
                            hitEffect.cancel();
                        }, 120);
                    }
                }
                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        swordsmanListener.sendNoLongerCooldownMessage(player, "Terrifying Cruelty");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 600);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            if(entitiesTerrified.contains(event.getDamager())){
                Random rand = new Random();
                if(rand.nextInt(100) < 60){
                    event.setCancelled(true);
                }
            }
        }
    }

    public List<Entity> getEntitiesTerrified(){
        return entitiesTerrified;
    }

}
