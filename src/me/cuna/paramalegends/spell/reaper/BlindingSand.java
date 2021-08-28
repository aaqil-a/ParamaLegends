package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class BlindingSand implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 30;

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
                playerParama.addEntity("SANDBALL", ball);
            }, 2);
            playerParama.addTask("SANDTHROW",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        playerParama.removeEntity("SANDBLOCK");
                        Snowball ball = (Snowball) playerParama.getEntity("SANDBALL");
                        if(ball != null) {
                            FallingBlock sand = ball.getWorld().spawnFallingBlock(ball.getLocation(), Material.SAND.createBlockData());
                            playerParama.addEntity("SANDBLOCK", sand);
                        } else {
                            playerParama.cancelTask("SANDTHROW");
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
    //Deal damage when custom projectile hits entity
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof Snowball && projectile.getCustomName() != null){ // Check if ice ball hits
            if(projectile.getCustomName().equals("blindsand")){
                event.setCancelled(true);
                Player player = (Player) projectile.getShooter();
                PlayerParama playerParama = plugin.getPlayerParama(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.removeEntity("SANDBLOCK");
                }, 2);
                playerParama.removeEntity("SANDBALL");
                playerParama.cancelTask("SANDTHROW");
                if(event.getHitEntity() != null){
                    if(event.getHitEntity() instanceof Damageable){
                        List<Entity> blinded = player.getWorld().getNearbyEntities(event.getHitEntity().getLocation(), 1.5,1.5,1.5).stream().toList();
                        for(Entity blind : blinded) {
                            if (blind.equals(player)|| blind instanceof ArmorStand) {
                                continue;
                            }
                            if (blind instanceof Damageable) {
                                blind.setMetadata("BLINDED",new FixedMetadataValue(plugin, "BLINDED"));
                                ((Damageable) blind).damage(1.034, player);
                                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, blind.getLocation(), 3, 0.25, 0.25, 0.25, 0, Material.SAND.createBlockData());
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    blind.removeMetadata("BLINDED", plugin);
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
        if(event.getDamager().hasMetadata("BLINDED")){
            event.setCancelled(true);
        }
        if(event.getDamager() instanceof Arrow){
            Projectile arrow = (Projectile) event.getDamager();
            if(arrow.getShooter() instanceof Entity){
                Entity shooter = (Entity) arrow.getShooter();
                if(shooter.hasMetadata("BLINDED")){
                    event.setCancelled(true);
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
