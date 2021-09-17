package me.cuna.paramalegends.spell.swordsman;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Calamity implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 500;
    private final int cooldown = 2400;
    private final int duration = 302;
    private final int lightningDamage = 30;

    public Calamity(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Calamity");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                player.setMetadata("CALAMITY", new FixedMetadataValue(plugin, "CALAMITY"));
                playerParama.addTask("CALAMITY",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            List<Entity> entities = player.getNearbyEntities(10,10,10);
                            List<Entity> toDamage = new ArrayList<Entity>();
                            for(Entity hit : entities){
                                if(hit instanceof Monster) {
                                    toDamage.add(hit);
                                }
                            }
                            player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);
                            player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getEyeLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);

                            Random rand = new Random();
                            if(toDamage.size() > 0){
                                Entity striked = toDamage.get(rand.nextInt(toDamage.size()));
                                striked.getWorld().strikeLightningEffect(striked.getLocation());
                                striked.getWorld().spawnParticle(Particle.FLASH, striked.getLocation().add(new Vector(0,1,0)), 5);
                                ((Damageable) striked).damage(lightningDamage+0.072, player);
                                plugin.experienceListener.addExp(player, ClassGameType.SWORDSMAN, 1);
                            }
                        }, 62, 20));
                playerParama.addTask("CALAMITYEFFECT",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation().add(0,1,0), 1, 0.5, 0.5, 0.5, 0);
                            player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getEyeLocation().add(0,1.5,0), 1, 0.5, 0.5, 0.5, 0);
                            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getEyeLocation().add(0,1.5,0), 1, 0.5, 0.5, 0.5, 0);
                        }, 0, 5));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.cancelTask("CALAMITY");
                    player.removeMetadata("CALAMITY", plugin);
                    playerParama.cancelTask("CALAMITYEFFECT");
                }, duration);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Calamity");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
            }
        }
    }

    public int getManaCost() {
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
