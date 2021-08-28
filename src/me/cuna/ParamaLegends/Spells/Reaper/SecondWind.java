package me.cuna.ParamaLegends.Spells.Reaper;

import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class SecondWind implements AttackParama {

    private final ParamaLegends plugin;


    public SecondWind(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama playerParama, Entity entity, double damage){
        if (!playerParama.checkCooldown(this)){
            if (entity instanceof LivingEntity){
                Player player = playerParama.getPlayer();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 62, 2));
                player.sendMessage(ChatColor.GREEN+"You gain a second wind.");
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> playerParama.removeFromCooldown(this), 162);
            }
        }
    }

    public int getManaCost(){
        return 0;
    }

}
