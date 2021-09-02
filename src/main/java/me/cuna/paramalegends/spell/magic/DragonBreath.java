package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class DragonBreath implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 200;
    private final int cooldown = 400;
    private final int duration = 200;
    private final int damage = 5;

    public DragonBreath(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Dragon's Breath");
        } else if (playerParama.subtractMana(manaCost)) {
            playerParama.addToCooldown(this);
            Player player = playerParama.getPlayer();
            playerParama.addTask("DRAGONBREATHEFFECT",
                    Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                        spawnBreathParticles(player.getEyeLocation());
                    }, 1, 10));
            playerParama.addTask("DRAGONBREATH",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Location location = player.getEyeLocation();
                        Vector direction = location.getDirection();
                        //create rectangular box in front of player
                        Location start = location.clone().add(new Vector(-1*direction.getZ(), 0, direction.getX()));
                        Location end = location.clone().add((new Vector(direction.getZ(), 0, -1*direction.getX())));
                        end.add(direction.clone().multiply(10));
                        //expand boxes y value
                        end.add(0, 2, 0);
                        start.add(0, -2 ,0);
                        BoundingBox breathBox = new BoundingBox(
                                start.getX(), start.getY(), start.getZ(),
                                end.getX(), end.getY(), end.getZ());
                        for(Entity hit : player.getWorld().getNearbyEntities(breathBox)){
                            if(hit.equals(player)){
                                continue;
                            }
                            if(hit instanceof Mob){
                                plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                                ((Mob) hit).damage(damage+0.069, player);
                            }
                        }
                    }, 1, 5));
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                playerParama.cancelTask("DRAGONBREATH");
                playerParama.cancelTask("DRAGONBREATHEFFECT");
            },duration);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Dragon's Breath");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown);
        }
    }

    public void spawnBreathParticles(Location location){
        Vector direction = location.getDirection();
        location.add(0, -0.4, 0);
        World world = location.getWorld();
        assert world != null;
        world.spawnParticle(Particle.DRAGON_BREATH, location.add(direction), 4, 0.2, 0.2, 0.2, 0);
        if(location.add(direction).getBlock().getType().isSolid()) return;
        world.spawnParticle(Particle.DRAGON_BREATH, location, 3, 0.2, 0.2, 0.2, 0);
        if(location.add(direction).getBlock().getType().isSolid()) return;
        world.spawnParticle(Particle.DRAGON_BREATH, location, 3, 0.2, 0.2, 0.2, 0);
        if(location.add(direction).getBlock().getType().isSolid()) return;
        world.spawnParticle(Particle.DRAGON_BREATH, location, 3, 0.2, 0.2, 0.2, 0);
        if(location.add(direction).getBlock().getType().isSolid()) return;
        world.spawnParticle(Particle.DRAGON_BREATH, location, 3, 0.2, 0.2, 0.2, 0);
        if(location.add(direction).getBlock().getType().isSolid()) return;
        world.spawnParticle(Particle.DRAGON_BREATH, location, 3, 0.2, 0.2, 0.2, 0);
        if(location.add(direction).getBlock().getType().isSolid()) return;
        world.spawnParticle(Particle.DRAGON_BREATH, location, 3, 0.2, 0.2, 0.2, 0);
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown(){return cooldown;}
}
