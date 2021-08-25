package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BloodyFervour {

    private final ParamaLegends plugin;
    
    public BloodyFervour(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
    }

    public void castBloodyFervour (Player player, Entity entity, double damage){
        double currHealth = player.getHealth();
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (currHealth < maxHealth) {
            player.setHealth(Math.min((currHealth + damage), maxHealth));
        }
        entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation(), 2, 0.5, 0.5, 0.5, 0);
    }
}
