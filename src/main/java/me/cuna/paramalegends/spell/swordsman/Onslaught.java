package me.cuna.paramalegends.spell.swordsman;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;

public class Onslaught implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 100;
    private final int cooldown = 480;
    private final int damage = 6;

    public Onslaught(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Onslaught");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                swordsAnimation(player);
                List<Entity> entities = player.getNearbyEntities(3.5,3,3.5);
                for(Entity hit : entities){
                    if(hit instanceof LivingEntity){
                        hit.setMetadata("OLDTICKS", new FixedMetadataValue(plugin, ((LivingEntity) hit).getMaximumNoDamageTicks()));
                        ((LivingEntity) hit).setMaximumNoDamageTicks(5);
                    }
                }
                playerParama.addTask("ONSLAUGHT", Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    for(Entity hit : entities){
                        if(hit instanceof Damageable && !(hit instanceof ArmorStand) && !(hit instanceof Player)){
                            plugin.gameManager.experience.addExp(player, ClassGameType.SWORDSMAN, 1);
                            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, hit.getLocation().add(0, 1,0), 1, 0, 0, 0, 0);
                            ((Damageable) hit).damage(damage+0.072, player);
                        }
                    }
                }, 0, 3));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for(Entity hit : entities){
                        if(hit instanceof LivingEntity){
                            ((LivingEntity) hit).setMaximumNoDamageTicks(hit.getMetadata("OLDTICKS").get(0).asInt());
                            hit.removeMetadata("OLDTICKS", plugin);
                        }
                    }
                    playerParama.cancelTask("ONSLAUGHT");
                }, 19);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Onslaught");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
            }
        }
    }

    public void swordsAnimation(Player player){
        PlayerParama playerParama = plugin.playerManager.getPlayerParama(player);
        playerParama.addEntity("SWORD1",
                player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                    armorStand.setInvisible(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setGravity(false);
                    armorStand.setRotation(-90,0);
                    armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
                    armorStand.setCanPickupItems(false);
                    armorStand.setCustomName("animation");

                    EntityEquipment equipment1 = armorStand.getEquipment();
                    assert equipment1 != null;
                    equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                }));
        playerParama.addEntity("SWORD2",
            player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                armorStand.setInvisible(true);
                armorStand.setInvulnerable(true);
                armorStand.setGravity(false);
                armorStand.setRotation(90,0);
                armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
                armorStand.setCanPickupItems(false);
                armorStand.setCustomName("animation");

                EntityEquipment equipment1 = armorStand.getEquipment();
                assert equipment1 != null;
                equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        }));
        playerParama.addEntity("SWORD3",
            player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                armorStand.setInvisible(true);
                armorStand.setInvulnerable(true);
                armorStand.setGravity(false);
                armorStand.setRotation(180,0);
                armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
                armorStand.setCanPickupItems(false);
                armorStand.setCustomName("animation");

                EntityEquipment equipment1 = armorStand.getEquipment();
                assert equipment1 != null;
                equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        }));
        playerParama.addEntity("SWORD4",
                player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
                armorStand.setInvisible(true);
                armorStand.setInvulnerable(true);
                armorStand.setGravity(false);
                armorStand.setRotation(0,0);
                armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));
                armorStand.setCanPickupItems(false);
                armorStand.setCustomName("animation");

                EntityEquipment equipment1 = armorStand.getEquipment();
                    assert equipment1 != null;
                    equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        }));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            playerParama.getEntity("SWORD1").teleport(player.getLocation().add(0,-0.2,-0.8).setDirection(new Vector(1, 0, 0)));
            playerParama.getEntity("SWORD2").teleport(player.getLocation().add(0,-0.2,0.8).setDirection(new Vector(-1, 0, 0)));
            playerParama.getEntity("SWORD3").teleport(player.getLocation().add(-0.8,-0.2,0).setDirection(new Vector(0, 0, -1)));
            playerParama.getEntity("SWORD4").teleport(player.getLocation().add(0.8,-0.2,0).setDirection(new Vector(0, 0, 1)));
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            ArmorStand sword1 = (ArmorStand) playerParama.getEntity("SWORD1");
            ArmorStand sword2 = (ArmorStand) playerParama.getEntity("SWORD2");
            ArmorStand sword3 = (ArmorStand) playerParama.getEntity("SWORD3");
            ArmorStand sword4 = (ArmorStand) playerParama.getEntity("SWORD4");
            sword1.teleport(sword1.getLocation().add(0.5, 0, 0));
            sword2.teleport(sword2.getLocation().add(-0.5,0,0));
            sword3.teleport(sword3.getLocation().add(0,0,-0.5));
            sword4.teleport(sword4.getLocation().add(0,0,0.5));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            swordAnimation.cancel();
            playerParama.removeEntity("SWORD1");
            playerParama.removeEntity("SWORD2");
            playerParama.removeEntity("SWORD3");
            playerParama.removeEntity("SWORD4");
        }, 10);

    }
    public int getManaCost(){
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
