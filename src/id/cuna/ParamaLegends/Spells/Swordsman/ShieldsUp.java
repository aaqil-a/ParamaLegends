package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class ShieldsUp implements Listener {

    private final ParamaLegends plugin;
    private final SwordsmanListener swordsmanListener;

    private final List<Player> playersShielded = new ArrayList<Player>();
    private final List<String> playerCooldowns = new ArrayList<>();

    public ShieldsUp(ParamaLegends plugin, SwordsmanListener swordsmanListener){
        this.plugin = plugin;
        this.swordsmanListener = swordsmanListener;
    }

    public void castShieldsUp(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            swordsmanListener.sendCooldownMessage(player, "Shields Up");
        } else {
            if(swordsmanListener.subtractMana(player, 50)){
                player.sendMessage(ChatColor.GREEN+"Shields Up activated.");
                shieldAnimation(player);
                playersShielded.add(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.GREEN+"Shields Up wore off.");
                    playersShielded.remove(player);
                    //shieldEffect.cancel();
                }, 162);
                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        swordsmanListener.sendNoLongerCooldownMessage(player, "Shields Up");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 400);
            }
        }
    }


    public void shieldAnimation(Player player){
        ArmorStand sword1 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(-90,0);
            armorStand.setArms(true);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });
        ArmorStand sword2 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(90,0);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });
        ArmorStand sword3 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(180,0);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });
        ArmorStand sword4 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(0,0);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });

        Location swordLocation1 = player.getLocation().add(0.5,-1,-0.8);
        swordLocation1.setDirection(new Vector(1, 0, 0));
        Location swordLocation2 = player.getLocation().add(-0.5,-1,0.8);
        swordLocation2.setDirection(new Vector(-1, 0, 0));
        Location swordLocation3 = player.getLocation().add(-0.8,-1,-0.5);
        swordLocation3.setDirection(new Vector(0, 0, -1));
        Location swordLocation4 = player.getLocation().add(0.8,-1,0.5);
        swordLocation4.setDirection(new Vector(0, 0, 1));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            sword1.teleport(swordLocation1);
            sword2.teleport(swordLocation2);
            sword3.teleport(swordLocation3);
            sword4.teleport(swordLocation4);
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            sword1.teleport(sword1.getLocation().add(0, +0.6, 0));
            sword2.teleport(sword2.getLocation().add(0,+0.6,0));
            sword3.teleport(sword3.getLocation().add(0,+0.6,0));
            sword4.teleport(sword4.getLocation().add(0,+0.6,0));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            swordAnimation.cancel();
            sword1.remove();
            sword2.remove();
            sword3.remove();
            sword4.remove();
        }, 10);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            //Reflect damage if shields up
            if(playersShielded.contains(player)){
                if(event.getDamager() instanceof Damageable){
                    Damageable attacker = (Damageable) event.getDamager();
                    plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                    attacker.damage(Math.floor(event.getDamage()*0.15)+0.072);
                }
            }
        }
    }

    public List<Player> getPlayersShielded(){
        return playersShielded;
    }

}
