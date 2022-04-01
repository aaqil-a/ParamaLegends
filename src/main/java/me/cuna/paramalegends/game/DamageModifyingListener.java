package me.cuna.paramalegends.game;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

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
                    damage *= 1.1;
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1f);
                }
                if(player.hasMetadata("WINDBOOSTPARAMA")){
                    projectile.getWorld().spawnParticle(Particle.SWEEP_ATTACK, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0);
                }
                if(playerParama.getClassLevel(ClassGameType.ARCHERY) >= 7){
                    Random rand = new Random();
                    if(rand.nextInt(99) < 10){
                        damage*=1.2;
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
                    }
                }
            }
        }
        if(event.getEntity().hasMetadata("HUNTEREYE")){
            damage = increasedIncomingDamage(damage, 1.3);
        }
        if(event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            PlayerParama playerParama = plugin.getPlayerParama(attacker);
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            Random rand = new Random();
            switch (item.getType()){
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    //swordmanship critical hit check
                    if(checkCustomDamageSource(damage) == null ||
                            checkCustomDamageSource(damage).equals(ClassGameType.SWORDSMAN)) {
                        int playerLevel = playerParama.getClassLevel(ClassGameType.SWORDSMAN);
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
                //reaper passive check
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    if(item.getItemMeta() != null && item.getItemMeta().getDisplayName().contains("Scythe")){
                        if(checkCustomDamageSource(damage) == null
                            || checkCustomDamageSource(damage).equals(ClassGameType.REAPER)) {
                            if(attacker.hasMetadata("HIDDENSTRIKE")){
                                damage *= 1.5;
                                attacker.getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0.2);
                                plugin.gameClassManager.reaper.coatedBlade.attackEntity(playerParama, event.getEntity(), event.getDamage());
                                attacker.removeMetadata("HIDDENSTRIKE", plugin);
                            }
                            if(attacker.hasMetadata("FORBIDDENSLASH")){
                                damage *= 2;
                                attacker.getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0,1,0),8, 0.5, 0.5, 0.5, 0.2);
                                attacker.getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1.5f);
                                attacker.removeMetadata("FORBIDDENSLASH", plugin);
                            }
                            if(attacker.hasMetadata("PROWL")){
                                damage *= 1.5;
                                attacker.playSound(event.getEntity().getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1.5f);
                            }
                        }
                    }
                }
            }
            if(event.getEntity().hasMetadata("TERRIFIED")){
                damage = increasedIncomingDamage(damage, 1.5);
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
        //reduce damage if slime king
        if(event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"aKing Slime")){
            damage *= (1/((double) (plugin.bossManager.natureFight.getPlayerCount())));
        }
        //reduce damage if ender dragon
        if(event.getEntity().getType().equals(EntityType.ENDER_DRAGON)){
            damage *= (1/((double) 100));
        }
        //increase damage from elder guardian
        if(event.getDamager().getType().equals(EntityType.ELDER_GUARDIAN)){
            damage *= 2;
        }

        if(event.getDamager().getType().equals(EntityType.PLAYER)){
            spawnDamageIndicator(event.getEntity(), damage, event.getDamager().hasMetadata("PRACTICE"));
        }
        if(event.getDamager() instanceof Projectile){
            if(((Projectile) event.getDamager()).getShooter() instanceof Player){
                spawnDamageIndicator(event.getEntity(), damage, event.getDamager().hasMetadata("PRACTICE"));
            }
        }
        if(event.getDamager().hasMetadata("PRACTICE")){
            damage = 0;
        }
        event.setDamage(damage);
    }

    public void spawnDamageIndicator(Entity entity, double damage, boolean practice){
        ChatColor color = ChatColor.YELLOW;
        if(practice) {
            color = ChatColor.GREEN;
        } else if(damage > 100){
            color = ChatColor.GOLD;
        } else if(damage > 50){
            color = ChatColor.RED;
        }
        ChatColor finalColor = color;
        Location location = entity.getLocation();
        if(entity instanceof LivingEntity){
            location = ((LivingEntity) entity).getEyeLocation();
        }
        Location finalLocation = location;
        entity.getWorld().spawn(location, ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setCustomName(ChatColor.BOLD+""+finalColor+""+(int) Math.ceil(damage));
            armorStand.setCustomNameVisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setSmall(true);
            armorStand.setCollidable(false);
            armorStand.setMarker(true);
            armorStand.setAI(false);
            BukkitTask animation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                armorStand.teleport(finalLocation.add(0,0.05,0));
            }, 0, 1);
            Bukkit.getScheduler().runTaskLater(plugin, animation::cancel, 20);
            Bukkit.getScheduler().runTaskLater(plugin, armorStand::remove, 30);
        });
    }


    public double increasedIncomingDamage(double damage, double multiplier){
        String damageString = String.valueOf(damage);
        double toAdd = 0;
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            switch (key) {
                case ".069", ".068" -> toAdd = 0.069;
                case ".034", ".033", ".035" -> toAdd = 0.034;
                case ".072", ".073", ".071" -> toAdd = 0.072;
                case ".016", ".015", ".017" -> toAdd = 0.016;
            }
        }
        return damage*multiplier+toAdd;
    }

    public ClassGameType checkCustomDamageSource(double damage){
        String damageString = String.valueOf(damage);
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            return switch (key) {
                case ".069", ".068" -> ClassGameType.MAGIC;
                case ".034", ".033", ".035" -> ClassGameType.REAPER;
                case ".072", ".073", ".071" -> ClassGameType.SWORDSMAN;
                case ".016", ".015", ".017" -> ClassGameType.ARCHERY;
                default -> null;
            };

        }
        return null;
    }
}
