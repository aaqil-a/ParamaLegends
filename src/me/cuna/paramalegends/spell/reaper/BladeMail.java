package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;


public class BladeMail implements AttackParama, Listener {

    private final ParamaLegends plugin;

    public BladeMail(ParamaLegends plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void attackEntity(PlayerParama playerParama, Entity entity, double damage){
        if (!playerParama.checkCooldown(this)){
            Player player = playerParama.getPlayer();
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 1.2f);
            if (entity instanceof LivingEntity){
                damage = Math.min(damage * 0.1, 10);
                damage = Math.ceil(damage);
                ((LivingEntity) entity).damage(damage + 0.0349, player);
            }
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerParama.removeFromCooldown(this);
            }, 82);
        }
    }

    //change blade mail death message
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
            Player attacker = (Player) event.getDamager();
            Player hit = (Player) event.getEntity();
            if(event.getFinalDamage() >= hit.getHealth()){
                String damageString = String.valueOf(event.getDamage());
                if(damageString.substring(damageString.indexOf(".")).length() >= 5){
                    String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+5);
                    if(key.equals(".0349") || key.equals(".0348")){
                        hit.setMetadata("BLADEMAIL", new FixedMetadataValue(plugin, attacker.getName()));
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(player.hasMetadata("BLADEMAIL")){
            event.setDeathMessage(player.getName()+" was killed trying to hurt "+player.getMetadata("BLADEMAIL").get(0).asString());
            player.removeMetadata("BLADEMAIL", plugin);
        }
    }
}
