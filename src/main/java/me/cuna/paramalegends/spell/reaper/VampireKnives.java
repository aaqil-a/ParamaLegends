package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class VampireKnives implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 50;
    private final int cooldown = 20;
    private final int damage = 10;

    public VampireKnives(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (!playerParama.checkCooldown(this) && playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1f, 1.2f);
//            knivesAnimation(player);
            Location hitLocation = player.getLocation().add(player.getLocation().getDirection().multiply(3));
            Bukkit.getScheduler().runTaskLater(plugin,()->{
                for(Entity hit : player.getWorld().getNearbyEntities(hitLocation, 3, 3, 3)){
                    if(hit instanceof LivingEntity){
                        ((LivingEntity) hit).damage(damage);
                        Random rand = new Random();
                        if(rand.nextInt(2)==0){
                            if(player.getHealth()<19)player.setHealth(player.getHealth()+1);
                        }
                    }
                }
            }, 10);
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                playerParama.removeFromCooldown(this);
            }, cooldown);
        }
    }
//
//    public void knivesAnimation(Player player){
//        PlayerParama playerParama = plugin.playerManager.getPlayerParama(player);
//        Vector direction = player.getLocation().getDirection();
//        Vector left = new Vector(-1*direction.getZ(), 0, direction.getX());
//        left.normalize();
//        left.multiply(-0.2);
//        playerParama.addEntity("KNIVES1",
//                player.getWorld().spawn(player.getLocation().add(direction.clone().multiply(0.5)).add(0,-0.2,0)
//                        .add(left), ArmorStand.class, armorStand -> {
//                    armorStand.setInvisible(true);
//                    armorStand.setInvulnerable(true);
//                    armorStand.setGravity(false);
//                    armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
//                    armorStand.setCanPickupItems(false);
//                    armorStand.setCustomName("animation");
//
//                    EntityEquipment equipment1 = armorStand.getEquipment();
//                    assert equipment1 != null;
//                    equipment1.setItemInMainHand(new ItemStack(Material.GOLDEN_HOE));
//                }));
//        playerParama.addEntity("KNIVES2",
//                player.getWorld().spawn(player.getLocation().add(direction.clone().multiply(0.5)).add(0,-0.2,0).add(left), ArmorStand.class, armorStand -> {
//                    armorStand.setInvisible(true);
//                    armorStand.setInvulnerable(true);
//                    armorStand.setGravity(false);
//                    armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
//                    armorStand.setCanPickupItems(false);
//                    armorStand.setCustomName("animation");
//
//                    EntityEquipment equipment1 = armorStand.getEquipment();
//                    assert equipment1 != null;
//                    equipment1.setItemInMainHand(new ItemStack(Material.GOLDEN_HOE));
//                }));
//        playerParama.addEntity("KNIVES3",
//                player.getWorld().spawn(player.getLocation().add(direction).add(0,-0.2,0).add(left).setDirection(direction), ArmorStand.class, armorStand -> {
//
////                    armorStand.setInvisible(true);
//                    armorStand.setInvulnerable(true);
//                    armorStand.setGravity(false);
//                    armorStand.setRightArmPose(new EulerAngle(-1.6,0,0));
//                    armorStand.setCanPickupItems(false);
//                    armorStand.setCustomName("animation");
//
//                    EntityEquipment equipment1 = armorStand.getEquipment();
//                    assert equipment1 != null;
//                    equipment1.setItemInMainHand(new ItemStack(Material.GOLDEN_HOE));
//                }));
//        playerParama.addTask("KNIVESANIMATION",
//                Bukkit.getScheduler().runTaskTimer(plugin, ()->{
//                    ArmorStand knives1 = (ArmorStand) playerParama.getEntity("KNIVES1");
//                    ArmorStand knives2 = (ArmorStand) playerParama.getEntity("KNIVES2");
//                    ArmorStand knives3 = (ArmorStand) playerParama.getEntity("KNIVES3");
//                    knives1.teleport(knives1.getLocation().add(direction.clone().multiply(0.7)));
//                    knives2.teleport(knives2.getLocation().add(direction.clone().add(new Vector(-0.3*direction.getZ(), 0.0, 0.3*direction.getX())).normalize().multiply(0.7)));
//                    knives3.teleport(knives3.getLocation().add(direction.clone().add(new Vector(0.3*direction.getZ(), 0.0, -0.3*direction.getX())).normalize().multiply(0.7)));
//                }, 0, 1));
//        Bukkit.getScheduler().runTaskLater(plugin, ()->{
//            playerParama.cancelTask("KNIVESANIMATION");
//            playerParama.removeEntity("KNIVES1");
//            playerParama.removeEntity("KNIVES2");
//            playerParama.removeEntity("KNIVES3");
//        }, 10);
//
//    }

    public int getManaCost(){
        return manaCost;
    }

    public int getCooldown() {
        return cooldown;
    }
}
