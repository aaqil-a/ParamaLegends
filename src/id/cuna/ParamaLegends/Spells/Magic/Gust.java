package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Gust {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;
    private final List<String> playerCooldowns = new ArrayList<>();

    public Gust(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castGust(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            magicListener.sendCooldownMessage(player, "Gust");
        } else if (magicListener.subtractMana(player, 30)) {
            playerCooldowns.add(player.getUniqueId().toString());
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
            BlockFace facing = player.getFacing();
            if(direction.getX() < Math.sin(Math.PI/8) && direction.getX() > -1*Math.sin(Math.PI/8)){
                if(direction.getZ() >= 0){
                    boxX1 += 2.5;
                    boxX2 -= 2.5;
                    boxZ2 += 4.5;
                    knockback.setZ(4);
                } else {
                    boxX1 -= 2.5;
                    boxX2 += 2.5;
                    boxZ2 -= 4.5;
                    knockback.setZ(-4);
                }
            } else if(direction.getZ() < Math.sin(Math.PI/8) && direction.getZ() > -1*Math.sin(Math.PI/8)) {
                if(direction.getX() >= 0){
                    boxZ1 += 2.5;
                    boxZ2 -= 2.5;
                    boxX2 += 4.5;
                    knockback.setX(4);
                } else {
                    boxZ1 -= 2.5;
                    boxZ2 += 2.5;
                    boxX2 -= 4.5;
                    knockback.setX(-4);
                }
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)){
                boxX2 += 3.5;
                boxZ2 += 3.5;
                knockback.setX(2);
                knockback.setZ(2);
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 3.5;
                boxZ2 += 3.5;
                knockback.setX(-2);
                knockback.setZ(2);
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 3.5;
                boxZ2 -= 3.5;
                knockback.setX(-2);
                knockback.setZ(-2);
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)) {
                boxX2 += 3.5;
                boxZ2 -= 3.5;
                knockback.setX(2);
                knockback.setZ(-2);
            }
            BoundingBox gustBox = new BoundingBox(boxX1,boxY1, boxZ1, boxX2, boxY2, boxZ2);
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)).add(new Vector(0,0.5,0)), 8, 1, 0.5, 1, 0);
            List<Entity> entities = player.getWorld().getNearbyEntities(gustBox).stream().toList();
            for(Entity knocked : entities){
                if(knocked.equals(player) || knocked instanceof Villager){
                    continue;
                }
                if(knocked instanceof Damageable){
                    plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                    Vector velocity = knocked.getVelocity();
                    knocked.setVelocity(velocity.add(knockback));
                    ((Damageable) knocked).damage(2.069, player);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    magicListener.sendNoLongerCooldownMessage(player, "Gust");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 200);
        }
    }

}
