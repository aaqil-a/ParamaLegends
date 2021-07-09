package id.cuna.ParamaAnjingCraft;

import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
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
    private final List<String> playerOrbCooldowns = new ArrayList<String>();
    private final List<String> playerBallCooldowns = new ArrayList<String>();
    private final List<String> playerIgniteCooldowns = new ArrayList<String>();
    private final List<String> playerGustCooldowns = new ArrayList<String>();
    private final List<String> playerBlinkCooldowns = new ArrayList<String>();
    private final List<String> playerLightningCooldowns = new ArrayList<String>();
    private final List<String> playerDragonBreathCooldowns = new ArrayList<String>();
    private final List<String> playerVoicesCooldowns = new ArrayList<String>();
    private final List<String> playerNovaCooldowns = new ArrayList<String>();
    private final HashMap<EnderPearl, BukkitTask> orbFlashTasks = new HashMap<>();
    private final HashMap<Entity, BukkitTask> entityIgnitedTasks = new HashMap<>();
    private final HashMap<Entity, Integer> entityIgnitedDuration = new HashMap<>();
    private final HashMap<Player, Vector> ballOffsetVectors = new HashMap<>();
    private final HashMap<Player, Snowball> ballsThrown = new HashMap<>();
    private final HashMap<Player, BukkitTask> ballsThrownTasks = new HashMap<>();
    private final HashMap<Player, FallingBlock> ballsDirt = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerLifeDrainTasks = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerManaRegenTasks = new HashMap<>();
    private final HashMap<Player, Integer> playerMagicLevel = new HashMap<Player, Integer>();
    private final HashMap<String, Integer> playerCurrentLevel = new HashMap<String, Integer>();
    //private final HashMap<Location, Material> blocksReplacedByBeacon = new HashMap<>();



    public MagicListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public boolean isSilenced(Player player){
        if(!plugin.playersSilenced.contains(player)) {
            return false;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "You are silenced!");
            return true;
        }
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
        if (item.getType().equals(Material.ENCHANTED_BOOK)){
            if(item.getItemMeta() != null){
                //Call cast spell function according to book name
                switch(item.getItemMeta().getDisplayName()){
                    case "§5Fling Earth":
                        if(!isSilenced(player))
                            castFlingEarth(player);
                        break;
                    case "§5Ignite":
                        if(checkLevel(player, 2) && !isSilenced(player)){
                            castIgnite(player);
                        }
                        break;
                    case "§5Gust":
                        if(checkLevel(player, 3) && !isSilenced(player)){
                            castGust(player);
                        }
                        break;
                    case "§5Life Drain":
                        if(checkLevel(player, 4) && !isSilenced(player)){
                            castLifeDrain(player);
                        }
                        break;
                    case "§5Blink":
                        if(checkLevel(player, 5) && !isSilenced(player)){
                            castBlink(player);
                        }
                        break;
                    case "§5Summon Lightning":
                        if(checkLevel(player, 6) && !isSilenced(player)){
                            castSummonLightning(player);
                        }
                        break;
                    case "§5Illusory Orb":
                        if(checkLevel(player, 7) && !isSilenced(player)){
                            castIllusoryOrb(player);
                        }
                        break;
                    case "§5Dragon's Breath":
                        if(checkLevel(player, 8) && !isSilenced(player)){
                            castDragonBreath(player);
                        }
                        break;
                    case "§5Voices of the Damned":
                        if(checkLevel(player, 9) && !isSilenced(player)){
                            castVoicesOfTheDamned(player);
                        }
                        break;
                    case "§5Nova":
                        if(checkLevel(player, 10) && !isSilenced(player)){
                            castNova(player);
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
        int magicLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".magic");
        int swordsLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".swordsmanship");
        int miningLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".mining");
        int archeryLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".archery");
        int reaperLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaper");
        int manaLevel = (magicLevel+swordsLevel+miningLevel+archeryLevel+reaperLevel)/5;
        playerMagicLevel.put(player, magicLevel);
        player.setExp(0);
        if(playerCurrentLevel.containsKey(player.getUniqueId().toString())){
            player.setLevel(playerCurrentLevel.get(player.getUniqueId().toString()));
        } else {
            player.setLevel(maxMana[manaLevel]);
        }
        //Create task to regenerate mana over time
        playerManaRegenTasks.put(player,
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                int curMana = player.getLevel();
                if(curMana < maxMana[manaLevel]){
                    curMana += manaRegen[manaLevel] ;
                    if(curMana > maxMana[manaLevel])
                        curMana = maxMana[manaLevel];
                    player.setExp(0);
                    player.setLevel(curMana);
                    playerCurrentLevel.put(player.getUniqueId().toString(), curMana);
                }
                if(player.getInventory().getItemInOffHand().getType().equals(Material.SHIELD)){
                    ItemStack item = player.getInventory().getItemInOffHand();
                    player.getInventory().setItemInOffHand(null);
                    player.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GRAY + "The shield feels much to heavy to use on one hand.");
                }
            }, 0, 20)
        );
    }

    //Cancel all exp gained
    @EventHandler
    public void onPlayerXpChange(PlayerExpChangeEvent event){
        event.setAmount(0);
    }

    //Remove player from plugin memory on leave but store player current mana
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
            if(projectile.getCustomName().equals("iceball")){
                event.setCancelled(true);
                ArmorStand source = (ArmorStand) projectile.getShooter();
                Player player = plugin.getServer().getPlayer(source.getName());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(ballsDirt.get(player) != null)
                        ballsDirt.get(player).remove();
                    ballsDirt.remove(player);
                }, 2);
                ballOffsetVectors.remove(player);
                ballsThrown.remove(player);
                cancelFlingEarthTasks(player);
                if(event.getHitEntity() != null){
                    if(event.getHitEntity() instanceof Damageable){
                        plugin.experienceListener.addExp(player, "magic", 1);
                        Damageable hit = (Damageable) event.getHitEntity();
                        hit.damage(5.069, player);
                    }
                }
            }
        }
    }

    //Check if damned soul hit entity
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Phantom || event.getDamager() instanceof Silverfish){
            if(event.getDamager().getCustomName() != null && event.getDamager().getCustomName().equals(ChatColor.DARK_PURPLE+"Damned Soul")){
                Player summoner = null;
                for (Player player: Bukkit.getOnlinePlayers())
                    if(event.getDamager().hasMetadata(player.getName()))
                        summoner = player;
                plugin.experienceListener.addExp(summoner, "magic", 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(event.getDamager() instanceof Phantom){
                        event.getDamager().getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, event.getDamager().getLocation(), 8, 0.5, 0.5, 0.5, 0);
                    } else {
                        event.getDamager().getWorld().spawnParticle(Particle.SMOKE_NORMAL, event.getDamager().getLocation(), 8, 0.5, 0.5, 0.5, 0);
                    }
                    event.getDamager().remove();
                }, 60);

                //Check if damned killed enemy and give exp if so
                Damageable victim = (Damageable) event.getEntity();
                if(event.getFinalDamage() > victim.getHealth()){
                    String mob = "";
                    switch (event.getEntityType()) {
                        case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED -> mob = "zombie";
                        case WITCH -> mob = "witch";
                        case SKELETON, STRAY -> mob = "skeleton";
                        case CREEPER -> mob = "creeper";
                        case SPIDER, CAVE_SPIDER -> mob = "spider";
                    }
                    //Grant exp and lectrum to player according to mob killed
                    if(!mob.equals("")){
                        plugin.experienceListener.addExp(summoner, "magic", data.getConfig().getInt("mobs."+mob+".exp"));
                        plugin.experienceListener.addLectrum(summoner, data.getConfig().getInt("mobs."+mob+".lectrum"));
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

    //Make damned not burn
    @EventHandler
    public void onEntityBurn(EntityCombustEvent event){
        Entity entity = event.getEntity();
        if(entity.getCustomName() != null){
            if(entity.getCustomName().contains("Damned Soul") || entity.getCustomName().contains("Soulstring"))
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

    public void cancelFlingEarthTasks(Player player){
        if(ballsThrownTasks.get(player) != null)
            ballsThrownTasks.get(player).cancel();
        ballsThrownTasks.remove(player);
    }

    public void createFireworkEffect(Location location, Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BURST)
                .flicker(false).trail(false).withColor(Color.WHITE, Color.YELLOW).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.setSilent(true);
        Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 1);
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
        if(playerBallCooldowns.contains(player.getUniqueId().toString())){
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
            playerBallCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(location);
                dummy.getLocation().add(new Vector(0,1,0));
            }, 1);
            BukkitTask ballEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Location newLocation = player.getEyeLocation();
                Vector newOffset = player.getEyeLocation().getDirection().multiply(2.5);
                newLocation.add(newOffset);
                ballOffsetVectors.put(player, newOffset);

                dummy.teleport(newLocation);

                dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.STONE.createBlockData());
                dummy.getWorld().spawnParticle(Particle.BLOCK_CRACK, dummy.getLocation(), 1, 0.25, 0.25, 0.25, 0, Material.DIRT.createBlockData());
            },2, 1);
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
                ballsThrown.put(player, ball);
            }, 20);
            ballsThrownTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if(ballsDirt.containsKey(player)){
                    ballsDirt.get(player).remove();
                    ballsDirt.remove(player);
                }
                Snowball ball = ballsThrown.get(player);
                if(ball != null) {
                    ballsDirt.put(player, ball.getWorld().spawnFallingBlock(ball.getLocation(), Material.DIRT.createBlockData()));
                    ballsDirt.get(player).setGravity(false);
                } else {
                    cancelFlingEarthTasks(player);
                }
            }, 21, 1));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerBallCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Fling Earth");
                    playerBallCooldowns.remove(player.getUniqueId().toString());
                }
            }, 60);
        }
    }

    public void castIgnite(Player player){
        if(playerIgniteCooldowns.contains(player.getUniqueId().toString())){
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
            playerIgniteCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerIgniteCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Ignite");
                    playerIgniteCooldowns.remove(player.getUniqueId().toString());
                }
            }, 140);
        }
    }

    public void castGust(Player player){
        if(playerGustCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Gust");
        } else if (subtractMana(player, 30)) {
            playerGustCooldowns.add(player.getUniqueId().toString());
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
                if(playerGustCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Gust");
                    playerGustCooldowns.remove(player.getUniqueId().toString());
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
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(new Vector(0,1,0)), 8, 1, 0.5, 1, 0);
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
        if(playerBlinkCooldowns.contains(player.getUniqueId().toString())){
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
            playerBlinkCooldowns.add(player.getUniqueId().toString());
            location.setDirection(player.getLocation().getDirection());
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getEyeLocation(), 10, 0.5, 0.5, 0.5);
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, location.add(new Vector(0,2,0)), 10, 0.5, 0.5, 0.5);
            teleportToAir(player, location);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerBlinkCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Blink");
                    playerBlinkCooldowns.remove(player.getUniqueId().toString());
                }
            }, 300);
        }
    }

    public void castSummonLightning(Player player){
        if(playerLightningCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Summon Lightning");
        } else {
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
            if (subtractMana(player, 150)) {

                playerLightningCooldowns.add(player.getUniqueId().toString());
                player.getWorld().strikeLightningEffect(location);
                player.getWorld().spawnParticle(Particle.FLASH, location.add(new Vector(0,1,0)), 5);
                player.getWorld().getHighestBlockAt(location.clone().add(0,1,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(0,0,1)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(-1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(0,0,-1)).getRelative(BlockFace.UP).setType(Material.FIRE);
                List<Entity> entities = player.getWorld().getNearbyEntities(location, 3,4,3).stream().toList();
                for(Entity ignited : entities){
                    if(ignited instanceof Damageable && !(ignited instanceof Player)){
                        plugin.experienceListener.addExp(player, "magic", 1);
                        ((Damageable) ignited).damage(20.069, player);
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerLightningCooldowns.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Summon Lightning");
                        playerLightningCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 600);
            }
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
            if(playerOrbCooldowns.contains(player.getUniqueId().toString())){
                sendCooldownMessage(player, "Illusory Orb");
            } else if (subtractMana(player, 100)) {
                EnderPearl newOrb = player.launchProjectile(EnderPearl.class);
                playerOrbCooldowns.add(player.getUniqueId().toString());
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
                    if(playerOrbCooldowns.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Illusory Orb");
                        playerOrbCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 200);
            }
        }
    }

    public void castDragonBreath(Player player){
        if(playerDragonBreathCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Dragon's Breath");
        } else if (subtractMana(player, 200)) {
            playerDragonBreathCooldowns.add(player.getUniqueId().toString());
            Location location = player.getLocation();
            double playerX = location.getX();
            double playerY = location.getY();
            double playerZ = location.getZ();
            double boxX1 = playerX, boxX2 = playerX, boxZ1 = playerZ, boxZ2 = playerZ;
            double boxY1 = playerY - 3, boxY2 = playerY + 3;
            Vector direction = player.getLocation().getDirection();
            direction.setY(0);
            direction.normalize();
            if(direction.getX() < Math.sin(Math.PI/8) && direction.getX() > -1*Math.sin(Math.PI/8)){
                if(direction.getZ() >= 0){
                    boxX1 += 3;
                    boxX2 -= 3;
                    boxZ2 += 8;
                } else {
                    boxX1 -= 3;
                    boxX2 += 3;
                    boxZ2 -= 8;
                }
            } else if(direction.getZ() < Math.sin(Math.PI/8) && direction.getZ() > -1*Math.sin(Math.PI/8)) {
                if(direction.getX() >= 0){
                    boxZ1 += 3;
                    boxZ2 -= 3;
                    boxX2 += 8;
                } else {
                    boxZ1 -= 3;
                    boxZ2 += 3;
                    boxX2 -= 8;
                }
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)){
                boxX2 += 5;
                boxZ2 += 5;
            } else if(direction.getZ() > Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 5;
                boxZ2 += 5;
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() < -1*Math.sin(Math.PI/8)){
                boxX2 -= 5;
                boxZ2 -= 5;
            } else if(direction.getZ() < -1*Math.sin(Math.PI/8) && direction.getX() > Math.sin(Math.PI/8)) {
                boxX2 += 5;
                boxZ2 -= 5;
            }
            BoundingBox gustBox = new BoundingBox(boxX1,boxY1, boxZ1, boxX2, boxY2, boxZ2);
            Location breathLocation = player.getLocation().add(player.getLocation().getDirection().setY(0).normalize().multiply(5));
            breathLocation.add(0,0.5,0);

            //test
            BukkitTask dragonBreath = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, breathLocation, 64, 1, 0, 1, 0);
                List<Entity> entities = player.getWorld().getNearbyEntities(gustBox).stream().toList();
                for(Entity knocked : entities){
                    if(knocked.equals(player)){
                        continue;
                    }
                    if(knocked instanceof Damageable){
                        plugin.experienceListener.addExp(player, "magic", 1);
                        ((Damageable) knocked).damage(10.069, player);
                    }
                }
            }, 3, 20);
            Bukkit.getScheduler().runTaskLater(plugin, dragonBreath::cancel, 125);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerDragonBreathCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Dragon's Breath");
                    playerDragonBreathCooldowns.remove(player.getUniqueId().toString());
                }
            }, 400);
        }
    }

    public void castVoicesOfTheDamned(Player player){
        if(playerVoicesCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Voices of the Damned");
        } else if (subtractMana(player, 400)) {
            Location location = player.getEyeLocation();
            Vector offset = player.getEyeLocation().getDirection().setY(0).normalize().multiply(5);
            location.add(offset);
            ArmorStand dummy = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND);
            dummy.setVisible(false);
            dummy.setCustomName(ChatColor.DARK_PURPLE + player.getName() +"'s Portal");
            dummy.setCustomNameVisible(true);
            dummy.setGravity(false);
            dummy.setInvulnerable(true);
            playerVoicesCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(location);
                dummy.getLocation().add(new Vector(0,1,0));
            }, 2);
            BukkitTask portalEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    dummy.getWorld().spawnParticle(Particle.CLOUD, dummy.getLocation(), 1, 0.25, 1, 0.25, 0);
                    dummy.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, dummy.getLocation(), 1, 0.25, 1, 0.25, 0);
                    dummy.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, dummy.getLocation(), 1, 0.25, 1, 0.25,0);
            },3, 3);
            List<Phantom> damnedListPhantom = new ArrayList<Phantom>();
            BukkitTask portalSpawnPhantom = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Phantom damned = (Phantom) dummy.getWorld().spawnEntity(dummy.getLocation(), EntityType.PHANTOM);
                damned.setMetadata(player.getName(), new FixedMetadataValue(plugin, "summoner"));
                damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                damned.setCustomNameVisible(true);
                damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(20.069);
                damned.setTarget(null);
                damnedListPhantom.add(damned);
            },3, 100);
            List<Silverfish> damnedListSilverfish = new ArrayList<Silverfish>();
            BukkitTask portalSpawnSilverfish = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Silverfish damned = (Silverfish) dummy.getWorld().spawnEntity(dummy.getLocation(), EntityType.SILVERFISH);
                damned.setMetadata(player.getName(), new FixedMetadataValue(plugin, "summoner"));
                damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                damned.setCustomNameVisible(true);
                damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10.069);
                damned.setTarget(null);
                damnedListSilverfish.add(damned);
            },53, 100);
            BukkitTask setDamnedTarget = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                List<Entity> entities = player.getNearbyEntities(10,10,10).stream().toList();
                double closestDistance = Double.MAX_VALUE;
                Entity closestEntity = null;
                for(Entity entity : entities){
                    if(entity instanceof Villager || entity instanceof Player || entity instanceof Silverfish
                            || entity instanceof ArmorStand || entity instanceof Phantom)
                        continue;
                    if(entity instanceof LivingEntity){
                        double distance = entity.getLocation().distance(player.getLocation());
                        if(distance < closestDistance){
                            closestDistance = distance;
                            closestEntity = entity;
                        }
                    }
                }
                if(closestEntity != null){
                    for(Phantom damned : damnedListPhantom)
                        if(damned.getTarget() == null)
                            damned.setTarget((LivingEntity) closestEntity);
                    for(Silverfish damned : damnedListSilverfish)
                        if(damned.getTarget() == null)
                            damned.setTarget((LivingEntity) closestEntity);
                }
            },3, 20);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                portalEffect.cancel();
                portalSpawnPhantom.cancel();
                portalSpawnSilverfish.cancel();
                dummy.remove();
            }, 600);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                setDamnedTarget.cancel();
                for(Phantom damned : damnedListPhantom)
                    damned.remove();
                for(Silverfish damned : damnedListSilverfish)
                    damned.remove();
            }, 800);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerVoicesCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Voices of the Damned");
                    playerVoicesCooldowns.remove(player.getUniqueId().toString());
                }
            }, 1200);
        }
    }


    public void castNova(Player player){
        if(playerNovaCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Nova");
        } else {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 50, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location locationExplosion = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    locationExplosion = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    locationExplosion = rayTrace.getHitBlock().getLocation();
                }
            } else{
                sendOutOfRangeMessage(player);
                return;
            }
            if(locationExplosion.distance(player.getLocation())<10){
                player.sendMessage(ChatColor.GRAY+"Target too close to caster.");
                return;
            }
            if (subtractMana(player, 600)) {

 //               Location location = player.getEyeLocation();
//                Vector offset = player.getEyeLocation().getDirection().setY(0).normalize().multiply(2.5);
//                location.add(offset);
//                ArmorStand dummyText = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND);;
//                dummyText.setCustomName(ChatColor.GRAY+"||||||||||");
//                dummyText.setCustomNameVisible(true);
//                dummyText.setVisible(false);
//                dummyText.setGravity(false);
//                dummyText.setInvulnerable(true);
//                playerNovaCooldowns.add(player.getUniqueId().toString());
//                Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                    dummyText.teleport(location.add(new Vector(0,-2,0)));
//                }, 1);
//                novaLoadingProgress.put(player, 0);
//
                Location finalLocationExplosion = locationExplosion.clone().add(0,1,0);
                Location startExplosionFlash = locationExplosion.clone().add(0,40,0);
                player.getWorld().strikeLightningEffect(finalLocationExplosion);
//                BukkitTask loadingEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
//                    StringBuilder dummyTextName = new StringBuilder(ChatColor.GREEN + "");
//                    int ballProgress = novaLoadingProgress.get(player);
//                    int ballToLoad = 10-ballProgress;
//                    dummyTextName.append("|".repeat(ballProgress));
//                    dummyTextName.append(ChatColor.GRAY);
//                    if(ballToLoad>0){
//                        dummyTextName.append("|".repeat(ballToLoad));
//                    }
//                    dummyText.setCustomName(dummyTextName.toString());
//                    novaLoadingProgress.put(player, ballProgress+1);
//                },2, 10);
                BukkitTask preExplosionEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, finalLocationExplosion, 16, 0.5,0.5,0.5,0);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, finalLocationExplosion, 16, 0.5,2,0.5,0);
                },2, 10);
