package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlindingSand implements Listener {

    private final ParamaLegends plugin;
    private final ReaperListener reaperListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final HashMap<Player, Vector> ballOffsetVectors = new HashMap<>();
    private final HashMap<Player, Snowball> ballsThrown = new HashMap<>();
    private final HashMap<Player, BukkitTask> ballsThrownTasks = new HashMap<>();
    private final HashMap<Player, FallingBlock> ballsDirt = new HashMap<>();
    private final List<Entity> entitiesBlinded = new ArrayList<>();

    public BlindingSand(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
        this.reaperListener = reaperListener;
    }

    public void castBlindingSand(Player player){
        if (playerCooldowns.contains(player.getUniqueId().toString())) {
            reaperListener.sendCooldownMessage(player, "Blinding Sand");
        } else {
            Location location = player.getEyeLocation();
            Vector offset = player.getEyeLocation().getDirection().multiply(2.5);
            location.add(offset);
            ArmorStand dummy = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND);
            dummy.setCustomName(player.getName());
            dummy.setVisible(false);
            dummy.setGravity(false);
            dummy.setInvulnerable(true);
            dummy.setCanPickupItems(false);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(location);
                dummy.getLocation().add(new Vector(0,1,0));
            }, 1);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(dummy.getLocation().add(new Vector(0,-2,0)));
                Snowball ball = dummy.launchProjectile(Snowball.class, ballOffsetVectors.get(player));
                dummy.remove();
                ball.setCustomName("blindsand");
                Vector velocity = ball.getVelocity();
                velocity.multiply(0.5);
                ball.setItem(new ItemStack(Material.SAND));
                ball.setGravity(true);
                ball.setVelocity(velocity);
                ballsThrown.put(player, ball);
            }, 2);
            ballsThrownTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if(ballsDirt.containsKey(player)){
                    ballsDirt.get(player).remove();
                    ballsDirt.remove(player);
                }
                Snowball ball = ballsThrown.get(player);
                if(ball != null) {
                    ballsDirt.put(player, ball.getWorld().spawnFallingBlock(ball.getLocation(), Material.SAND.createBlockData()));
                    ballsDirt.get(player).setGravity(false);
                } else {
                    cancelFlingEarthTasks(player);
                }
            }, 2, 1));
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    reaperListener.sendNoLongerCooldownMessage(player, "Blinding Sand");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 320);
        }
    }

    public void cancelFlingEarthTasks(Player player){
        if(ballsThrownTasks.get(player) != null)
            ballsThrownTasks.get(player).cancel();
        ballsThrownTasks.remove(player);
    }

    //Deal damage when custom projectile hits entity
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof Snowball && projectile.getCustomName() != null){ // Check if ice ball hits
            if(projectile.getCustomName().equals("blindsand")){
                event.setCancelled(true);
                ArmorStand source = (ArmorStand) projectile.getShooter();
                Player player = plugin.getServer().getPlayer(source.getName());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(ballsDirt.get(player) != null)
                        ballsDirt.get(player).remove();
                    ballsDirt.remove(player);
                }, 2);
                ballOffsetVectors.remove(player);
                ballsThrown.remove(player);
                cancelFlingEarthTasks(player);
                if(event.getHitEntity() != null){
                    if(event.getHitEntity() instanceof Damageable){
                        entitiesBlinded.add(event.getHitEntity());
                        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, event.getHitEntity().getLocation(), 3, 0.25, 0.25, 0.25, 0, Material.SAND.createBlockData());
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                            entitiesBlinded.remove(event.getHitEntity());
                        }, 100);
                    }
                }
            }
        }
    }

    //cancel attacks from blinded enemies
    @EventHandler
    public void onEntityDamageByEntitySand(EntityDamageByEntityEvent event){
        if(entitiesBlinded.contains(event.getDamager())){
            event.setCancelled(true);
        }
    }

}
