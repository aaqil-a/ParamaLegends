package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
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

public class BlindingSand implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 30;
    private final HashMap<Player, Snowball> ballsThrown = new HashMap<>();
    private final HashMap<Player, BukkitTask> ballsThrownTasks = new HashMap<>();
    private final HashMap<Player, FallingBlock> ballsDirt = new HashMap<>();
    private final List<Entity> entitiesBlinded = new ArrayList<>();

    public BlindingSand(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Blinding Sand");
        } else if(playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Snowball ball = player.launchProjectile(Snowball.class);
                ball.setCustomName("blindsand");
                Vector velocity = ball.getVelocity();
                velocity.multiply(0.7);
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
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Blinding Sand");
                    playerParama.removeFromCooldown(this);
                }
            }, 205);
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
                Player player = (Player) projectile.getShooter();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(ballsDirt.get(player) != null)
                        ballsDirt.get(player).remove();
                    ballsDirt.remove(player);
                }, 2);
                ballsThrown.remove(player);
                cancelFlingEarthTasks(player);
                if(event.getHitEntity() != null){
                    if(event.getHitEntity() instanceof Damageable){
                        List<Entity> blinded = player.getWorld().getNearbyEntities(event.getHitEntity().getLocation(), 1.5,1.5,1.5).stream().toList();
                        for(Entity blind : blinded) {
                            if (blind.equals(player)|| blind instanceof ArmorStand) {
                                continue;
                            }
                            if (blind instanceof Damageable) {
                                entitiesBlinded.add(blind);
                                ((Damageable) blind).damage(1.034, player);
                                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, blind.getLocation(), 3, 0.25, 0.25, 0.25, 0, Material.SAND.createBlockData());
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    entitiesBlinded.remove(blind);
                                }, 100);
                            }
                        }
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
        if(event.getDamager() instanceof Arrow){
            Projectile arrow = (Projectile) event.getDamager();
            if(arrow.getShooter() instanceof Entity){
                Entity shooter = (Entity) arrow.getShooter();
                if(entitiesBlinded.contains(shooter)){
                    event.setCancelled(true);
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
