package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Ignite implements Listener {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;
    private final List<String> playerCooldowns = new ArrayList<>();

    private final HashMap<Entity, Integer> entityIgnitedDuration = new HashMap<>();
    private final HashMap<Entity, BukkitTask> entityIgnitedTasks = new HashMap<>();

    public Ignite(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castIgnite(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            magicListener.sendCooldownMessage(player, "Ignite");
        } else if (magicListener.subtractMana(player, 20)) {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0,
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
                if(ignited instanceof Player){
                    continue;
                }
                if(ignited instanceof Damageable){
                    plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                    entityIgnitedDuration.put(ignited, 5);
                    entityIgnitedTasks.put(ignited,
                            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                int duration = entityIgnitedDuration.get(ignited);
                                duration--;
                                if (duration >= 0){
                                    ignited.getWorld().spawnParticle(Particle.SMALL_FLAME, ignited.getLocation().add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
                                    entityIgnitedDuration.replace(ignited, duration);
                                    ((Damageable) ignited).damage(3.069, player);
                                } else {
                                    entityIgnitedTasks.get(ignited).cancel();
                                    entityIgnitedTasks.remove(ignited);
                                    entityIgnitedDuration.remove(ignited);
                                }
                            }, 0, 20));
                }
            }
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    magicListener.sendNoLongerCooldownMessage(player, "Ignite");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 140);
        }
    }

}
