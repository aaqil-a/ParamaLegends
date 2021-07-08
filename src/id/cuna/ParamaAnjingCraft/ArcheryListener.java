package id.cuna.ParamaAnjingCraft;

import com.sk89q.worldedit.world.block.BlockType;
import net.royawesome.jlibnoise.module.modifier.Abs;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.Random;

public class ArcheryListener implements Listener{
    private final ParamaAnjingCraft plugin;
    public DataManager data;

    private final HashMap<Player, Integer> playerArcheryLevel = new HashMap<Player, Integer>();
    private final List<Entity> entitiesHunterEye = new ArrayList<>();
    private final List<Entity> entitiesViperBite = new ArrayList<>();
    private final List<String> playerTotsuka = new ArrayList<>();
    private final List<String> playerTotsukaCooldowns = new ArrayList<>();
    private final List<String> playerWindBoostCooldowns = new ArrayList<>();
    private final List<String> playersWindBoosted = new ArrayList<>();

    public ArcheryListener(ParamaAnjingCraft plugin) {
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Get player's archery level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        playerArcheryLevel.put(player, data.getConfig().getInt("players." + player.getUniqueId().toString() + ".archery"));
        if(playerArcheryLevel.get(player) >= 4){
            applySpeedPassive(player);
        }
    }

