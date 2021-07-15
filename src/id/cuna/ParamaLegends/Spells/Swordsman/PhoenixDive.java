package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhoenixDive {

    private final ParamaLegends plugin;
    private final SwordsmanListener swordsmanListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final HashMap<Player, BukkitTask> playerCheckVelocityTasks = new HashMap<Player, BukkitTask>();

    public PhoenixDive(ParamaLegends plugin, SwordsmanListener swordsmanListener){
        this.plugin = plugin;
        this.swordsmanListener = swordsmanListener;
    }

    public void castPhoenixDive(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            swordsmanListener.sendCooldownMessage(player, "Phoenix Dive");
        } else {
            if(swordsmanListener.subtractMana(player, 100)){
                Vector dive = player.getLocation().getDirection().setY(0).normalize();
                dive.setY(1);
                player.setVelocity(dive);
                player.getWorld().spawnParticle(Particle.LAVA, player.getEyeLocation(), 16, 1, 0.5, 1, 0);
                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 16, 1, 0.5, 1, 0);
                playerCheckVelocityTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if(player.getVelocity().getX() == 0d && player.getVelocity().getZ() == 0d){
                        player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 16, 1, 0.5, 1, 0);
                        player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 16, 1, 0.5, 1, 0);
                        List<Entity> entities = player.getNearbyEntities(2.5,2.5,2.5);
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
                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        swordsmanListener.sendNoLongerCooldownMessage(player, "Phoenix Dive");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 300);
            }
        }
    }

}
