package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Soulstring {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<ArmorStand> soulstringAiming = new ArrayList<>();

    public Soulstring(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castSoulstring(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            archeryListener.sendCooldownMessage(player, "Soulstring");
        } else if (archeryListener.subtractMana(player, 150)) {
            summonSoulstring(player);
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    archeryListener.sendNoLongerCooldownMessage(player, "Soulstring");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 1200);
        }
    }

    public void summonSoulstring(Player player){
        Location location = player.getEyeLocation();
        Vector offset = player.getEyeLocation().getDirection().multiply(2.5);
        location.add(offset);
        if(!(location.getBlock().isEmpty() || location.getBlock().isLiquid())){
            archeryListener.findAir(location);
        }

        ItemStack shirt = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta shirtMeta = (LeatherArmorMeta) shirt.getItemMeta();
        shirtMeta.setColor(Color.WHITE);
        shirt.setItemMeta(shirtMeta);
        ArmorStand dummy = player.getWorld().spawn(new Location(player.getWorld(), 0,256,0), ArmorStand.class, armorStand -> {
            armorStand.setCustomName(player.getName()+"'s Soulstring");
            armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
            armorStand.getEquipment().setChestplate(shirt);
            armorStand.setArms(true);
            armorStand.setRightArmPose(new EulerAngle(-1.6, 0, -0.2));
            armorStand.setSilent(true);
            armorStand.setCanPickupItems(false);
            armorStand.setCollidable(false);
            armorStand.setGravity(false);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setCustomName("soulstring shooter");

        });
        ArmorStand dummyText = player.getWorld().spawn(new Location(player.getWorld(), 0,256,0), ArmorStand.class, armorStand-> {
            armorStand.setCustomName(ChatColor.GREEN + player.getName()+"'s Soulstring");
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setCollidable(false);
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setInvulnerable(true);
        });

        BukkitTask soulStringEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, location.clone().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0);
        }, 0, 100);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            dummy.teleport(location.clone().add(0, -0.1, 0));
            dummyText.teleport(location);
        }, 2);
        BukkitTask shoot = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(!soulstringAiming.contains(dummy)){
                shootSoulstring(player, dummy);
            }
        }, 40, 20);
        Bukkit.getScheduler().runTaskLater(plugin, shoot::cancel, 385);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            dummy.remove();
            dummyText.remove();
            soulStringEffect.cancel();
            soulstringAiming.remove(dummy);
        }, 405);
    }

    public void shootSoulstring(Player player, ArmorStand dummy){
        List<Entity> entities = player.getNearbyEntities(10,10,10).stream().toList();
        Random rand = new Random();
        List<Entity> newEntities = new ArrayList<>();
        for(Entity hit: entities){
            if(!(hit instanceof LivingEntity) || hit instanceof Villager || hit instanceof Player || hit instanceof Silverfish
                    || hit instanceof ArmorStand || hit instanceof Phantom)
                continue;
            newEntities.add(hit);
        }
        if(newEntities.size() > 0){
            int toHit = rand.nextInt(newEntities.size());
            Entity entityToHit = newEntities.get(toHit);
            if(entityToHit != null){
                aimSoulstring(player, dummy, entityToHit);
            }
        }
    }

    public void aimSoulstring(Player player, ArmorStand dummy, Entity entity){
        double dummyX = dummy.getLocation().getX();
        double dummyY = dummy.getLocation().getY();
        double dummyZ = dummy.getLocation().getZ();
        soulstringAiming.add(dummy);
        BukkitTask followEntity = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            double entityX = entity.getLocation().getX();
            double entityY = entity.getLocation().getY();
            double entityZ = entity.getLocation().getZ();
            Vector direction = new Vector(entityX-dummyX, entityY-dummyY-0.5, entityZ-dummyZ);
            dummy.teleport(dummy.getLocation().setDirection(direction.normalize()));
        }, 0, 2);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            followEntity.cancel();
            Arrow arrow = dummy.launchProjectile(Arrow.class, dummy.getLocation().getDirection());
            arrow.setShooter(player);
            arrow.setGravity(false);
            arrow.setVelocity(arrow.getVelocity().multiply(1.5));
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                arrow.remove();
            }, 20);
            soulstringAiming.remove(dummy);
        }, 30);

    }

}
