package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ArcheryListener;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;


public class Soulstring implements SpellParama {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;
    private final int manaCost = 100;
    private final int cooldown = 600;

    public Soulstring(ParamaLegends plugin){
        this.plugin = plugin;
        this.archeryListener = plugin.archeryListener;
    }

    public void castSpell(PlayerParama player){
        if(player.checkCooldown(this)){
            plugin.sendCooldownMessage(player, "Soulstring");
        } else if (player.subtractMana(manaCost)) {
            summonSoulstring(player.getPlayer());
            player.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(player.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(player, "Soulstring");
                    player.removeFromCooldown(this);
                }
            }, cooldown);
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
        assert shirtMeta != null;
        shirtMeta.setColor(Color.WHITE);
        shirt.setItemMeta(shirtMeta);

        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.addEntity("TURRET",
                player.getWorld().spawn(new Location(player.getWorld(), 0,256,0), ArmorStand.class, armorStand -> {
                    armorStand.setCustomName(player.getName()+"'s Soulstring");
                    Objects.requireNonNull(armorStand.getEquipment()).setItemInMainHand(new ItemStack(Material.BOW));
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
                }));
       playerParama.addEntity("TURRETTEXT",
               player.getWorld().spawn(new Location(player.getWorld(), 0,256,0), ArmorStand.class, armorStand-> {
                   armorStand.setCustomName(ChatColor.GREEN + player.getName()+"'s Soulstring");
                   armorStand.setCustomNameVisible(true);
                   armorStand.setVisible(false);
                   armorStand.setCollidable(false);
                   armorStand.setGravity(false);
                   armorStand.setCanPickupItems(false);
                   armorStand.setInvulnerable(true);
               }));
       playerParama.addTask("TURRETEFFECT",
               Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                   player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, location.clone().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0);
               }, 0, 100));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playerParama.getEntity("TURRET").teleport(location.clone().add(0, -0.1, 0));
            playerParama.getEntity("TURRETTEXT").teleport(location);
        }, 2);
        playerParama.addTask("TURRETSHOOT",
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if(!playerParama.getEntity("TURRET").hasMetadata("AIMING")){
                        shootSoulstring(player, (ArmorStand) playerParama.getEntity("TURRET"));
                    }
                }, 40, 20));
        Bukkit.getScheduler().runTaskLater(plugin, ()->playerParama.cancelTask("TURRETSHOOT"), 385);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playerParama.removeEntity("TURRET");
            playerParama.removeEntity("TURRETTEXT");
            playerParama.cancelTask("TURRETEFFECT");
        }, 405);
    }

    public void shootSoulstring(Player player, ArmorStand dummy){
        for(Entity hit: player.getNearbyEntities(10,10,10)){
            if(hit instanceof Silverfish || hit instanceof Phantom)
                continue;
            if(hit instanceof Monster && dummy.hasLineOfSight(hit)){
                aimSoulstring(player, dummy, hit);
                break;
            }
        }
    }

    public void aimSoulstring(Player player, ArmorStand dummy, Entity entity){
        double dummyX = dummy.getLocation().getX();
        double dummyY = dummy.getLocation().getY();
        double dummyZ = dummy.getLocation().getZ();
        dummy.setMetadata("AIMING", new FixedMetadataValue(plugin, "AIMING"));
        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.addTask("TURRETFOLLOW",
                Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    double entityX = entity.getLocation().getX();
                    double entityY = entity.getLocation().getY();
                    double entityZ = entity.getLocation().getZ();
                    Vector direction = new Vector(entityX-dummyX, entityY-dummyY-0.5, entityZ-dummyZ);
                    dummy.teleport(dummy.getLocation().setDirection(direction.normalize()));
                }, 0, 2));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            playerParama.cancelTask("TURRETFOLLOW");
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                Arrow arrow = dummy.launchProjectile(Arrow.class, dummy.getLocation().getDirection());
                arrow.setShooter(player);
                arrow.setGravity(false);
                arrow.setVelocity(arrow.getVelocity().multiply(1.5));
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                Bukkit.getScheduler().runTaskLater(plugin, arrow::remove, 20);
            }, 3);
            dummy.removeMetadata("AIMING", plugin);
        }, 30);
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown(){return cooldown;}

}
