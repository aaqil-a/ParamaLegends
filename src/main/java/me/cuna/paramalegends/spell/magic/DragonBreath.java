package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class DragonBreath implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 250;
    private final int cooldown = 700;
    private final int duration = 200;
    private final int damage = 5;
    private final int damageBonus = 2;
    private final int cooldownReduction = 40;


    public DragonBreath(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.hasTask("DRAGONBREATH") || playerParama.hasTask("DRAGONBREATHEFFECT")){
            playerParama.cancelTask("DRAGONBREATH");
            playerParama.cancelTask("DRAGONBREATHEFFECT");
            playerParama.getPlayer().sendMessage(ChatColor.GREEN+"Dragon's Breath deactivated.");
        } else if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Dragon's Breath");
        } else if (playerParama.subtractMana(manaCost)) {
            playerParama.addToCooldown(this);
            Player player = playerParama.getPlayer();
            int masteryLevel = playerParama.getMasteryLevel("dragonbreath");
            player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.7f, 1.5f);
            playerParama.addTask("DRAGONBREATHEFFECT",
                    Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                        spawnBreathParticles(player.getEyeLocation());
                    }, 1, 7));
            playerParama.addTask("DRAGONBREATH",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Location location = player.getEyeLocation();
                        Vector direction = location.getDirection();
                        //create rectangular box in front of player
                        Location start = location.clone().add(new Vector(-0.7*direction.getZ(), 0, 0.7*direction.getX()));
                        Location end = location.clone().add((new Vector(0.7*direction.getZ(), 0, -0.7*direction.getX())));
                        end.add(direction.clone().multiply(7));
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
                            if(hit instanceof Mob || hit instanceof Player){
                                plugin.gameManager.experience.addExp(player, ClassGameType.MAGIC, 1);
                                ((Damageable) hit).damage(damage+damageBonus*masteryLevel+0.069, player);
                                if(hit instanceof Monster || hit instanceof Phantom) playerParama.addMastery("dragonbreath", 1);
                            }
                        }
                    }, 1, 5));
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                if(playerParama.hasTask("DRAGONBREATH") || playerParama.hasTask("DRAGONBREAHEFFECT")){
                    playerParama.cancelTask("DRAGONBREATH");
                    playerParama.cancelTask("DRAGONBREATHEFFECT");
                    player.sendMessage(ChatColor.GREEN+"Dragon's Breath wore off.");
                }
            },duration);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Dragon's Breath");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown- (long) cooldownReduction *masteryLevel);
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
