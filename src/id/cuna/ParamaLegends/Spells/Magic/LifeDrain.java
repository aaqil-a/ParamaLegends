package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class LifeDrain implements Listener {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;

    private final HashMap<Player, BukkitTask> playerLifeDrainTasks = new HashMap<>();

    public LifeDrain(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castLifeDrain(Player player){
        if(playerLifeDrainTasks.containsKey(player)){
            player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
            playerLifeDrainTasks.get(player).cancel();
            playerLifeDrainTasks.remove(player);
        } else {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    Entity drained = rayTrace.getHitEntity();
                    if(drained instanceof Damageable && !(drained instanceof ArmorStand)){
                        player.sendMessage(ChatColor.GREEN + "Life Drain activated.");
                        playerLifeDrainTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            if(drained.isDead() || player.isDead()){
                                player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
                                playerLifeDrainTasks.get(player).cancel();
                                playerLifeDrainTasks.remove(player);
                            } else if(magicListener.subtractMana(player, 10)){
                                player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(new Vector(0,1,0)), 4, 0.5, 0.5, 0.5, 0);
                                plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                                ((Damageable) drained).damage(3.069, player);
                                if(player.getHealth() <= 19){
                                    player.setHealth(player.getHealth()+1);
                                }
                            } else {
                                player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
                                playerLifeDrainTasks.get(player).cancel();
                                playerLifeDrainTasks.remove(player);
                            }
                        }, 0, 20));
                    }
                }
            }
        }
    }

}
