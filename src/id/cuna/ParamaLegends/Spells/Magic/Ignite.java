package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;

import java.util.List;
import java.util.function.Predicate;

public class Ignite implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 20;

    public Ignite(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Ignite");
        } else if (playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 1.5,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(20));
            }
            player.getWorld().spawnParticle(Particle.FLAME, location.add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
            player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.add(0,1,0), 5,0.5,0.5,0.5,0);
            List<Entity> entities = player.getWorld().getNearbyEntities(location, 2,2,2).stream().toList();
            for(Entity ignited : entities){
                if(ignited instanceof Player || ignited instanceof ArmorStand){
                    continue;
                }
                if(ignited instanceof Damageable){
                    plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                    playerParama.addTask("IGNITED"+ignited.getUniqueId().toString(),
                            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                ignited.getWorld().spawnParticle(Particle.SMALL_FLAME, ignited.getLocation().add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
                                ((Damageable) ignited).damage(3.069, player);
                            }, 0, 20));
                    Bukkit.getScheduler().runTaskLater(plugin, ()->playerParama.cancelTask("IGNITED"+ignited.getUniqueId().toString()), 105);
                }
            }
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Ignite");
                    playerParama.removeFromCooldown(this);
                }
            }, 140);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
