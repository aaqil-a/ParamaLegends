package me.cuna.paramalegends.spell.swordsman;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Superconducted implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 300;
    private final int cooldown = 1200;
    private final int damage = 20;

    public Superconducted(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Superconducted");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                List<Entity> entities = player.getNearbyEntities(5,4,5);
                createFireworkEffect(player.getLocation(), player);
                List<Entity> toDamage = new ArrayList<Entity>();
                for(Entity hit : entities){
                    if(hit instanceof LivingEntity && !(hit instanceof Player) && !(hit instanceof ArmorStand)){
                        hit.setMetadata("SUPERCONDUCTED", new FixedMetadataValue(plugin, "SUPERCONDUCTED"));
                        ((LivingEntity) hit).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8, 5, false, false, false));
                        toDamage.add(hit);
                    }
                }
                int delay = 10;
                for(Entity damaged : toDamage){
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if(damaged instanceof Damageable){
                            plugin.experienceListener.addExp(player, ClassGameType.SWORDSMAN, 1);
                            ((Damageable) damaged).damage(damage+0.072, player);
                            createFireworkEffect(damaged.getLocation(), player);
                        }
                    }, delay);
                    delay += 10;
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for(Entity hit: entities){
                        if(hit instanceof LivingEntity && !(hit instanceof Player)){
                            hit.removeMetadata("SUPERCONDUCTED", plugin);
                        }
                    }
                }, 160);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Superconducted");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
            }
        }
    }

    public void createFireworkEffect(Location location, Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(location.add(new Vector(0,1,0)), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.STAR)
                .flicker(false).trail(false).withColor(Color.WHITE, Color.NAVY, Color.SILVER).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.setSilent(true);
        firework.detonate();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            if(event.getDamager().hasMetadata("SUPERCONDUCTED")){
                event.setCancelled(true);
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}