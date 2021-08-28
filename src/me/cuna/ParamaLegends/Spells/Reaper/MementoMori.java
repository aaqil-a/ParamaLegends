package me.cuna.ParamaLegends.Spells.Reaper;

import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.AttackParama;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

public class MementoMori implements AttackParama {

    private final ParamaLegends plugin;
    private final int manaCost = 300;

    public MementoMori(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void attackEntity(PlayerParama playerParama , Entity entity, double damage) {
        if (!playerParama.checkCooldown(this) && playerParama.subtractMana(manaCost)) {
            if(entity instanceof LivingEntity){
                Player player = playerParama.getPlayer();
                hoeAnimation(entity);
                playerParama.addToCooldown(this);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ((LivingEntity) entity).damage(damage+250.034, player);
                    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f, 1.3f);
                    entity.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, ((LivingEntity) entity).getEyeLocation(), 4, 0.5, 0.5, 0.5, 0);
                    entity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, ((LivingEntity) entity).getEyeLocation(), 1, 0, 0, 0, 0);
                    entity.getWorld().spawnParticle(Particle.FLASH, ((LivingEntity) entity).getEyeLocation(), 1, 0, 0, 0, 0);
                }, 15);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        playerParama.removeFromCooldown(this);
                        plugin.sendNoLongerCooldownMessage(playerParama, "Memento Mori");
                    }
                }, 1202);//1202
            }
        }
    }

    public void hoeAnimation(Entity entity) {
        ArmorStand sword1 = entity.getWorld().spawn(new Location(entity.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(-90, 0);
            armorStand.setArms(true);
            armorStand.setRightArmPose(new EulerAngle(-0.2, -0.2, 3.14));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            assert equipment1 != null;
            equipment1.setItemInMainHand(new ItemStack(Material.NETHERITE_HOE));
        });

        Location swordLocation1 = entity.getLocation().add(entity.getLocation().getDirection().setY(0).normalize().multiply(1));
        swordLocation1.setDirection(entity.getLocation().getDirection());
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            sword1.teleport(swordLocation1.add(0, 1, 0));
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            sword1.setRightArmPose(sword1.getRightArmPose().add(0.2, 0, 0));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, swordAnimation::cancel, 15);
        Bukkit.getScheduler().runTaskLater(plugin, sword1::remove, 40);
    }
    public int getManaCost(){
        return manaCost;
    }
}
