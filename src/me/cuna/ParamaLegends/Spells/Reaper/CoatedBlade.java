package me.cuna.ParamaLegends.Spells.Reaper;

import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class CoatedBlade implements AttackParama {

    private final ParamaLegends plugin;

    public CoatedBlade(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama attacker, Entity entity, double damage) {
        if (!attacker.checkCooldown(this)) {
            if (entity instanceof LivingEntity) {
                Player player = attacker.getPlayer();
                attacker.addTask("COATEDBLADE",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation(),4, 0.5, 0.5, 0.5, 0);
                            ((LivingEntity) entity).damage(1.034, player);
                        }, 0, 20) );
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    attacker.cancelTask("COATEDBLADE");
                }, 42);
                attacker.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    attacker.removeFromCooldown(this);
                }, 80);
            }
        }
    }
}
