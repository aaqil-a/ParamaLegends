package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class CoatedBlade {

    private final ParamaLegends plugin;

    private final List<String> playerCooldowns = new ArrayList<>();

    public CoatedBlade(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
    }

    public void castCoatedBlade(Player attacker, Entity entity) {
        if (!playerCooldowns.contains(attacker.getUniqueId().toString())) {
            if (entity instanceof LivingEntity) {
                BukkitTask poison = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation(),4, 0.5, 0.5, 0.5, 0);
                    ((LivingEntity) entity).damage(1.034, attacker);
                }, 0, 20);
                Bukkit.getScheduler().runTaskLater(plugin, poison::cancel, 42);
                playerCooldowns.add(attacker.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerCooldowns.remove(attacker.getUniqueId().toString());
                }, 80);
            }
        }
    }
}
