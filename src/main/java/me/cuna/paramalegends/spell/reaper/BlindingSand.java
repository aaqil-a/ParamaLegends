package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Locale;

public class BlindingSand implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 30;
    private final int duration = 100;
    private final int cooldown = 205;
    public BlindingSand(ParamaLegends plugin){
        this.plugin = plugin;

    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Blinding Sand");
        } else if(playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            Vector direction = player.getLocation().getDirection();

            ArrayList<Vector> directions = new ArrayList<>();
            directions.add(new Vector(direction.getX(), direction.getY(), direction.getZ()));

            directions.add(new Vector(direction.getX()*0.96-direction.getZ()*0.26, direction.getY(), direction.getX()*0.26+direction.getZ()*0.96));
            directions.add(new Vector(direction.getX()*0.86-direction.getZ()*0.5, direction.getY(), direction.getX()*0.5+direction.getZ()*0.86));

            directions.add(new Vector(direction.getX()*0.96+direction.getZ()*0.26, direction.getY(), direction.getX()*-0.26+direction.getZ()*0.96));
            directions.add(new Vector(direction.getX()*0.86+direction.getZ()*0.5, direction.getY(), direction.getX()*-0.5+direction.getZ()*0.86));

            directions.forEach(velocity -> {
                Snowball ball = player.launchProjectile(Snowball.class);
                ball.setCustomName("blindsand");
                ball.setItem(new ItemStack(Material.SAND));
                ball.setGravity(true);
                ball.setVelocity(velocity.normalize().multiply(1.2));
            });

            playerParama.addToCooldown(this);
            playerParama.addToReaperRefreshCooldown("Blinding Sand", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Blinding Sand");
                    playerParama.removeFromCooldown(this);
                    playerParama.removeFromReaperRefreshCooldown("Blinding Sand");
                }
            }, cooldown));
        }
    }
    //Deal damage when custom projectile hits entity
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof Snowball && projectile.getCustomName() != null){ // Check if ice ball hits
            if(projectile.getCustomName().equals("blindsand") && projectile.getShooter() instanceof Player){
                event.setCancelled(true);
                Player player = (Player) projectile.getShooter();
                PlayerParama playerParama = plugin.getPlayerParama(player);
                if(event.getHitEntity() != null){
                    if(event.getHitEntity() instanceof Damageable){
                        for(Entity blind : player.getWorld().getNearbyEntities(event.getHitEntity().getLocation(), 1.5,1.5,1.5)) {
                            if (blind.equals(player)|| blind instanceof ArmorStand) {
                                continue;
                            }
                            if (blind instanceof LivingEntity) {
                                blind.setMetadata("BLINDED",new FixedMetadataValue(plugin, "BLINDED"));
                                plugin.gameManager.experience.addExp(player, ClassGameType.REAPER, 1);
                                ((Damageable) blind).damage(1.034, player);

                                //blinded particle effect
                                playerParama.addTask("BLINDED"+blind.getUniqueId(),
                                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, ((LivingEntity) blind).getEyeLocation(), 8, 0.3, 0.25, 0.3, 0, Material.SAND.createBlockData());
                                        }, 0, 10));

                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    playerParama.cancelTask("BLINDED"+blind.getUniqueId());
                                    blind.removeMetadata("BLINDED", plugin);
                                }, duration);
                            }
                        }
                    }
                }
            }
        }
    }

    //cancel attacks from blinded enemies
    @EventHandler
    public void onEntityDamageByBlindedEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Wither || event.getDamager() instanceof EnderDragon
            || event.getDamager().hasMetadata("BOSS")){
            return;
        }
        if(event.getDamager().hasMetadata("BLINDED")){
            event.setCancelled(true);
        }
        if(event.getDamager() instanceof AbstractArrow){
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

    public int getCooldown() {
        return cooldown;
    }
}
