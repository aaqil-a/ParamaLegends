package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class IllusoryOrb implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 100;
    private final int damage = 20;
    private final int duration = 40;
    private final int cooldown = 200;

    public IllusoryOrb(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        Player player = playerParama.getPlayer();
        if(playerParama.getEntity("ILLUSORYORB") != null) {
            EnderPearl orb = (EnderPearl) playerParama.getEntity("ILLUSORYORB");
            orb.remove();
            orb.getWorld().spawnParticle(Particle.FLASH, orb.getLocation(), 1);
            plugin.magicListener.teleportToAir(player, orb.getLocation(), player.getLocation().getDirection());
            playerParama.cancelTask("ORBFLASH");
            if(player.hasMetadata("ORBCASTED")) player.removeMetadata("ORBCASTED",plugin);
        } else {
            if(playerParama.checkCooldown(this)){
                plugin.sendCooldownMessage(playerParama, "Illusory Orb");
            } else if (playerParama.subtractMana(manaCost)) {
                EnderPearl newOrb = player.launchProjectile(EnderPearl.class);
                playerParama.addToCooldown(this);
                Vector velocity = newOrb.getVelocity();
                velocity.multiply(0.5);
                newOrb.setCustomName("illusoryorb");
                newOrb.setGravity(false);
                newOrb.setVelocity(velocity);
                playerParama.addEntity("ILLUSORYORB", newOrb);
                player.setMetadata("ORBCASTED", new FixedMetadataValue(plugin, "ORBCASTED"));
                newOrb.getWorld().playSound(newOrb.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 1f, 2f);
                playerParama.addTask("ORBFLASH",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            newOrb.getWorld().spawnParticle(Particle.FLASH, newOrb.getLocation(), 1);
                        }, 0, 5));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerParama.removeEntity("ILLUSORYORB");
                    playerParama.cancelTask("ORBFLASH");
                    if(player.hasMetadata("ORBCASTED")) player.removeMetadata("ORBCASTED",plugin);
                }, duration);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Illusory Orb");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
            }
        }
    }

    // Cancel teleports with ender pearl
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)){
            if(event.getPlayer().hasMetadata("ORBCASTED")) event.setCancelled(true);
        }
    }

    //Deal damage when custom projectile hits entity
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        //Check if illusory orb hits
        if (projectile instanceof EnderPearl && (projectile.getCustomName() != null)){
            event.setCancelled(true);
            if(event.getHitEntity() != null){
                if(event.getHitEntity() instanceof Damageable){
                    plugin.experienceListener.addExp((Player) projectile.getShooter(), ClassGameType.MAGIC, 1);
                    Damageable hit = (Damageable) event.getHitEntity();
                    hit.damage(damage+0.069, (Player) projectile.getShooter());
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown() { return cooldown;}
}
