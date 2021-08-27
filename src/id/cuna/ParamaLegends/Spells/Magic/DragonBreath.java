package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.List;

public class DragonBreath implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 200;

    public DragonBreath(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Dragon's Breath");
        } else if (playerParama.subtractMana(manaCost)) {
            playerParama.addToCooldown(this);
            Player player = playerParama.getPlayer();
            Location location = player.getLocation();
            double playerX = location.getX();
            double playerY = location.getY();
            double playerZ = location.getZ();
            double boxX1 = playerX, boxX2 = playerX, boxZ1 = playerZ, boxZ2 = playerZ;
            double boxY1 = playerY - 3, boxY2 = playerY + 3;
            Vector direction = player.getLocation().getDirection();
            direction.setY(0);
            direction.normalize();
            if(direction.getX() < Math.sin(Math.PI/8) && direction.getX() > -1*Math.sin(Math.PI/8)){
                if(direction.getZ() >= 0){
                    boxX1 += 3;
                    boxX2 -= 3;
                    boxZ2 += 8;
                } else {
                    boxX1 -= 3;
                    boxX2 += 3;
                    boxZ2 -= 8;
                }
            } else if(direction.getZ() < Math.sin(Math.PI/8) && direction.getZ() > -1*Math.sin(Math.PI/8)) {
                if(direction.getX() >= 0){
                    boxZ1 += 3;
                    boxZ2 -= 3;
                    boxX2 += 8;
                } else {
                    boxZ1 -= 3;
                    boxZ2 += 3;
                    boxX2 -= 8;
                }
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)){
                boxX2 += 5;
                boxZ2 += 5;
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 5;
                boxZ2 += 5;
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 5;
                boxZ2 -= 5;
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)) {
                boxX2 += 5;
                boxZ2 -= 5;
            }
            BoundingBox gustBox = new BoundingBox(boxX1,boxY1, boxZ1, boxX2, boxY2, boxZ2);
            Location breathLocation = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(5));
            breathLocation.add(0,0.5,0);

            playerParama.addTask("DRAGONBREATH",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, breathLocation, 64, 1, 0, 1, 0);
                        List<Entity> entities = player.getWorld().getNearbyEntities(gustBox).stream().toList();
                        for(Entity knocked : entities){
                            if(knocked.equals(player)){
                                continue;
                            }
                            if(knocked instanceof Damageable){
                                plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                                ((Damageable) knocked).damage(10.069, player);
                            }
                        }
                    }, 3, 20));
            Bukkit.getScheduler().runTaskLater(plugin, ()-> playerParama.cancelTask("DRAGONBREATH"),200);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Dragon's Breath");
                    playerParama.removeFromCooldown(this);
                }
            }, 400);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
