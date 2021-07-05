package id.cuna.ParamaAnjingCraft;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


public class MagicListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;
    private final int[] maxMana = {0,50,100,150,200,250,300,400,500,600,800};
    private final int[] manaRegen = {0,1,1,2,3,3,4,5,6,7,8};
    private final List<EnderPearl> castedOrb = new ArrayList<EnderPearl>();
    private final List<Player> playerOrbCooldowns = new ArrayList<Player>();
    private final List<Player> playerBallCooldowns = new ArrayList<Player>();
    private final List<Player> playerIgniteCooldowns = new ArrayList<Player>();
    private final List<Player> playerGustCooldowns = new ArrayList<Player>();
    private final List<Player> playerBlinkCooldowns = new ArrayList<Player>();
    private final List<Player> playerLightningCooldowns = new ArrayList<Player>();
    private final HashMap<EnderPearl, BukkitTask> orbFlashTasks = new HashMap<>();
    private final HashMap<Entity, BukkitTask> entityIgnitedTasks = new HashMap<>();
    private final HashMap<Entity, Integer> entityIgnitedDuration = new HashMap<>();
    private final HashMap<Player, Vector> ballOffsetVectors = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerLifeDrainTasks = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerManaRegenTasks = new HashMap<>();
    private final HashMap<Player, Integer> playerMagicLevel = new HashMap<Player, Integer>();



    public MagicListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        Player player = (Player) event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        //Check if player is holding an enchanted book
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType().equals(Material.ENCHANTED_BOOK)){
            if(item.getItemMeta() != null){
                //Call cast spell function according to book name
                switch(item.getItemMeta().getDisplayName()){
                    case "§5Fling Earth":
                        castFlingEarth(player);
                        break;
                    case "§5Ignite":
                        if(checkLevel(player, 2)){
                            castIgnite(player);
                        }
                        break;
                    case "§5Gust":
                        if(checkLevel(player, 3)){
                            castGust(player);
                        }
                        break;
                    case "§5Life Drain":
                        if(checkLevel(player, 4)){
                            castLifeDrain(player);
                        }
                        break;
                    case "§5Blink":
                        if(checkLevel(player, 5)){
                            castBlink(player);
                        }
                        break;
                    case "§5Summon Lightning":
                        if(checkLevel(player, 6)){
                            castSummonLightning(player);
                        }
                        break;
                    case "§5Illusory Orb":
                        if(checkLevel(player, 7)){
                            castIllusoryOrb(player);
                        }
                        break;
                }
            }
        }
    }

    //Get player's magic level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        playerMagicLevel.put(player, data.getConfig().getInt("players." + player.getUniqueId().toString() + ".magic"));
        player.setExp(0);
        player.setLevel(maxMana[playerMagicLevel.get(player)]);
        //Create task to regenerate mana over time
        playerManaRegenTasks.put(player,
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                int curMana = player.getLevel();
                if(curMana < maxMana[playerMagicLevel.get(player)]){
                    curMana += manaRegen[playerMagicLevel.get(player)] ;
                    if(curMana > maxMana[playerMagicLevel.get(player)])
                        curMana = maxMana[playerMagicLevel.get(player)];
                    player.setExp(0);
                    player.setLevel(curMana);
                }
            }, 0, 20)
        );
    }

    //Cancel all exp gained
    @EventHandler
    public void onPlayerXpChange(PlayerExpChangeEvent event){
        event.setAmount(0);
    }

    //Remove player from plugin memory on leave
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        playerManaRegenTasks.get(player).cancel();
        playerManaRegenTasks.remove(player);
        playerMagicLevel.remove(player);
    }

    //Change player's level in playerMagicLevel hashmap when leveling up
    public void levelUp(Player player){
        int curLevel = playerMagicLevel.get(player);
        playerMagicLevel.replace(player, curLevel+1);
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
                    plugin.experienceListener.addExp((Player) projectile.getShooter(), "magic", 1);
                    Damageable hit = (Damageable) event.getHitEntity();
                    hit.damage(20.069, (Player) projectile.getShooter());
                }
            }
        } else if(projectile instanceof Snowball && projectile.getCustomName() != null){ // Check if ice ball hits
            event.setCancelled(true);
            if(projectile.getCustomName().equals("iceball")){
                if(event.getHitEntity() != null){
                    if(event.getHitEntity() instanceof Damageable){
                        ArmorStand source = (ArmorStand) projectile.getShooter();
                        Player player = plugin.getServer().getPlayer(source.getName());
                        plugin.experienceListener.addExp(player, "magic", 1);
                        Damageable hit = (Damageable) event.getHitEntity();
                        hit.damage(5.069, player);
                    }
                }
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

    // Determine if player magic level is high enough to cast a spell
    public boolean checkLevel(Player player, int level){
        if(playerMagicLevel.get(player) < level){
            player.sendMessage(ChatColor.GRAY + "You do not understand how to use this spell yet.");
            return false;
        } else {
            return true;
        }
    }

    // Determine if player has enough mana to cast a spell and subtract mana if possible
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

    public void sendCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.GRAY + " is on cooldown.");
    }
    public void sendNoLongerCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }
    public void sendOutOfRangeMessage(Player player){
        player.sendMessage(ChatColor.GRAY + "Out of range to cast spell.");
    }

    //Teleports player to a safe area near a location if exists
    public void teleportToAir(Player player, Location location){
        if(location.getBlock().getType().isAir()){
            player.teleport(location);
        } else if(location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
            player.teleport(location.add(0,1,0));
        } else if(location.getBlock().getRelative(BlockFace.DOWN).getType().isAir()){
            player.teleport(location.add(0,-1,0));
        } else if(location.getBlock().getRelative(BlockFace.NORTH).getType().isAir()){
            player.teleport(location.add(0,0,-1));
        } else if(location.getBlock().getRelative(BlockFace.SOUTH).getType().isAir()){
            player.teleport(location.add(0,0,1));
        } else if(location.getBlock().getRelative(BlockFace.WEST).getType().isAir()){
            player.teleport(location.add(-1,0,0));
        } else if(location.getBlock().getRelative(BlockFace.EAST).getType().isAir()){
            player.teleport(location.add(1,0,0));
        }
    }

    public void castFlingEarth(Player player){
        if(playerBallCooldowns.contains(player)){
            sendCooldownMessage(player, "Fling Earth");
        } else if (subtractMana(player, 10)) {
            Location location = player.getEyeLocation();
            Vector offset = player.getEyeLocation().getDirection().multiply(2.5);
            location.add(offset);
            ArmorStand dummy = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND);
            dummy.setCustomName(player.getName());
            dummy.setVisible(false);
            dummy.setGravity(false);
            dummy.setInvulnerable(true);
            playerBallCooldowns.add(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(location);
                dummy.getLocation().add(new Vector(0,1,0));
            }, 2);
            BukkitTask ballEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Location newLocation = player.getEyeLocation();
                Vector newOffset = player.getEyeLocation().getDirection().multiply(2.5);
                newLocation.add(newOffset);
                ballOffsetVectors.put(player, newOffset);
                dummy.teleport(newLocation);
                dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.SNOW_BLOCK.createBlockData());
                dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.STONE.createBlockData());
                dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.DIRT.createBlockData());
            },3, 3);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(dummy.getLocation().add(new Vector(0,-2,0)));
                Snowball ball = dummy.launchProjectile(Snowball.class, ballOffsetVectors.get(player));
                dummy.remove();
                ball.setCustomName("iceball");
                Vector velocity = ball.getVelocity();
                velocity.multiply(0.5);
                ball.setItem(new ItemStack(Material.DIRT));
                ballEffect.cancel();
                ball.setGravity(true);
                ball.setVelocity(velocity);
            }, 20);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerBallCooldowns.contains(player)){
                    sendNoLongerCooldownMessage(player, "Fling Earth");
                    playerBallCooldowns.remove(player);
                }
            }, 60);
        }
    }

    public void castIgnite(Player player){
        if(playerIgniteCooldowns.contains(player)){
            sendCooldownMessage(player, "Ignite");
        } else if (subtractMana(player, 20)) {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(20));
            }
            player.getWorld().spawnParticle(Particle.FLAME, location.add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
            player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.add(0,1,0), 5,0.5,0.5,0.5,0);
            List<Entity> entities = player.getWorld().getNearbyEntities(location, 2,2,2).stream().toList();
            for(Entity ignited : entities){
                if(ignited instanceof Player){
                    continue;
                }
                if(ignited instanceof Damageable){
                    plugin.experienceListener.addExp(player, "magic", 1);
                    entityIgnitedDuration.put(ignited, 5);
                    entityIgnitedTasks.put(ignited,
                            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                int duration = entityIgnitedDuration.get(ignited);
                                duration--;
                                if (duration >= 0){
                                    ignited.getWorld().spawnParticle(Particle.SMALL_FLAME, ignited.getLocation().add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
                                    entityIgnitedDuration.replace(ignited, duration);
                                    ((Damageable) ignited).damage(3.069, player);
                                } else {
                                    entityIgnitedTasks.get(ignited).cancel();
                                    entityIgnitedTasks.remove(ignited);
                                    entityIgnitedDuration.remove(ignited);
                                }
                            }, 0, 20));
                }
            }
            playerIgniteCooldowns.add(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerIgniteCooldowns.contains(player)){
                    sendNoLongerCooldownMessage(player, "Ignite");
                    playerIgniteCooldowns.remove(player);
                }
            }, 140);
        }
    }

    public void castGust(Player player){
        if(playerGustCooldowns.contains(player)){
            sendCooldownMessage(player, "Gust");
        } else if (subtractMana(player, 10)) {
            playerGustCooldowns.add(player);
            Location location = player.getLocation();
            double playerX = location.getX();
            double playerY = location.getY();
            double playerZ = location.getZ();
            double boxX1 = playerX, boxX2 = playerX, boxZ1 = playerZ, boxZ2 = playerZ;
            double boxY1 = playerY - 3, boxY2 = playerY + 3;
            Vector knockback = new Vector(0, 0, 0);
            Vector direction = player.getLocation().getDirection();
            direction.setY(0);
            direction.normalize();
            BlockFace facing = player.getFacing();
            if(direction.getX() < Math.sin(Math.PI/8) && direction.getX() > -1*Math.sin(Math.PI/8)){
                if(direction.getZ() >= 0){
                    boxX1 += 2.5;
                    boxX2 -= 2.5;
                    boxZ2 += 4.5;
                    knockback.setZ(4);
                } else {
                    boxX1 -= 2.5;
                    boxX2 += 2.5;
                    boxZ2 -= 4.5;
                    knockback.setZ(-4);
                }
            } else if(direction.getZ() < Math.sin(Math.PI/8) && direction.getZ() > -1*Math.sin(Math.PI/8)) {
                if(direction.getX() >= 0){
                    boxZ1 += 2.5;
                    boxZ2 -= 2.5;
                    boxX2 += 4.5;
                    knockback.setX(4);
                } else {
                    boxZ1 -= 2.5;
                    boxZ2 += 2.5;
                    boxX2 -= 4.5;
                    knockback.setX(-4);
                }
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)){
                boxX2 += 3.5;
                boxZ2 += 3.5;
                knockback.setX(2);
                knockback.setZ(2);
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 3.5;
                boxZ2 += 3.5;
                knockback.setX(-2);
                knockback.setZ(2);
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 3.5;
                boxZ2 -= 3.5;
                knockback.setX(-2);
                knockback.setZ(-2);
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)) {
                boxX2 += 3.5;
                boxZ2 -= 3.5;
                knockback.setX(2);
                knockback.setZ(-2);
            }
            BoundingBox gustBox = new BoundingBox(boxX1,boxY1, boxZ1, boxX2, boxY2, boxZ2);
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getEyeLocation().add(player.getLocation().getDirection().multiply(2)).add(new Vector(0,0.5,0)), 8, 1, 0.5, 1, 0);
            List<Entity> entities = player.getWorld().getNearbyEntities(gustBox).stream().toList();
            for(Entity knocked : entities){
                if(knocked.equals(player) || knocked instanceof Villager){
                    continue;
                }
                if(knocked instanceof Damageable){
                    plugin.experienceListener.addExp(player, "magic", 1);
                    Vector velocity = knocked.getVelocity();
                    knocked.setVelocity(velocity.add(knockback));
                    ((Damageable) knocked).damage(2.069, player);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerGustCooldowns.contains(player)){
                    sendNoLongerCooldownMessage(player, "Gust");
                    playerGustCooldowns.remove(player);
                }
            }, 200);
        }
    }

    public void castLifeDrain(Player player){
        if(playerLifeDrainTasks.containsKey(player)){
            player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
            playerLifeDrainTasks.get(player).cancel();
            playerLifeDrainTasks.remove(player);
        } else {
            player.sendMessage(ChatColor.GREEN + "Life Drain activated.");
            playerLifeDrainTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if(subtractMana(player, 10)){
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 8, 1, 0.5, 1, 0);
                    List<Entity> entities = player.getNearbyEntities(2,2,2);
                    int count = 0;
                    for(Entity drained : entities){
                        if(drained instanceof Player){
                            continue;
                        }
                        if(drained instanceof Damageable){
                            count++;
                            plugin.experienceListener.addExp(player, "magic", 1);
                            ((Damageable) drained).damage(5.069, player);
                        }
                        if(count >= 4){
                            break;
                        }
                    }
                    if(count > 0){
                        if(count > 2 && player.getHealth() <= 18){
                            player.setHealth(player.getHealth()+2);
                        } else if(player.getHealth() <= 19){
                            player.setHealth(player.getHealth()+1);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
                    playerLifeDrainTasks.get(player).cancel();
                    playerLifeDrainTasks.remove(player);
                }
            }, 0, 20));
        }
    }

    public void castBlink(Player player){
        if(playerBlinkCooldowns.contains(player)){
            sendCooldownMessage(player, "Blink");
        } else if (subtractMana(player, 10)) {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(20));
            }
            playerBlinkCooldowns.add(player);
            location.setDirection(player.getLocation().getDirection());
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getEyeLocation(), 10, 0.5, 0.5, 0.5);
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, location.add(new Vector(0,2,0)), 10, 0.5, 0.5, 0.5);
            teleportToAir(player, location);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerBlinkCooldowns.contains(player)){
                    sendNoLongerCooldownMessage(player, "Blink");
                    playerBlinkCooldowns.remove(player);
                }
            }, 300);
        }
    }

    public void castSummonLightning(Player player){
        if(playerLightningCooldowns.contains(player)){
            sendCooldownMessage(player, "Summon Lightning");
        } else if (subtractMana(player, 150)) {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 50, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                sendOutOfRangeMessage(player);
                return;
            }
            playerLightningCooldowns.add(player);
            player.getWorld().strikeLightningEffect(location);
            player.getWorld().spawnParticle(Particle.FLASH, location.add(new Vector(0,1,0)), 5);
            player.getWorld().getHighestBlockAt(location.add(1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
            player.getWorld().getHighestBlockAt(location.add(0,0,1)).getRelative(BlockFace.UP).setType(Material.FIRE);
            player.getWorld().getHighestBlockAt(location.add(-1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
            player.getWorld().getHighestBlockAt(location.add(0,0,-1)).getRelative(BlockFace.UP).setType(Material.FIRE);
            List<Entity> entities = player.getWorld().getNearbyEntities(location, 4,4,4).stream().toList();
            for(Entity ignited : entities){
                if(ignited instanceof Damageable){
                    plugin.experienceListener.addExp(player, "magic", 1);
                    ((Damageable) ignited).damage(20.069, player);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerLightningCooldowns.contains(player)){
                    sendNoLongerCooldownMessage(player, "Summon Lightning");
                    playerLightningCooldowns.remove(player);
                }
            }, 300);
        }
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
                    teleportToAir(player, orb.getLocation());
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
            if(playerOrbCooldowns.contains(player)){
                sendCooldownMessage(player, "Illusory Orb");
            } else if (subtractMana(player, 100)) {
                EnderPearl newOrb = player.launchProjectile(EnderPearl.class);
                playerOrbCooldowns.add(player);
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
                    if(playerOrbCooldowns.contains(player)){
                        sendNoLongerCooldownMessage(player, "Illusory Orb");
                        playerOrbCooldowns.remove(player);
                    }
                }, 200);
            }
        }
    }
}
