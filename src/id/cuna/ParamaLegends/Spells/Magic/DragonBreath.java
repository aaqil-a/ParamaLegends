package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DragonBreath {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;

    private final List<String> playerCooldowns = new ArrayList<>();

    public DragonBreath(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castDragonBreath(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            magicListener.sendCooldownMessage(player, "Dragon's Breath");
        } else if (magicListener.subtractMana(player, 200)) {
            playerCooldowns.add(player.getUniqueId().toString());
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

            //test
            BukkitTask dragonBreath = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
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
            }, 3, 20);
            Bukkit.getScheduler().runTaskLater(plugin, dragonBreath::cancel, 125);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    magicListener.sendNoLongerCooldownMessage(player, "Dragon's Breath");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 400);
        }
    }
}