//                BukkitTask followPlayer = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
//                    Location newLocation = player.getEyeLocation();
//                    Vector newOffset = player.getEyeLocation().getDirection().multiply(2.5);
//                    newLocation.add(newOffset);
//
//                    dummyText.teleport(newLocation.add(new Vector(0,-2,0)));
//                },2, 1);
                BukkitTask fireworkEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    createFireworkEffect(finalLocationExplosion, player);
                },0, 20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fireworkEffect.cancel();
                }, 62);
                BukkitTask fireworkEffect3 = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    createFireworkEffect(finalLocationExplosion, player);
                },70, 10);
                BukkitTask flashEffect2 = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.FLASH, startExplosionFlash.add(0,-2,0), 16, 0,0,0,0);
                },102, 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    fireworkEffect3.cancel();
//                    loadingEffect.cancel();
//                    followPlayer.cancel();
//                    dummyText.remove();
                }, 102);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    preExplosionEffect.cancel();
                    flashEffect2.cancel();
                    player.getWorld().createExplosion(finalLocationExplosion, 8F, true, true, player);
                    List<Entity> entities = player.getWorld().getNearbyEntities(finalLocationExplosion, 8,10,8).stream().toList();
                    for(Entity exploded : entities){
                        if(exploded instanceof Damageable){
                            plugin.experienceListener.addExp(player, "magic", 1);
                            ((Damageable) exploded).damage(60.069, player);
                        }
                    }
                }, 125);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerNovaCooldowns.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Nova");
                        playerNovaCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 2400);
            }
        }
    }

}
