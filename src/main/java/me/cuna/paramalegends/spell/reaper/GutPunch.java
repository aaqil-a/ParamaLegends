package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class GutPunch implements AttackParama {

    private final ParamaLegends plugin;
    private final int manaCost = 150;
    private final int cooldown = 182;

    public GutPunch(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama playerParama, Entity entity, double damage) {
        if (!playerParama.checkCooldown(this) && playerParama.subtractMana(manaCost)) {
            if(entity instanceof LivingEntity){
                double entityHealth = ((LivingEntity) entity).getHealth();
                double finalDamage = damage + entityHealth*0.2;
                finalDamage = Math.max(finalDamage, 15);
                finalDamage = Math.min(finalDamage,50);
                finalDamage = Math.ceil(finalDamage);
                playerParama.addToCooldown(this);

                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 62, 3));
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 62, 3));
                ((LivingEntity) entity).damage(finalDamage+0.034, playerParama.getPlayer());

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        playerParama.removeFromCooldown(this);
                        plugin.sendNoLongerCooldownMessage(playerParama, "Gut Punch");
                    }
                }, cooldown);
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }

    public int getCooldown() {
        return cooldown;
    }
}
