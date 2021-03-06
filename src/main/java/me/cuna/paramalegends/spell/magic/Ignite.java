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
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;

import java.util.List;
import java.util.function.Predicate;

public class Ignite implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 20;
    private final int damage = 1;
    private final int duration = 105;
    private final int cooldown = 140;
    private final int damageBonus = 1;
    private final int cooldownReduction = 5;

    public Ignite(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Ignite");
        } else if (playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 10, FluidCollisionMode.NEVER, true, 1.2,
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
            int masteryLevel = playerParama.getMasteryLevel("ignite");
            player.getWorld().spawnParticle(Particle.FLAME, location.add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
            player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.add(0,1,0), 5,0.5,0.5,0.5,0);
            for(Entity ignited : player.getWorld().getNearbyEntities(location, 2,2,2)){
                if(ignited instanceof Player || ignited instanceof ArmorStand){
                    continue;
                }
                if(ignited instanceof Damageable){
                    plugin.gameManager.experience.addExp(player, ClassGameType.MAGIC, 1);
                    playerParama.addMastery( "ignite", 1);
                    playerParama.addTask("IGNITED"+ignited.getUniqueId().toString(),
                            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                ignited.getWorld().spawnParticle(Particle.SMALL_FLAME, ignited.getLocation().add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
                                if(ignited instanceof Monster || ignited instanceof Phantom || ignited instanceof Slime){
                                    ((Damageable) ignited).damage(damage+damageBonus*masteryLevel+0.069, player);
                                }
                            }, 0, 20));
                    Bukkit.getScheduler().runTaskLater(plugin, ()->playerParama.cancelTask("IGNITED"+ignited.getUniqueId().toString()), duration);
                }
            }
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Ignite");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown- (long) cooldownReduction *masteryLevel);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown() { return cooldown;}
}
