package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlingEarth implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 3;

    private final HashMap<Player, Vector> ballOffsetVectors = new HashMap<>();
    private final HashMap<Player, Snowball> ballsThrown = new HashMap<>();
    private final HashMap<Player, BukkitTask> ballsThrownTasks = new HashMap<>();
    private final HashMap<Player, BukkitTask> ballEffectTasks = new HashMap<>();
    private final HashMap<Player, FallingBlock> ballsDirt = new HashMap<>();

    public FlingEarth(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Fling Earth");
        } else if (playerParama.subtractMana(3)) {
            Player player = playerParama.getPlayer();
            Location location = player.getEyeLocation();
            Vector offset = player.getEyeLocation().getDirection().multiply(2.5);
            location.add(offset);
            ArmorStand dummy = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND);
            dummy.setCustomName(player.getName());
            dummy.setVisible(false);
            dummy.setGravity(false);
            dummy.setInvulnerable(true);
            dummy.setCanPickupItems(false);
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(location);
                dummy.getLocation().add(new Vector(0,1,0));
            }, 1);
            ballEffectTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Location newLocation = player.getEyeLocation();
                Vector newOffset = player.getEyeLocation().getDirection().multiply(2.5);
                newLocation.add(newOffset);
                ballOffsetVectors.put(player, newOffset);

                dummy.teleport(newLocation);

                dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.STONE.createBlockData());
                dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.DIRT.createBlockData());
            },2, 1));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(dummy.getLocation().add(new Vector(0,-2,0)));
                Snowball ball = dummy.launchProjectile(Snowball.class, ballOffsetVectors.get(player));
                dummy.remove();
                ball.setCustomName("iceball");
                Vector velocity = ball.getVelocity();
                velocity.multiply(1);
                ball.setItem(new ItemStack(Material.DIRT));
                if(ballEffectTasks.containsKey(player)){
                    ballEffectTasks.get(player).cancel();
                    ballEffectTasks.remove(player);
                }
                ball.setGravity(true);
                ball.setVelocity(velocity);
                ballsThrown.put(player, ball);
            }, 5);
            ballsThrownTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if(ballsDirt.containsKey(player)){
                    ballsDirt.get(player).remove();
                    ballsDirt.remove(player);
                }
                Snowball ball = ballsThrown.get(player);
                if(ball != null) {
                    ballsDirt.put(player, ball.getWorld().spawnFallingBlock(ball.getLocation(), Material.DIRT.createBlockData()));
                    ballsDirt.get(player).setGravity(false);
                } else {
                    cancelFlingEarthTasks(player);
                }
            }, 6, 1));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerParama.removeFromCooldown(this);
            }, 16);
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
            if(projectile.getCustomName().equals("iceball")){
                event.setCancelled(true);
                ArmorStand source = (ArmorStand) projectile.getShooter();
                assert source != null;
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
                        plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                        Damageable hit = (Damageable) event.getHitEntity();
                        hit.damage(5.069, player);
                    }
                }
                projectile.remove();
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
