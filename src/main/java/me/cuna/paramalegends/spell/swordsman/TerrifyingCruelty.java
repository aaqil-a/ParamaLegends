package me.cuna.paramalegends.spell.swordsman;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class TerrifyingCruelty implements Listener, SpellParama {

    private final ParamaLegends plugin;;
    private final int manaCost = 200;
    private final int cooldown = 800;
    private final int duration = 242;

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
                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation().add(0,1,0), 8, 0.5, 0.5, 0.5, 0);
                for(Entity hit : entities){
                    if(hit instanceof Damageable && !(hit instanceof Player) && !(hit instanceof ArmorStand)){
                        BukkitTask hitEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            hit.getWorld().spawnParticle(Particle.SMOKE_NORMAL, hit.getLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);
                        }, 20, 20);
                        plugin.experienceListener.addExp(player, ClassGameType.SWORDSMAN, 1);
                        hit.setMetadata("TERRIFIED", new FixedMetadataValue(plugin, "TERRIFIED"));
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            hit.removeMetadata("TERRIFIED", plugin);
                            hitEffect.cancel();
                        }, duration);
                    }
                }
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Terrifying Cruelty");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            if(event.getDamager().hasMetadata("TERRIFIED")){
                event.setCancelled(true);
            }
        }
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
