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

import java.util.List;

public class Gust implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 30;
    private final int cooldown = 200;
    private final int damage = 2;

    public Gust(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Gust");
        } else if (playerParama.subtractMana(manaCost)) {
            playerParama.addToCooldown(this);
            Player player = playerParama.getPlayer();
            Location location = player.getLocation();
            double playerX = location.getX();
            double playerY = location.getY();
            double playerZ = location.getZ();
            double boxX1 = playerX, boxX2 = playerX, boxZ1 = playerZ, boxZ2 = playerZ;
            double boxY1 = playerY - 3, boxY2 = playerY + 3;
            Vector knockback = new Vector(0, 0, 0);
            Vector direction = player.getLocation().getDirection();
            direction.setY(0);
            direction.normalize();
            if(direction.getX() < Math.sin(Math.PI/8) && direction.getX() > -1*Math.sin(Math.PI/8)){
                if(direction.getZ() >= 0){
                    boxX1 += 2.5;
                    boxX2 -= 2.5;
                    boxZ1 -= 1;
                    boxZ2 += 6;
                    knockback.setZ(4);
                } else {
                    boxX1 -= 2.5;
                    boxX2 += 2.5;
                    boxZ1 += 1;
                    boxZ2 -= 6;
                    knockback.setZ(-4);
                }
            } else if(direction.getZ() < Math.sin(Math.PI/8) && direction.getZ() > -1*Math.sin(Math.PI/8)) {
                if(direction.getX() >= 0){
                    boxZ1 += 2.5;
                    boxZ2 -= 2.5;
                    boxX1 -= 1;
                    boxX2 += 6;
                    knockback.setX(4);
                } else {
                    boxZ1 -= 2.5;
                    boxZ2 += 2.5;
                    boxX1 += 1;
                    boxX2 -= 6;
                    knockback.setX(-4);
                }
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)){
                boxX2 += 5;
                boxZ2 += 5;
                boxX1 -= 1;
                boxZ1 -= 1;
                knockback.setX(2);
                knockback.setZ(2);
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 5;
                boxZ2 += 5;
                boxX1 += 1;
                boxZ1 -= 1;
                knockback.setX(-2);
                knockback.setZ(2);
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 5;
                boxZ2 -= 5;
                boxX1 += 1;
                boxZ1 += 1;
                knockback.setX(-2);
                knockback.setZ(-2);
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)) {
                boxX2 += 5;
                boxZ2 -= 5;
                boxX1 -= 1;
                boxZ1 += 1;
                knockback.setX(2);
                knockback.setZ(-2);
            }
            BoundingBox gustBox = new BoundingBox(boxX1,boxY1, boxZ1, boxX2, boxY2, boxZ2);
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)).add(new Vector(0,0.5,0)), 8, 1, 0.5, 1, 0);
            for(Entity knocked : player.getWorld().getNearbyEntities(gustBox)){
                if(knocked.equals(player) || knocked instanceof Villager || knocked instanceof ArmorStand){
                    continue;
                }
                if(knocked instanceof Damageable){
                    plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                    Vector velocity = knocked.getVelocity();
                    knocked.setVelocity(velocity.add(knockback));
                    ((Damageable) knocked).damage(damage+0.069, player);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Gust");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown() {return cooldown;}
}
