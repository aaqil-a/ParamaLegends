package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.util.RayTraceResult;

import java.util.List;
import java.util.function.Predicate;

public class Nova implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 600;
    private final int damage = 60;
    private final int cooldown = 2400;
    private final int cooldownReduction = 100;
    private final int damageBonus = 15;

    public Nova(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama) {
        if (playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Nova");
        } else {
            int masteryLevel = playerParama.getMasteryLevel("nova");
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
                            player.getWorld().spawnParticle(Particle.FLASH, startExplosionFlash.add(0, -2, 0), 16, 0, 0, 0, 0, null, true);
                        }, 102, 1));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.cancelTask("FIREWORKEFFECT2");
                }, 102);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.cancelTask("PREEXPLOSIONEFFECT");
                    playerParama.cancelTask("FIREWORKEFFECT3");
                    player.getWorld().createExplosion(finalLocationExplosion, 8F, true, true, player);
                    for (Entity exploded : player.getWorld().getNearbyEntities(finalLocationExplosion, 8, 10, 8)) {
                        if (exploded instanceof Damageable) {
                            plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                            ((Damageable) exploded).damage(damage+masteryLevel*damageBonus+0.069, player);
                            if(exploded instanceof Monster) plugin.magicListener.addMastery(playerParama, "nova", 10);
                        }
                    }
                }, 125);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (playerParama.checkCooldown(this)) {
                        plugin.sendNoLongerCooldownMessage(playerParama, "Nova");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown- (long) masteryLevel *cooldownReduction);
            }
        }
    }
    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown() { return cooldown;}
}
