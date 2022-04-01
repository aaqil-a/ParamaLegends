package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ArcheryListener;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;


public class Soulstring implements SpellParama, Listener {

    private final ParamaLegends plugin;
    private final int manaCost = 100;
    private final int cooldown = 900;

    public Soulstring(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama player){
        if(player.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(player, "Soulstring");
        } else if (player.subtractMana(manaCost)) {
            summonSoulstring(player.getPlayer());
            player.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(player.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(player, "Soulstring");
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
            plugin.gameClassManager.archery.findAir(location);
        }

        ItemStack shirt = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta shirtMeta = (LeatherArmorMeta) shirt.getItemMeta();
        assert shirtMeta != null;
        shirtMeta.setColor(Color.WHITE);
        shirt.setItemMeta(shirtMeta);

        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.addEntity("TURRET",
                player.getWorld().spawn(location.clone().add(0, -0.1, 0), ArmorStand.class, armorStand -> {
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
               player.getWorld().spawn(location, ArmorStand.class, armorStand-> {
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
                   player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, location.clone().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0);
               }, 0, 100));
        playerParama.addTask("TURRETSHOOT",
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if(!playerParama.getEntity("TURRET").hasMetadata("AIMING")){
                        shootSoulstring(player, (ArmorStand) playerParama.getEntity("TURRET"));
                    }
                }, 20, 11));
        Bukkit.getScheduler().runTaskLater(plugin, ()->playerParama.cancelTask("TURRETSHOOT"), 385);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playerParama.removeEntity("TURRET");
            playerParama.removeEntity("TURRETTEXT");
            playerParama.cancelTask("TURRETEFFECT");
        }, 405);
    }

    public void shootSoulstring(Player player, ArmorStand dummy){
        player.getNearbyEntities(10, 10, 10).stream()
                .filter(e -> (e instanceof Monster || e instanceof Slime) && !e.isDead())
                .findAny()
                .ifPresent(e ->
                    aimSoulstring(player, dummy, (LivingEntity) e));
    }

    public void aimSoulstring(Player player, ArmorStand dummy, LivingEntity entity){
        double dummyX = dummy.getEyeLocation().getX();
        double dummyY = dummy.getEyeLocation().getY();
        double dummyZ = dummy.getEyeLocation().getZ();
        dummy.setMetadata("AIMING", new FixedMetadataValue(plugin, "AIMING"));
        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.addTask("TURRETFOLLOW",
                Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    double entityX = entity.getEyeLocation().getX();
                    double entityY = entity.getEyeLocation().getY();
                    double entityZ = entity.getEyeLocation().getZ();
                    Vector direction = new Vector(entityX-dummyX, entityY-dummyY-0.5, entityZ-dummyZ);
                    dummy.teleport(dummy.getLocation().setDirection(direction.normalize()));
                }, 0, 1));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            Arrow arrow = dummy.launchProjectile(Arrow.class, dummy.getLocation().getDirection());
            arrow.setShooter(player);
            arrow.setGravity(false);
            arrow.setDamage(6.016);
            arrow.setMetadata("SOULSTRINGARROW", new FixedMetadataValue(plugin, true));
            arrow.setVelocity(arrow.getVelocity().multiply(2));
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            Bukkit.getScheduler().runTaskLater(plugin, arrow::remove, 20);
            dummy.removeMetadata("AIMING", plugin);
        }, 4);
    }

    @EventHandler
    public void onPlayerHitBySoulstring(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager().hasMetadata("SOULSTRINGARROW")){
            event.setCancelled(true);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown(){return cooldown;}

}
