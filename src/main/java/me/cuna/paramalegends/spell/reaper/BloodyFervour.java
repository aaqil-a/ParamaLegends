package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.AttackParama;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BloodyFervour implements AttackParama {

    private final ParamaLegends plugin;

    public BloodyFervour(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama playerParama, Entity entity, double damage){
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
        entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation(), 2, 0.5, 0.5, 0.5, 0);
    }
}
