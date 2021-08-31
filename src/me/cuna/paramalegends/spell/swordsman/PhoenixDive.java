package me.cuna.paramalegends.spell.swordsman;

import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class PhoenixDive implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 100;
    private final int cooldown = 300;
    private final int damage = 2;

    public PhoenixDive(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Phoenix Dive");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                Vector dive = player.getLocation().getDirection().setY(0).normalize();
                dive.setY(1);
                player.setVelocity(dive);
                player.getWorld().spawnParticle(Particle.LAVA, player.getEyeLocation(), 16, 1, 0.5, 1, 0);
                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 16, 1, 0.5, 1, 0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 2, false, false, false));
                playerParama.addTask("PHOENIXDIVE",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            if(player.getVelocity().getX() == 0d && player.getVelocity().getZ() == 0d || player.isOnGround()){
                                player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 16, 1, 0.5, 1, 0);
                                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 16, 1, 0.5, 1, 0);
                                player.removePotionEffect(PotionEffectType.JUMP);
                                List<Entity> entities = player.getNearbyEntities(3,2.5,3);
                                for(Entity burned : entities){
                                    if(burned instanceof Player){
                                        continue;
                                    }
                                    if(burned instanceof Damageable){
                                        BukkitTask burnEntity = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                            plugin.experienceListener.addExp(player, ClassGameType.SWORDSMAN, 1);
                                            burned.getWorld().spawnParticle(Particle.SMALL_FLAME, burned.getLocation().add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
                                            ((Damageable) burned).damage(damage+0.072, player);
                                        }, 0, 20);
                                        Bukkit.getScheduler().runTaskLater(plugin, burnEntity::cancel,  62);
                                    }
                                }
                                playerParama.cancelTask("PHOENIXDIVE");
                            }
                        }, 3, 1));
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Phoenix Dive");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
