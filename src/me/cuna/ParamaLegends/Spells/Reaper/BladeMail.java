package me.cuna.ParamaLegends.Spells.Reaper;

import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;


public class BladeMail implements AttackParama {

    private final ParamaLegends plugin;

    public BladeMail(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama playerParama, Entity entity, double damage){
        if (!playerParama.checkCooldown(this)){
            Player player = playerParama.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 1.2f);
            if (entity instanceof LivingEntity){
                damage = Math.min(damage * 0.1, 10);
                ((LivingEntity) entity).damage(damage + 0.34, player);
            }
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerParama.removeFromCooldown(this);
            }, 82);
        }
    }
}
