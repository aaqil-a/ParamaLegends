package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class TotsukaCreation implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 30;
    private final int cooldown = 200;
    private final int duration = 140;

    public TotsukaCreation(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Totsuka's Creation");
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
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Totsuka's Creation");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown);
        }
    }

    public void spawnWebs(Location location, Player player){
        PlayerParama playerParama = plugin.getPlayerParama(player);
        location.add(0,0.5,0);

        //add webs
        for(int x = -2; x <= 2; x++){
            for(int z = -2; z <= 2; z++){
                FallingBlock web = player.getWorld().spawnFallingBlock(location.clone().add(x, 0, z), Material.COBWEB.createBlockData());
                web.setCustomName("totsukaweb");
                web.setDropItem(false);
                playerParama.addEntity("web"+x+""+z, web);
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //remove webs
            for(int x = -2; x <= 2; x++){
                for(int z = -2; z <= 2; z++){
                    playerParama.removeEntity("web"+x+""+z);
                }
            }
            player.removeMetadata("TOTSUKA", plugin);
        }, duration);
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
    }

    @EventHandler
    public void onChangeBlock(EntityChangeBlockEvent event){
        if(event.getEntity().getCustomName() != null
            && event.getEntity().getCustomName().equals("totsukaweb")){
            event.getBlock().setMetadata("TOTSUKAWEB", new FixedMetadataValue(plugin, true));
            //remove block after
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                event.getBlock().breakNaturally(new ItemStack(Material.IRON_PICKAXE));
            }, 140);
        }
    }

    @EventHandler
    public void onDropWeb(BlockDropItemEvent event){
        if(event.getBlock().hasMetadata("TOTSUKAWEB")){
            event.setCancelled(true);
        }
    }


    public int getManaCost(){
        return manaCost;
    }
    public int getDuration() {return duration;}
    public int getCooldown() {return cooldown;}
}
