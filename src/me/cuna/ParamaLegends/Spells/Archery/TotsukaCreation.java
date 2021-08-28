package me.cuna.ParamaLegends.Spells.Archery;

import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

public class TotsukaCreation implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 40;

    public TotsukaCreation(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Totsuka's Creation");
        } else if (playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            Snowball ball = player.launchProjectile(Snowball.class);
            ball.setCustomName("totsuka");
            Vector velocity = ball.getVelocity();
            velocity.multiply(0.5);
            ball.setItem(new ItemStack(Material.COBWEB));
            ball.setGravity(true);
            ball.setVelocity(velocity);

            //add spell to cooldown
            playerParama.addToCooldown(this);

            //remove from cooldown
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Totsuka's Creation");
                    playerParama.removeFromCooldown(this);
                }
            }, 300);
        }
    }

    public void spawnWebs(Location location, Player player){
        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.addTask("TOTSUKA",
                Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
                    List<Entity> entities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 2, 2, 2).stream().toList();
                    for(Entity rooted : entities){
                        if(rooted instanceof LivingEntity && !(rooted instanceof Player)){
                            ((LivingEntity) rooted).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 4, false, false ,false));
                        }
                    }
                }, 0, 5));
        location.add(0,0.5,0);
        FallingBlock web1 = Objects.requireNonNull(location.getWorld()).spawnFallingBlock(location, Material.COBWEB.createBlockData());
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
            playerParama.cancelTask("TOTSUKA");
            web1.remove();
            web2.remove();
            web3.remove();
            web4.remove();
            web5.remove();
            web6.remove();
            web7.remove();
            web8.remove();
            web9.remove();
            player.removeMetadata("TOTSUKA", plugin);
        }, 140);
    }

    @EventHandler
    public void effectSpell(ProjectileHitEvent event){

        //Deal when totsuka projectile hits block or entity
        Projectile projectile = event.getEntity();
        if(projectile instanceof Snowball && projectile.getCustomName() != null){ // Check if ice ball hits
            if(projectile.getCustomName().equals("totsuka")){
                event.setCancelled(true);
                if(projectile.getShooter() != null && projectile.getShooter() instanceof Player){
                    Player player = (Player) projectile.getShooter();
                    if(!player.hasMetadata("TOTSUKA")){
                        player.setMetadata("TOTSUKA", new FixedMetadataValue(plugin, "TOTSUKA"));
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

        //Cancel event when other projectile hits web entities
        Entity entity = event.getHitEntity();
        if(entity instanceof FallingBlock){
            FallingBlock block = (FallingBlock) entity;
            if(block.getBlockData().getMaterial().equals(Material.COBWEB)){
                event.setCancelled(true);
            }
        }
    }


    public int getManaCost(){
        return manaCost;
    }

}
