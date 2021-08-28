package me.cuna.paramalegends.game;

import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class DamageModifyingListener implements Listener {

    private final ParamaLegends plugin;

    public DamageModifyingListener(final ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void spawnCritParticles(Location location){
        Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.EXPLOSION_LARGE, location.add(0,1,0), 1, 0.5, 0.5, 0.5, 0);
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
    }

    public void playCritSound(Player player){
        player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1f, 1f);
    }


    //Modifying entity to entity attacks
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        double damage = event.getDamage();
        //Check damage modifiers from archery
        if(event.getDamager() instanceof AbstractArrow) {
            Projectile projectile = (Projectile) event.getDamager();
            if(projectile.getShooter() instanceof Player){
                Player player = (Player) projectile.getShooter();
                PlayerParama playerParama = plugin.getPlayerParama(player);
                if(event.getEntity().getLocation().distance(player.getLocation()) > 10){
                    damage *= 1.2;
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1f);
                }
                if(player.hasMetadata("WINDBOOSTPARAMA")){
                    projectile.getWorld().spawnParticle(Particle.SWEEP_ATTACK, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0);
                }
                if(playerParama.getLevelFromClassType(ClassGameType.ARCHERY) >= 7){
                    Random rand = new Random();
                    if(rand.nextInt(99) < 10){
                        damage*=1.2;
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
                    }
                }
            }
        }
        if(event.getEntity().hasMetadata("HUNTEREYE")){
            damage = plugin.increasedIncomingDamage(damage, 1.3);
        }
        if(event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            PlayerParama playerParama = plugin.getPlayerParama(attacker);
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            Random rand = new Random();
            switch (item.getType()){
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    if(plugin.checkCustomDamageSource(damage) == null ||
                            plugin.checkCustomDamageSource(damage).equals(ClassGameType.SWORDSMAN)) {
                        //Deal crit damage according to player level
                        int playerLevel = playerParama.getLevelFromClassType(ClassGameType.SWORDSMAN);
                        int critRoll = rand.nextInt(100);
                        if(attacker.hasMetadata("ENRAGING")){
                            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                                damage *= 1.4;
                            }
                            if(critRoll < 90){
                                damage *= 1.8;
                                spawnCritParticles(event.getEntity().getLocation());
                                playCritSound(attacker);
                            }
                        } else if(attacker.hasMetadata("CALAMITY")){
                            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                                damage *= 1.4;
                            } else {
                                damage *= 1.7;
                                playCritSound(attacker);
                            }
                            spawnCritParticles(event.getEntity().getLocation());
                        } else if(playerLevel < 4){
                            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                                return;
                            }
                            if(critRoll < 20){
                                damage *= 1.4;
                                spawnCritParticles(event.getEntity().getLocation());
                                playCritSound(attacker);
                            }
                        } else {
                            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                                damage *= 1.4;
                            } else if(critRoll < 35){
                                damage *= 1.7;
                                spawnCritParticles(event.getEntity().getLocation());
                                playCritSound(attacker);
                            }
                        }
                    }
                }
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    if(item.getItemMeta() != null && item.getItemMeta().getDisplayName().contains("Scythe")){
                        if(plugin.checkCustomDamageSource(damage) == null
                            || plugin.checkCustomDamageSource(damage).equals(ClassGameType.REAPER)) {
                            if(attacker.hasMetadata("HIDDENSTRIKE")){
                                damage *= 1.5;
                                attacker.getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0.2);
                                plugin.reaperListener.coatedBlade.attackEntity(playerParama, event.getEntity(), event.getDamage());
                                attacker.removeMetadata("HIDDENSTRIKE", plugin);
                            }
                            if(attacker.hasMetadata("FORBIDDENSLASH")){
                                damage *= 2;
                                attacker.getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0,1,0),8, 0.5, 0.5, 0.5, 0.2);
                                attacker.getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1.5f);
                                attacker.removeMetadata("FORBIDDENSLASH", plugin);
                            }
                        }
                    }
                }
            }
            if(event.getEntity().hasMetadata("TERRIFIED")){
                damage = plugin.increasedIncomingDamage(damage, 1.5);
            }
        }
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            //Reduce damage if shields up
            if(player.hasMetadata("SHIELDSUP")){
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0,1,0), 8, 0.25, 0.25, 0.25, 0, Material.IRON_BLOCK.createBlockData());
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.4f, 0f);
                damage *= 0.3;
            }
        }
        event.setDamage(damage);
    }


    //Modifying entity damage by anything
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        double damage = event.getDamage();
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

        }
        event.setDamage(damage);
    }

}
