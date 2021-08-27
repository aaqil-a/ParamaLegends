package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TerrifyingCruelty implements Listener, SpellParama {

    private final ParamaLegends plugin;;
    private final int manaCost = 200;

    public TerrifyingCruelty(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Terrifying Cruelty");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                List<Entity> entities = player.getNearbyEntities(3.5,3,3.5);
                player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation().add(0,1,0), 8, 0.5, 0.5, 0.5, 0);
                for(Entity hit : entities){
                    if(hit instanceof Damageable && !(hit instanceof Player) && !(hit instanceof ArmorStand)){
                        BukkitTask hitEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            hit.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, hit.getLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);
                        }, 20, 20);
                        plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                        hit.setMetadata("TERRIFIED", new FixedMetadataValue(plugin, "TERRIFIED"));
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            hit.removeMetadata("TERRIFIED", plugin);
                            hitEffect.cancel();
                        }, 120);
                    }
                }
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Terrifying Cruelty");
                        playerParama.removeFromCooldown(this);
                    }
                }, 600);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            if(event.getDamager().hasMetadata("TERRIFIED")){
                Random rand = new Random();
                if(rand.nextInt(100) < 60){
                    event.setCancelled(true);
                }
            }
        }
    }

    public int getManaCost() {
        return manaCost;
    }

}
