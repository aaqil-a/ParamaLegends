package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class GutPunch implements AttackParama {

    private final ParamaLegends plugin;
    private final int manaCost = 150;

    public GutPunch(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama playerParama, Entity entity, double damage) {
        if (!playerParama.checkCooldown(this) && playerParama.subtractMana(manaCost)) {
            if(entity instanceof LivingEntity){
                double entityHealth = ((LivingEntity) entity).getHealth();
                double maxHealth = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double percentHealth = (entityHealth / maxHealth);
                double finalDamage = damage + (30 * percentHealth) + 0.34;
                playerParama.addToCooldown(this);

                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 62, 3));
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 62, 3));
                ((LivingEntity) entity).damage(finalDamage, playerParama.getPlayer());

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        playerParama.removeFromCooldown(this);
                        plugin.sendNoLongerCooldownMessage(playerParama, "Gut Punch");
                    }
                }, 182);
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
