package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GutPunch {

    private final ParamaLegends plugin;
    private final ReaperListener reaperListener;

    private final List<String> playerCooldowns = new ArrayList<>();

    public GutPunch(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
        this.reaperListener = reaperListener;
    }

    public void castGutPunch (Player player , Entity entity) {
        if (!playerCooldowns.contains(player.getUniqueId().toString()) && reaperListener.subtractMana(player, 150)) {
            if(entity instanceof LivingEntity){
                double entityHealth = ((LivingEntity) entity).getHealth();
                double maxHealth = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double percentHealth = (entityHealth / maxHealth);
                double finalDamage = (30 * percentHealth) + 0.34;
                playerCooldowns.add(player.getUniqueId().toString());

                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 62, 3));
                ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 62, 3));

                ((LivingEntity) entity).damage(finalDamage, player);


                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        playerCooldowns.remove(player.getUniqueId().toString());
                        reaperListener.sendNoLongerCooldownMessage(player, "Gut Punch");
                    }
                }, 182);
            }
        }
    }

    public List<String> getPlayerCooldowns() {
        return playerCooldowns;
    }
}
