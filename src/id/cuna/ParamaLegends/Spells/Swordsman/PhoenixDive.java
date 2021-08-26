package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassListener.SwordsmanListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhoenixDive implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 100;
    private final HashMap<Player, BukkitTask> playerCheckVelocityTasks = new HashMap<Player, BukkitTask>();

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
                playerCheckVelocityTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
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
                                    plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                                    burned.getWorld().spawnParticle(Particle.SMALL_FLAME, burned.getLocation().add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
                                    ((Damageable) burned).damage(2.072, player);
                                }, 0, 20);
                                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                    burnEntity.cancel();
                                },  62);
                            }
                        }
                        playerCheckVelocityTasks.get(player).cancel();
                        playerCheckVelocityTasks.remove(player);
                    }
                }, 3, 1));
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Phoenix Dive");
                        playerParama.removeFromCooldown(this);
                    }
                }, 300);
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }

}
