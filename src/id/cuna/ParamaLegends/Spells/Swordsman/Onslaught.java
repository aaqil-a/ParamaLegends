package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.List;

public class Onslaught implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 100;
    private final HashMap<Player, BukkitTask> playerOnslaughtTasks = new HashMap<>();

    public Onslaught(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Onslaught");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                swordsAnimation(player);
                List<Entity> entities = player.getNearbyEntities(3.5,3,3.5);
                playerOnslaughtTasks.put(player,Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    for(Entity hit : entities){
                        if(hit instanceof Damageable && !(hit instanceof ArmorStand) && !(hit instanceof Player)){
                            plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, hit.getLocation().add(0, 1,0), 1, 0, 0, 0, 0);
                            ((Damageable) hit).damage(12.072, player);
                        }
                    }
                }, 0, 6));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerOnslaughtTasks.containsKey(player)){
                        playerOnslaughtTasks.get(player).cancel();
                        playerOnslaughtTasks.remove(player);
                    }
                }, 19);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Onslaught");
                        playerParama.removeFromCooldown(this);
                    }
                }, 480);
            }
        }
    }

    public void swordsAnimation(Player player){
        ArmorStand sword1 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(-90,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });
        ArmorStand sword2 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(90,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });
        ArmorStand sword3 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(180,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });
        ArmorStand sword4 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(0,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });

        Location swordLocation1 = player.getLocation().add(0,-0.2,-0.8);
        swordLocation1.setDirection(new Vector(1, 0, 0));
        Location swordLocation2 = player.getLocation().add(0,-0.2,0.8);
        swordLocation2.setDirection(new Vector(-1, 0, 0));
        Location swordLocation3 = player.getLocation().add(-0.8,-0.2,0);
        swordLocation3.setDirection(new Vector(0, 0, -1));
        Location swordLocation4 = player.getLocation().add(0.8,-0.2,0);
        swordLocation4.setDirection(new Vector(0, 0, 1));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            sword1.teleport(swordLocation1);
            sword2.teleport(swordLocation2);
            sword3.teleport(swordLocation3);
            sword4.teleport(swordLocation4);
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            sword1.teleport(sword1.getLocation().add(0.5, 0, 0));
            sword2.teleport(sword2.getLocation().add(-0.5,0,0));
            sword3.teleport(sword3.getLocation().add(0,0,-0.5));
            sword4.teleport(sword4.getLocation().add(0,0,0.5));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            swordAnimation.cancel();
            sword1.remove();
            sword2.remove();
            sword3.remove();
            sword4.remove();
        }, 10);

    }
    public int getManaCost(){
        return manaCost;
    }
}