    //Remove player from plugin memory on leave
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        playerArcheryLevel.remove(player);
    }

    //Change player's level in playerArcheryLevle hashmap when leveling up
    public void levelUp(Player player){
        int curLevel = playerArcheryLevel.get(player);
        playerArcheryLevel.replace(player, curLevel+1);
        if(curLevel >= 4){
            applySpeedPassive(player);
        }
    }

    // Mana Handler
    public boolean subtractMana(Player player, int manaCost){
        int currMana = player.getLevel();
        if(manaCost <= currMana){
            player.setLevel(currMana - manaCost);
            return true;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "Not enough mana.");
            return false;
        }
    }

    // Determine if player swords level is high enough to cast a spell
    public boolean checkLevel(Player player, int level){
        if(playerArcheryLevel.get(player) < level){
            player.sendMessage(ChatColor.GRAY + "You do not understand how to use this ability yet.");
            return false;
        } else {
            return true;
        }
    }

    //Cooldown Handler
    public void sendCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_GREEN + spell + ChatColor.GRAY + " is on cooldown.");
    }
    public void sendNoLongerCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_GREEN + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }

    public void applySpeedPassive(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999,0));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        double damage = event.getDamage();
        if(event.getDamager() instanceof AbstractArrow) {
            Projectile projectile = (Projectile) event.getDamager();
            if(projectile.getShooter() instanceof Player){
                Player player = (Player) projectile.getShooter();
                if(event.getEntity().getLocation().distance(player.getLocation()) > 10){
                    damage *= 1.2;
                    projectile.getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0,1,0),8, 0.5, 0.5, 0.5, 0);
                }
            }
            if(projectile.getShooter() instanceof Player){
                Player player = (Player) projectile.getShooter();
                if(playersWindBoosted.contains(player.getUniqueId().toString())){
                    projectile.getWorld().spawnParticle(Particle.SWEEP_ATTACK, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0);
                }
            }
        }
        if(event.getDamager() instanceof Arrow){
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getCustomName() != null){
                switch(arrow.getCustomName()){
                    case "huntereye" -> {
                        if(!entitiesHunterEye.contains(event.getEntity())){
                            if(event.getEntity() instanceof LivingEntity && arrow.getShooter() instanceof Player){
                                entitiesHunterEye.add(event.getEntity());
                                ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0));
                                Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                                    entitiesHunterEye.remove(event.getEntity());
                                }, 200);
                            }
                        }
                    }
                    case "viperbite" -> {
                        if(!entitiesViperBite.contains(event.getEntity())){
                            entitiesViperBite.add(event.getEntity());
                            BukkitTask poison = Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
                                if(event.getEntity() instanceof Damageable && arrow.getShooter() instanceof Player){
                                    ((Damageable) event.getEntity()).damage(1.016, (Player) arrow.getShooter());
                                }
                            }, 20, 20);
                            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                poison.cancel();
                                entitiesViperBite.remove(event.getEntity());
                            }, 162);
                        }
                    }
                    case "neurotoxin" -> {
                        if(!entitiesViperBite.contains(event.getEntity())){
                            entitiesViperBite.add(event.getEntity());
                            BukkitTask neurotoxin = Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
                                if(event.getEntity() instanceof LivingEntity && arrow.getShooter() instanceof Player){
                                    ((LivingEntity) event.getEntity()).damage(3.016);
                                    ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1));
                                }
                            }, 20, 20);
                            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                neurotoxin.cancel();
                                entitiesViperBite.remove(event.getEntity());
                            }, 162);
                        }
                    }
                }
            }
        }
        if(entitiesHunterEye.contains(event.getEntity())){
            damage = plugin.increasedIncomingDamage(damage, 1.1);

        }
        event.setDamage(damage);
    }


    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        //Check if held item is book

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if(item.getItemMeta() != null){
            switch(item.getItemMeta().getDisplayName()){
                case "§aTotsuka's Creation" -> {
                    if (!isSilenced(player))
                        if(checkLevel(player, 3))
                            castTotsuka(player);
                }
                case "§aWind Boost" -> {
                    if (!isSilenced(player))
                        if(checkLevel(player, 4))
                            castWindBoost(player);
                }
            }
        }

    }

    //Deal when player places cobweb
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.getItemInHand().hasItemMeta()){
            ItemStack placed = event.getItemInHand();
            if(placed.getItemMeta().getDisplayName().equals("§aTotsuka's Creation")){
                event.setCancelled(true);
            }
        }
    }

    //Deal when projectile hits block
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();

        if (projectile instanceof Arrow && (projectile.getCustomName() != null)){
            Arrow arrow = (Arrow) projectile;
            if (event.getHitBlock() != null) {

            }
        } else if(projectile instanceof SpectralArrow && projectile.getCustomName() != null){
            SpectralArrow arrow = (SpectralArrow) projectile;
            if (event.getHitBlock() != null) {
                switch(arrow.getCustomName()){

                }
            }
        }
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

    //When player is damaged by anything
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

        }
    }

    public boolean isSilenced(Player player){
        if(!plugin.playersSilenced.contains(player)) {
            return false;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "You are silenced!");
            return true;
        }
    }

    //When player shoots bow
    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event){
        if(event.getEntity() instanceof Player){
            ItemStack arrowItem = event.getConsumable();
            ItemMeta arrowMeta = arrowItem.getItemMeta();
            Player player = (Player) event.getEntity();
            if(arrowMeta != null){
                switch(arrowMeta.getDisplayName()){
                    case "§aHunter's Eye" -> {
                        if(!isSilenced(player)){
                            castHuntersEye(player, event.getProjectile());
                        }
                    }
                    case "§aViper's Bite" -> {
                        if(!isSilenced(player)){
                            if(checkLevel(player, 2)){
                                castViperBite(player, event.getProjectile());
                            }
                        }
                    }
                    case "§aNeurotoxin" -> {
                        if(!isSilenced(player)){
                            if(checkLevel(player, 5)){
                                castNeurotoxin(player, event.getProjectile());
                            }
                        }
                    }
                }
            }
            if(event.getProjectile() instanceof AbstractArrow){
                if(playersWindBoosted.contains(player.getUniqueId().toString())){
                    double damage = ((AbstractArrow) event.getProjectile()).getDamage();
                    ((AbstractArrow) event.getProjectile()).setDamage(damage*1.15);
                    ((AbstractArrow) event.getProjectile()).setKnockbackStrength(1);
                }
            }
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

    public void castHuntersEye(Player player, Entity entity){
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(subtractMana(player, 20)){
                arrow.setCustomName("huntereye");
            }
        }
    }

    public void castViperBite(Player player, Entity entity){
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(subtractMana(player, 15)){
                arrow.setCustomName("viperbite");
            }
        }
    }

    public void castTotsuka(Player player){
        if(playerTotsukaCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Totsuka's Creation");
        } else if (subtractMana(player, 40)) {
            Snowball ball = player.launchProjectile(Snowball.class);
            ball.setCustomName("totsuka");
            Vector velocity = ball.getVelocity();
            velocity.multiply(0.5);
            ball.setItem(new ItemStack(Material.COBWEB));
            ball.setGravity(true);
            ball.setVelocity(velocity);
            playerTotsukaCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerTotsukaCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Totsuka's Creation");
                    playerTotsukaCooldowns.remove(player.getUniqueId().toString());
                }
            }, 600);
        }
    }

    public void castWindBoost(Player player){
        if(playerWindBoostCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Wind Boost");
        } else if (subtractMana(player, 60)) {
            player.sendMessage(ChatColor.GREEN+"Wind Boost activated.");
            playersWindBoosted.add(player.getUniqueId().toString());
            playerWindBoostCooldowns.add(player.getUniqueId().toString());
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0,1,0),8, 0.5, 0.5, 0.5, 0);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(ChatColor.GREEN+"Wind Boost wore off.");
                playersWindBoosted.remove(player.getUniqueId().toString());
            }, 240);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerWindBoostCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Wind Boost");
                    playerWindBoostCooldowns.remove(player.getUniqueId().toString());
                }
            }, 600);
        }
    }

    public void castNeurotoxin(Player player, Entity entity){
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(subtractMana(player, 50)){
                arrow.setCustomName("neurotoxin");
            }
        }
    }
}

