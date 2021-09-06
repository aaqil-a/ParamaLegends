package me.cuna.paramalegends.spell.magic;

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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FlingEarth implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 3;
    private final int cooldown = 16;
    private final int damage = 5;
    private final int damageBonus = 1;

    public FlingEarth(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Fling Earth");
        } else if (playerParama.subtractMana(manaCost)) {
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
            playerParama.addTask("EARTHEFFECT",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Location newLocation = player.getEyeLocation();
                        Vector newOffset = player.getEyeLocation().getDirection().multiply(2.5);
                        newLocation.add(newOffset);

                        dummy.teleport(newLocation);

                        dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.STONE.createBlockData());
                        dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.DIRT.createBlockData());
                    },2, 1));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(dummy.getLocation().add(new Vector(0,-2,0)));
                Snowball ball = dummy.launchProjectile(Snowball.class, player.getEyeLocation().getDirection().multiply(2.5));
                dummy.remove();
                ball.setCustomName("iceball");
                Vector velocity = ball.getVelocity();
                velocity.multiply(1);
                ball.setItem(new ItemStack(Material.DIRT));
                playerParama.cancelTask("EARTHEFFECT");
                ball.setGravity(true);
                ball.setVelocity(velocity);
                playerParama.addEntity("FLINGEARTHBALL", ball);
            }, 5);
            playerParama.addTask("EARTHTHROWN",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        playerParama.removeEntity("FLINGEARTHDIRT");
                        Snowball ball = (Snowball) playerParama.getEntity("FLINGEARTHBALL");
                        if(ball != null) {
                            FallingBlock dirt = ball.getWorld().spawnFallingBlock(ball.getLocation(), Material.DIRT.createBlockData());
                            playerParama.addEntity("FLINGEARTHDIRT", dirt);
                            dirt.setGravity(false);
                        } else {
                            playerParama.cancelTask("EARTHTHROWN");
                        }
                    }, 6, 1));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerParama.removeFromCooldown(this);
            }, cooldown);
        }
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
                PlayerParama playerParama = plugin.getPlayerParama(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.removeEntity("FLINGEARTHDIRT");
                }, 2);
                playerParama.removeEntity("FLINGEARTHBALL");
                playerParama.cancelTask("EARTHTHROWN");
                int masteryLevel = playerParama.getMasteryLevel("flingearth");
                if(event.getHitEntity() != null){
                    if(event.getHitEntity() instanceof Damageable){
                        plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                        Damageable hit = (Damageable) event.getHitEntity();
                        hit.damage(damage+damageBonus*masteryLevel+0.069, player);
                        plugin.magicListener.addMastery(playerParama, "flingearth", 1);
                    }
                }
                projectile.remove();
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown(){return cooldown;}
}
