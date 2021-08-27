package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.function.Predicate;

public class Nova implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 600;

    public Nova(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama) {
        if (playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Nova");
        } else {
            Player player = playerParama.getPlayer();
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 50, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location locationExplosion = new Location(player.getWorld(), 0, 0, 0);
            if (rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    locationExplosion = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    locationExplosion = rayTrace.getHitBlock().getLocation();
                }
            } else {
                plugin.sendOutOfRangeMessage(playerParama);
                return;
            }
            if (playerParama.subtractMana(manaCost)) {
                Location finalLocationExplosion = locationExplosion.clone().add(0, 1, 0);
                Location startExplosionFlash = locationExplosion.clone().add(0, 40, 0);
                player.getWorld().strikeLightningEffect(finalLocationExplosion);
                playerParama.addTask("PREEXPLOSIONEFFECT", Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, finalLocationExplosion, 16, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, finalLocationExplosion, 16, 0.5, 2, 0.5, 0);
                }, 2, 10));
                playerParama.addTask("FIREWORKEFFECT",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            plugin.magicListener.createFireworkEffect(finalLocationExplosion, player);
                        }, 0, 20) );
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.cancelTask("FIREWORKEFFECT");
                }, 62);
                playerParama.addTask("FIREWORKEFFECT2",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            plugin.magicListener.createFireworkEffect(finalLocationExplosion, player);
                        }, 70, 10));
                playerParama.addTask("FIREWORKEFFECT3",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            player.getWorld().spawnParticle(Particle.FLASH, startExplosionFlash.add(0, -2, 0), 16, 0, 0, 0, 0);
                        }, 102, 1));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.cancelTask("FIREWORKEFFECT2");
                }, 102);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.cancelTask("PREEXPLOSIONEFFECT");
                    playerParama.cancelTask("FIREWORKEFFECT3");
                    player.getWorld().createExplosion(finalLocationExplosion, 8F, true, true, player);
                    List<Entity> entities = player.getWorld().getNearbyEntities(finalLocationExplosion, 8, 10, 8).stream().toList();
                    for (Entity exploded : entities) {
                        if (exploded instanceof Damageable) {
                            plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                            ((Damageable) exploded).damage(60.069, player);
                        }
                    }
                }, 125);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (playerParama.checkCooldown(this)) {
                        plugin.sendNoLongerCooldownMessage(playerParama, "Nova");
                        playerParama.removeFromCooldown(this);
                    }
                }, 2400);
            }
        }
    }
    public int getManaCost(){
        return manaCost;
    }
}
