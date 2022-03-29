package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class    BloodyFervour implements AttackParama {

    private final ParamaLegends plugin;
    private final int cooldown = 202;

    public BloodyFervour(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama playerParama, Entity entity, double damage){
        if (!playerParama.checkCooldown(this)){
            if (entity instanceof LivingEntity){
                Player player = playerParama.getPlayer();
                double currHealth = player.getHealth();
                //prevent reviving
                if(currHealth <= 0) return;
                //set max life steal to 8
                damage = Math.min(damage, 8);
                double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                if (currHealth < maxHealth) {
                    player.setHealth(Math.min((currHealth + damage), maxHealth));
                }
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PHANTOM_BITE, 1f, 1.2f);
                entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation(), 2, 0.5, 0.5, 0.5, 0);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> playerParama.removeFromCooldown(this), cooldown);
            }
        }

    }
}
