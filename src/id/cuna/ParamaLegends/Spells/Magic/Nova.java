package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Nova {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;

    private final List<String> playerCooldowns = new ArrayList<>();

    public Nova(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castNova(Player player) {
        if (playerCooldowns.contains(player.getUniqueId().toString())) {
            magicListener.sendCooldownMessage(player, "Nova");
        } else {
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
                magicListener.sendOutOfRangeMessage(player);
                return;
            }
            if (locationExplosion.distance(player.getLocation()) < 10) {
                player.sendMessage(ChatColor.GRAY + "Target too close to caster.");
                return;
            }
            if (magicListener.subtractMana(player, 600)) {
                Location finalLocationExplosion = locationExplosion.clone().add(0, 1, 0);
                Location startExplosionFlash = locationExplosion.clone().add(0, 40, 0);
                player.getWorld().strikeLightningEffect(finalLocationExplosion);
                BukkitTask preExplosionEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, finalLocationExplosion, 16, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, finalLocationExplosion, 16, 0.5, 2, 0.5, 0);
                }, 2, 10);
                BukkitTask fireworkEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    magicListener.createFireworkEffect(finalLocationExplosion, player);
                }, 0, 20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fireworkEffect.cancel();
                }, 62);
                BukkitTask fireworkEffect3 = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    magicListener.createFireworkEffect(finalLocationExplosion, player);
                }, 70, 10);
                BukkitTask flashEffect2 = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.FLASH, startExplosionFlash.add(0, -2, 0), 16, 0, 0, 0, 0);
                }, 102, 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fireworkEffect3.cancel();
                }, 102);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    preExplosionEffect.cancel();
                    flashEffect2.cancel();
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
                    if (playerCooldowns.contains(player.getUniqueId().toString())) {
                        magicListener.sendNoLongerCooldownMessage(player, "Nova");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 2400);
            }
        }
    }
}
