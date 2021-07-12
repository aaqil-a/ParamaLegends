package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IllusoryOrb implements Listener {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<EnderPearl> castedOrb = new ArrayList<EnderPearl>();
    private final HashMap<EnderPearl, BukkitTask> orbFlashTasks = new HashMap<>();

    public IllusoryOrb(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castIllusoryOrb(Player player){
        EnderPearl orb = null;
        boolean castNewOrb = true;
        for(EnderPearl orbCheck : castedOrb){
            Player shooter = (Player) orbCheck.getShooter();
            if(shooter != null){
                if (shooter.equals(player)){
                    orb = orbCheck;
                    orb.getWorld().spawnParticle(Particle.FLASH, orb.getLocation(), 1);
                    magicListener.teleportToAir(player, orb.getLocation());
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
            if(playerCooldowns.contains(player.getUniqueId().toString())){
                magicListener.sendCooldownMessage(player, "Illusory Orb");
            } else if (magicListener.subtractMana(player, 100)) {
                EnderPearl newOrb = player.launchProjectile(EnderPearl.class);
                playerCooldowns.add(player.getUniqueId().toString());
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
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        magicListener.sendNoLongerCooldownMessage(player, "Illusory Orb");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 200);
            }
        }
    }

    // Cancel teleports with ender pearl
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event){
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)){
            event.setCancelled(true);
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
}
