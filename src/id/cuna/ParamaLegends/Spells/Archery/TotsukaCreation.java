package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class TotsukaCreation implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<String> playerTotsuka = new ArrayList<>();

    public TotsukaCreation(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castTotsuka(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            archeryListener.sendCooldownMessage(player, "Totsuka's Creation");
        } else if (archeryListener.subtractMana(player, 40)) {
            Snowball ball = player.launchProjectile(Snowball.class);
            ball.setCustomName("totsuka");
            Vector velocity = ball.getVelocity();
            velocity.multiply(0.5);
            ball.setItem(new ItemStack(Material.COBWEB));
            ball.setGravity(true);
            ball.setVelocity(velocity);
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    archeryListener.sendNoLongerCooldownMessage(player, "Totsuka's Creation");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 600);
        }
    }

    public void spawnWebs(Location location, Player player){
        BukkitTask slow = Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
            List<Entity> entities = location.getWorld().getNearbyEntities(location, 2, 2, 2).stream().toList();
            for(Entity rooted : entities){
                if(rooted instanceof LivingEntity && !(rooted instanceof Player)){
                    ((LivingEntity) rooted).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 4, false, false ,false));
                }
            }
        }, 0, 5);
        location.add(0,0.5,0);
        FallingBlock web1 = location.getWorld().spawnFallingBlock(location, Material.COBWEB.createBlockData());
        web1.setGravity(false);
        FallingBlock web2 = location.getWorld().spawnFallingBlock(location.clone().add(-1,0,-1), Material.COBWEB.createBlockData());
        web2.setGravity(false);
        FallingBlock web3 = location.getWorld().spawnFallingBlock(location.clone().add(1,0,-1), Material.COBWEB.createBlockData());
        web3.setGravity(false);
        FallingBlock web4 = location.getWorld().spawnFallingBlock(location.clone().add(-1,0,1), Material.COBWEB.createBlockData());
        web4.setGravity(false);
        FallingBlock web5 = location.getWorld().spawnFallingBlock(location.clone().add(1,0,1), Material.COBWEB.createBlockData());
        web5.setGravity(false);
        FallingBlock web6 = location.getWorld().spawnFallingBlock(location.clone().add(-1,0,0), Material.COBWEB.createBlockData());
        web6.setGravity(false);
        FallingBlock web7 = location.getWorld().spawnFallingBlock(location.clone().add(1,0,0), Material.COBWEB.createBlockData());
        web7.setGravity(false);
        FallingBlock web8 = location.getWorld().spawnFallingBlock(location.clone().add(0,0,-1), Material.COBWEB.createBlockData());
        web8.setGravity(false);
        FallingBlock web9 = location.getWorld().spawnFallingBlock(location.clone().add(0,0,1), Material.COBWEB.createBlockData());
        web9.setGravity(false);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            slow.cancel();
            web1.remove();
            web2.remove();
            web3.remove();
            web4.remove();
            web5.remove();
            web6.remove();
            web7.remove();
            web8.remove();
            web9.remove();
            playerTotsuka.remove(player.getUniqueId().toString());
        }, 60);
    }

    //Deal when projectile hits block
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof Snowball && projectile.getCustomName() != null){ // Check if ice ball hits
            if(projectile.getCustomName().equals("totsuka")){
                event.setCancelled(true);
                Player player = (Player) projectile.getShooter();
                if(!playerTotsuka.contains(player.getUniqueId().toString())){
                    playerTotsuka.add(player.getUniqueId().toString());
                    Location location = new Location(player.getWorld(), 0,256,0);
                    if(event.getHitBlock() != null) {
                        location = event.getHitBlock().getLocation().add(0,1,0);
                    } else if(event.getHitEntity() != null){
                        location = event.getHitEntity().getLocation();
                    }
                    spawnWebs(location, player);
                }
            }
        }
    }


}
