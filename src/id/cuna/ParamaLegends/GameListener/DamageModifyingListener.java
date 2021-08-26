package id.cuna.ParamaLegends.GameListener;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Particle;
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
                if(plugin.archeryListener.getPlayersWindBoosted().contains(player.getUniqueId().toString())){
                    projectile.getWorld().spawnParticle(Particle.SWEEP_ATTACK, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0);
                }
                if(playerParama.getLevelFromClassType(ClassType.ARCHERY) >= 7){
                    Random rand = new Random();
                    if(rand.nextInt(99) < 10){
                        damage*=1.2;
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
                    }
                }
            }
        }
        if(plugin.archeryListener.getEntitiesHunterEye().contains(event.getEntity())){
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
                            plugin.checkCustomDamageSource(damage).equals(ClassType.SWORDSMAN)) {
                        //Deal crit damage according to player level
                        int playerLevel = playerParama.getLevelFromClassType(ClassType.SWORDSMAN);
                        int critRoll = rand.nextInt(100);
                        if(plugin.swordsmanListener.getPlayersEnraging().contains(attacker)){
                            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                                damage *= 1.4;
                            }
                            if(critRoll < 90){
                                damage *= 1.8;
                                spawnCritParticles(event.getEntity().getLocation());
                                playCritSound(attacker);
                            }
                        } else if(plugin.swordsmanListener.getPlayersCalamity().contains(attacker)){
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
                            || plugin.checkCustomDamageSource(damage).equals(ClassType.REAPER)) {
                            if(plugin.reaperListener.hiddenStrike.getPlayersHiddenStrike().contains(attacker)){
                                damage *= 1.5;
                                attacker.getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0.2);
                                plugin.reaperListener.coatedBlade.attackEntity(playerParama, event.getEntity(), event.getDamage());
                                plugin.reaperListener.hiddenStrike.getPlayersHiddenStrike().remove(attacker);
                            }
                            if(plugin.reaperListener.forbiddenSlash.getPlayersForbiddenSlash().contains(attacker)){
                                damage *= 2;
                                attacker.getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0,1,0),8, 0.5, 0.5, 0.5, 0.2);
                                attacker.getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1.5f);
                                plugin.reaperListener.forbiddenSlash.getPlayersForbiddenSlash().remove(attacker);
                            }
                        }
                    }
                }
            }
            if(plugin.swordsmanListener.getEntitiesTerrified().contains(event.getEntity())){
                damage = plugin.increasedIncomingDamage(damage, 1.5);
            }
        }
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            //Reduce damage if shields up
            if(plugin.swordsmanListener.getPlayersShielded().contains(player)){
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
