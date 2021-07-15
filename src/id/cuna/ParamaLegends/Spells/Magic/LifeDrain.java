package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LifeDrain implements Listener {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;

    private final List<String> playerCooldowns = new ArrayList<>();
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
            player.sendMessage(ChatColor.GREEN + "Life Drain activated.");
            playerLifeDrainTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if(magicListener.subtractMana(player, 10)){
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(new Vector(0,1,0)), 8, 1, 0.5, 1, 0);
                    List<Entity> entities = player.getNearbyEntities(2,2,2);
                    int count = 0;
                    for(Entity drained : entities){
                        if(drained instanceof Player){
                            continue;
                        }
                        if(drained instanceof Damageable){
                            count++;
                            plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                            ((Damageable) drained).damage(5.069, player);
                        }
                        if(count >= 4){
                            break;
                        }
                    }
                    if(count > 0){
                        if(count > 2 && player.getHealth() <= 18){
                            player.setHealth(player.getHealth()+2);
                        } else if(player.getHealth() <= 19){
                            player.setHealth(player.getHealth()+1);
                        }
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
