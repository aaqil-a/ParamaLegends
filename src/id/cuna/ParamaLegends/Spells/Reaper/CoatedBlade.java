package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;


public class CoatedBlade implements AttackParama {

    private final ParamaLegends plugin;
    private final HashMap<Player, BukkitTask> poisonTasks = new HashMap<>();

    public CoatedBlade(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama attacker, Entity entity, double damage) {
        if (!attacker.checkCooldown(this)) {
            if (entity instanceof LivingEntity) {
                Player player = attacker.getPlayer();
                poisonTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation(),4, 0.5, 0.5, 0.5, 0);
                    ((LivingEntity) entity).damage(1.034, player);
                }, 0, 20));
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    poisonTasks.get(player).cancel();
                    poisonTasks.remove(player);
                }, 42);
                attacker.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    attacker.removeFromCooldown(this);
                }, 80);
            }
        }
    }
}
