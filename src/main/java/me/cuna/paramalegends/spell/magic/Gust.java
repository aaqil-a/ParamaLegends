package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class Gust implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 30;
    private final int cooldown = 200;
    private final int damage = 2;
    private final int cooldownReduction = 20;
    private final double velocityMultiplier = 0.1;

    public Gust(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Gust");
        } else if (playerParama.subtractMana(manaCost)) {
            playerParama.addToCooldown(this);
            int masteryLevel = playerParama.getMasteryLevel("gust");
            Player player = playerParama.getPlayer();
            Location location = player.getEyeLocation();
            Vector direction = player.getLocation().getDirection();
            direction.setY(0);
            direction.normalize();

            BoundingBox gustBox = plugin.gameClassManager.magic.getBoxInFrontOfLocation(location, direction, 10);

            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getEyeLocation().add(player.getLocation().getDirection().multiply(1)).add(new Vector(0,0.5,0)), 8, 1, 0.5, 1, 0);
            for(Entity knocked : player.getWorld().getNearbyEntities(gustBox)){
                if(knocked.equals(player)){
                    continue;
                }
                if(knocked instanceof Damageable && !(knocked instanceof ArmorStand)){
                    plugin.gameManager.experience.addExp(player, ClassGameType.MAGIC, 1);
                    knocked.setVelocity(knocked.getVelocity().add(direction.multiply(2+velocityMultiplier*masteryLevel)));
                    ((Damageable) knocked).damage(damage+0.069, player);
                    if(knocked instanceof Monster) playerParama.addMastery( "gust", 2);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Gust");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown- (long) cooldownReduction *masteryLevel);
        }
    }

    public void castSpellSelf(PlayerParama playerParama) {
        if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Gust");
        } else if (playerParama.subtractMana(manaCost)) {
            playerParama.addToCooldown(this);
            int masteryLevel = playerParama.getMasteryLevel("gust");
            Player player = playerParama.getPlayer();
            Vector direction = player.getLocation().getDirection();

            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)).add(new Vector(0,0.5,0)), 8, 1, 0.5, 1, 0);
            player.setVelocity(player.getVelocity().add(direction.multiply(1.1+velocityMultiplier*masteryLevel)));

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Gust");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown- (long) cooldownReduction *masteryLevel);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown() {return cooldown;}
}
