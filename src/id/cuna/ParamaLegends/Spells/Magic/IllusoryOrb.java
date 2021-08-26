package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IllusoryOrb implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 100;
    private final List<EnderPearl> castedOrb = new ArrayList<EnderPearl>();
    private final HashMap<EnderPearl, BukkitTask> orbFlashTasks = new HashMap<>();

    public IllusoryOrb(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        Player player = playerParama.getPlayer();
        EnderPearl orb = null;
        boolean castNewOrb = true;
        for(EnderPearl orbCheck : castedOrb){
            Player shooter = (Player) orbCheck.getShooter();
            if(shooter != null){
                if (shooter.equals(player)){
                    orb = orbCheck;
                    orb.getWorld().spawnParticle(Particle.FLASH, orb.getLocation(), 1);
                    plugin.magicListener.teleportToAir(player, orb.getLocation(), player.getLocation().getDirection());
                    castNewOrb = false;
                }
            }
        }
        if(!castNewOrb) {
            castedOrb.remove(orb);
            orb.remove();
            if(orbFlashTasks.containsKey(orb)){
                orbFlashTasks.get(orb).cancel();
            }
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
                castedOrb.add(newOrb);
                orbFlashTasks.put(newOrb, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    newOrb.getWorld().spawnParticle(Particle.FLASH, newOrb.getLocation(), 1);
                }, 0, 5));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(castedOrb.contains(newOrb)){
                        castedOrb.remove(newOrb);
                        newOrb.remove();
                    }
                    if(orbFlashTasks.containsKey(newOrb)){
                        orbFlashTasks.get(newOrb).cancel();
                    }
                }, 40);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Illusory Orb");
                        playerParama.removeFromCooldown(this);
                    }
                }, 200);
            }
        }
    }

    // Cancel teleports with ender pearl
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)){
            for(EnderPearl pearl: castedOrb){
                if(pearl.getShooter() != null && pearl.getShooter().equals(event.getPlayer())) event.setCancelled(true);
            }
        }
    }

    //Deal damage when custom projectile hits entity
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        //Check if illusory orb hits
        if (projectile instanceof EnderPearl && (projectile.getCustomName() != null)){
            event.setCancelled(true);
            if (event.getHitBlock() != null) {
                event.setCancelled(true);
            }
            if(event.getHitEntity() != null){
                if(event.getHitEntity() instanceof Damageable){
                    plugin.experienceListener.addExp((Player) projectile.getShooter(), ClassType.MAGIC, 1);
                    Damageable hit = (Damageable) event.getHitEntity();
                    hit.damage(20.069, (Player) projectile.getShooter());
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
