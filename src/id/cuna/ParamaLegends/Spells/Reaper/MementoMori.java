package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class MementoMori {

    private final ParamaLegends plugin;
    private final ReaperListener reaperListener;

    private final List<String> playerCooldowns = new ArrayList<>();

    public MementoMori(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
        this.reaperListener = reaperListener;
    }

    public void castMementoMori (Player player , Entity entity) {
        if (!playerCooldowns.contains(player.getUniqueId().toString()) && reaperListener.subtractMana(player, 300)) {
            if(entity instanceof LivingEntity){
                hoeAnimation(entity);
                playerCooldowns.add(player.getUniqueId().toString());

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ((LivingEntity) entity).damage(250.034, player);
                    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f, 1.3f);
                    entity.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, ((LivingEntity) entity).getEyeLocation(), 4, 0.5, 0.5, 0.5, 0);
                    entity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, ((LivingEntity) entity).getEyeLocation(), 1, 0, 0, 0, 0);
                    entity.getWorld().spawnParticle(Particle.FLASH, ((LivingEntity) entity).getEyeLocation(), 1, 0, 0, 0, 0);
                }, 15);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        playerCooldowns.remove(player.getUniqueId().toString());
                        reaperListener.sendNoLongerCooldownMessage(player, "Memento Mori");
                    }
                }, 1202);//1202
            }
        }
    }

    public void hoeAnimation(Entity entity){
        ArmorStand sword1 = entity.getWorld().spawn(new Location(entity.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(-90,0);
            armorStand.setArms(true);
            armorStand.setRightArmPose(new EulerAngle(-0.2,-0.2,3.14));
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("animation");

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.NETHERITE_HOE));
        });

        Location swordLocation1 = entity.getLocation().add(entity.getLocation().getDirection().setY(0).normalize().multiply(1));
        swordLocation1.setDirection(entity.getLocation().getDirection());
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            sword1.teleport(swordLocation1.add(0,1,0));
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            sword1.setRightArmPose(sword1.getRightArmPose().add(0.2, 0, 0));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, swordAnimation::cancel, 15);
        Bukkit.getScheduler().runTaskLater(plugin, sword1::remove, 40);
    }

    public List<String> getPlayerCooldowns() {
        return playerCooldowns;
    }
}
