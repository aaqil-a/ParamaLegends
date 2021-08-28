package me.cuna.ParamaLegends.Spells.Swordsman;

import me.cuna.ParamaLegends.ClassType;
import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

public class ShieldsUp implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 50;

    public ShieldsUp(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Shields Up");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                player.sendMessage(ChatColor.GREEN+"Shields Up activated.");
                shieldAnimation(player);
                player.setMetadata("SHIELDSUP", new FixedMetadataValue(plugin, "SHIELDSUP"));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.GREEN+"Shields Up wore off.");
                    player.removeMetadata("SHIELDSUP", plugin);
                }, 202);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Shields Up");
                        playerParama.removeFromCooldown(this);
                    }
                }, 400);
            }
        }
    }


    public void shieldAnimation(Player player){
        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.addEntity("SHIELD1",
                player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                    armorStand.setInvisible(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setGravity(false);
                    armorStand.setRotation(-90,0);
                    armorStand.setArms(true);
                    armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
                    armorStand.setCanPickupItems(false);
                    armorStand.setCustomName("animation");

                    EntityEquipment equipment1 = armorStand.getEquipment();
                    assert equipment1 != null;
                    equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
                }));
        playerParama.addEntity("SHIELD2",
                player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                    armorStand.setInvisible(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setGravity(false);
                    armorStand.setRotation(90,0);
                    armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
                    armorStand.setCanPickupItems(false);
                    armorStand.setCustomName("animation");

                    EntityEquipment equipment1 = armorStand.getEquipment();
                    assert equipment1 != null;
                    equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
                }));
        playerParama.addEntity("SHIELD3",
                player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                    armorStand.setInvisible(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setGravity(false);
                    armorStand.setRotation(180,0);
                    armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
                    armorStand.setCanPickupItems(false);
                    armorStand.setCustomName("animation");

                    EntityEquipment equipment1 = armorStand.getEquipment();
                    assert equipment1 != null;
                    equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
                }));
        playerParama.addEntity("SHIELD4",
                player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                    armorStand.setInvisible(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setGravity(false);
                    armorStand.setRotation(0,0);
                    armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));
                    armorStand.setCanPickupItems(false);
                    armorStand.setCustomName("animation");

                    EntityEquipment equipment1 = armorStand.getEquipment();
                    assert equipment1 != null;
                    equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
                }));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            playerParama.getEntity("SHIELD1").teleport(player.getLocation().add(0.5,-1,-0.8).setDirection(new Vector(1, 0, 0)));
            playerParama.getEntity("SHIELD2").teleport(player.getLocation().add(-0.5,-1,0.8).setDirection(new Vector(-1, 0, 0)));
            playerParama.getEntity("SHIELD3").teleport(player.getLocation().add(-0.8,-1,-0.5).setDirection(new Vector(0, 0, -1)));
            playerParama.getEntity("SHIELD4").teleport(player.getLocation().add(0.8,-1,0.5).setDirection(new Vector(0, 0, 1)));
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            ArmorStand shield1 = (ArmorStand) playerParama.getEntity("SHIELD1");
            ArmorStand shield2 = (ArmorStand) playerParama.getEntity("SHIELD2");
            ArmorStand shield3 = (ArmorStand) playerParama.getEntity("SHIELD3");
            ArmorStand shield4 = (ArmorStand) playerParama.getEntity("SHIELD4");
            shield1.teleport(shield1.getLocation().add(0, +0.6, 0));
            shield2.teleport(shield2.getLocation().add(0,+0.6,0));
            shield3.teleport(shield3.getLocation().add(0,+0.6,0));
            shield4.teleport(shield4.getLocation().add(0,+0.6,0));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            swordAnimation.cancel();
            playerParama.removeEntity("SHIELD1");
            playerParama.removeEntity("SHIELD2");
            playerParama.removeEntity("SHIELD3");
            playerParama.removeEntity("SHIELD4");
        }, 10);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            //Reflect damage if shields up
            if(player.hasMetadata("SHIELDSUP")){
                if(event.getDamager() instanceof Damageable){
                    Damageable attacker = (Damageable) event.getDamager();
                    plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                    attacker.damage(Math.floor(event.getDamage()*0.15)+0.072);
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }

}
