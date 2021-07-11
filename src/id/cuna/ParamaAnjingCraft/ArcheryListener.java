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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
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
    private final HashMap<Player, List<Entity>> entitiesWhistlingWind = new HashMap<>();
    private final HashMap<Player, Entity> targetWhistlingWind = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerHuayraEffectTask = new HashMap<Player, BukkitTask>();
    private final HashMap<Player, BukkitTask> playerHuayraShootTask = new HashMap<Player, BukkitTask>();
    private final List<Entity> entitiesHunterEye = new ArrayList<>();
    private final List<Entity> entitiesViperBite = new ArrayList<>();
    private final List<String> playerTotsuka = new ArrayList<>();
    private final List<String> playerTotsukaCooldowns = new ArrayList<>();
    private final List<String> playerWindBoostCooldowns = new ArrayList<>();
    private final List<String> playerSoulStringCooldowns = new ArrayList<>();
    private final List<String> playerRoyalArtilleryCooldowns = new ArrayList<>();
    private final List<String> playerHuayraFuryCooldowns = new ArrayList<>();
    private final List<String> playersWindBoosted = new ArrayList<>();
    private final List<String> playersRetreatBoosted = new ArrayList<>();
    private final List<String> playersHuayra = new ArrayList<>();
    private final List<ArmorStand> soulstringAiming = new ArrayList<>();


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
    public boolean checkLevel(Player player, int level, boolean silent){
        if(playerArcheryLevel.get(player) < level){
            if(!silent) player.sendMessage(ChatColor.GRAY + "You do not understand how to use this ability yet.");
            return false;
        } else {
            return true;
        }
    }
    public boolean checkLevel(Player player, int level){
        return checkLevel(player, level, false);
    }

    //Cooldown Handler
    public void sendCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_GREEN + spell + ChatColor.GRAY + " is on cooldown.");
    }
    public void sendNoLongerCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_GREEN + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }

    public void applySpeedPassive(Player player){
        double movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed*1.2);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        double damage = event.getDamage();

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
        if(event.getDamager() instanceof AbstractArrow) {
            Projectile projectile = (Projectile) event.getDamager();
            if(projectile.getShooter() instanceof Player){
                Player player = (Player) projectile.getShooter();
                if(event.getEntity().getLocation().distance(player.getLocation()) > 10){
                    damage *= 1.2;
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1f, 1f);
                }
                if(playersWindBoosted.contains(player.getUniqueId().toString())){
                    projectile.getWorld().spawnParticle(Particle.SWEEP_ATTACK, event.getEntity().getLocation().add(0,1,0),4, 0.5, 0.5, 0.5, 0);
                }
                if(checkLevel(player, 7, true)){
                    Random rand = new Random();
                    if(rand.nextInt(99) < 10){
                        damage*=1.2;
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
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
                case "§aSoulstring" -> {
                    if (!isSilenced(player))
                        if(checkLevel(player, 6))
                            castSoulstring(player);
                }
                case "§aRoyal Artillery" -> {
                    if (!isSilenced(player))
                        if(checkLevel(player, 9))
                            castRoyalArtillery(player);
                }
                case "§aHuayra's Fury"-> {
                    if (!isSilenced(player))
                        if(checkLevel(player, 7))
                            castHuayraFury(player);
                }
            }
        }

    }

    //Deal when player places cobweb
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.getItemInHand().hasItemMeta()){
            ItemStack placed = event.getItemInHand();
            switch(placed.getItemMeta().getDisplayName()){
                case "§aTotsuka's Creation", "§aSoulstring", "§aHuayra's Fury" -> event.setCancelled(true);
            }
        }
    }

    //Deal when projectile hits block
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();

        if (projectile instanceof Arrow && (projectile.getCustomName() != null)){
            Arrow arrow = (Arrow) projectile;
            switch(arrow.getCustomName()){
                case "blast" -> {
                    explodeArrow(arrow);
                    event.setCancelled(true);
                    arrow.remove();
                }
                case "barrage" -> {
                    event.setCancelled(true);
                }
            }
        } else if(projectile instanceof SpectralArrow && projectile.getCustomName() != null){
            SpectralArrow arrow = (SpectralArrow) projectile;
            if ("whistlingwind".equals(arrow.getCustomName())) {
                event.setCancelled(true);
                Player player = (Player) arrow.getShooter();
                arrow.remove();
                if(entitiesWhistlingWind.containsKey(player)){
                    Entity target = targetWhistlingWind.get(player);
                    if (target instanceof LivingEntity)((LivingEntity) target).damage(30.016, player);
                    target.getWorld().spawnParticle(Particle.FLASH, target.getLocation(), 1, 0, 0, 0 ,0);
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        directArrowToEntity(target, player);
                    }, 10);
                }
            } else if ("return".equals(arrow.getCustomName())) {
                event.setCancelled(true);
                Player player = (Player) arrow.getShooter();
                if(event.getHitEntity()!=null){
                    if (event.getHitEntity() instanceof LivingEntity){
                        if(event.getHitEntity().equals(player)){
                            arrow.remove();
                        } else {
                            ((LivingEntity) event.getHitEntity()).damage(30.016, player);
                        }
                    }

                }
                player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 1, 0, 0, 0 ,0);

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

    public void givePlayerWhistlingWind(Player player, ItemStack item){
        if(player.getEquipment().getItemInOffHand().equals(item)){
            player.getEquipment().setItemInOffHand(item);

        } else {
            player.getInventory().setItem(player.getInventory().first(item), item);
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
                            castHuntersEye(player, event.getProjectile(), false);
                        }
                    }
                    case "§aViper's Bite" -> {
                        if(!isSilenced(player)){
                            if(checkLevel(player, 2)){
                                castViperBite(player, event.getProjectile(), false);
                            }
                        }
                    }
                    case "§aNeurotoxin" -> {
                        if(!isSilenced(player)){
                            if(checkLevel(player, 5)){
                                castNeurotoxin(player, event.getProjectile(), false);
                            }
                        }
                    }
                    case "§aRetreat" -> {
                        if(!isSilenced(player)){
                            if(checkLevel(player, 7)){
                                castRetreat(player, event.getProjectile(), false);
                            }
                        }
                    }
                    case "§aBlast" -> {
                        if(!isSilenced(player)){
                            if(checkLevel(player, 8)){
                                castBlast(player, event.getProjectile(), false);
                            }
                        }
                    }
                    case "§aWhistling Wind" -> {
                        if(event.getProjectile() instanceof SpectralArrow){
                            ((SpectralArrow) event.getProjectile()).setGlowingTicks(0);
                        }
                        if(!isSilenced(player)){
                            if(checkLevel(player, 10)){
                                if(entitiesWhistlingWind.containsKey(player)){
                                    ((SpectralArrow) event.getProjectile()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                                    player.sendMessage(ChatColor.GRAY+"A whistling wind is already in use.");
                                    givePlayerWhistlingWind(player, event.getConsumable());
                                } else {
                                    castWhistlingWind(player, event, false);
                                }
                            } else {
                                ((SpectralArrow) event.getProjectile()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                                givePlayerWhistlingWind(player, event.getConsumable());
                            }
                        } else {
                            ((SpectralArrow) event.getProjectile()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                            givePlayerWhistlingWind(player, event.getConsumable());
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

    public void findAir(Location location){
         if(location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
             location.add(0,1,0);
        } else if(location.getBlock().getRelative(BlockFace.DOWN).getType().isAir()){
            location.add(0,-1,0);
        } else if(location.getBlock().getRelative(BlockFace.NORTH).getType().isAir()){
            location.add(0,0,-1);
        } else if(location.getBlock().getRelative(BlockFace.SOUTH).getType().isAir()){
            location.add(0,0,1);
        } else if(location.getBlock().getRelative(BlockFace.WEST).getType().isAir()){
             location.add(-1,0,0);
        } else if(location.getBlock().getRelative(BlockFace.EAST).getType().isAir()){
           location.add(1,0,0);
        }
    }

    public void summonSoulstring(Player player){
        Location location = player.getEyeLocation();
        Vector offset = player.getEyeLocation().getDirection().multiply(2.5);
        location.add(offset);
        if(!(location.getBlock().isEmpty() || location.getBlock().isLiquid())){
            findAir(location);
        }

        ItemStack shirt = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta shirtMeta = (LeatherArmorMeta) shirt.getItemMeta();
        shirtMeta.setColor(Color.WHITE);
        shirt.setItemMeta(shirtMeta);
        ArmorStand dummy = player.getWorld().spawn(new Location(player.getWorld(), 0,256,0), ArmorStand.class, armorStand -> {
            armorStand.setCustomName(player.getName()+"'s Soulstring");
            armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
            armorStand.getEquipment().setChestplate(shirt);
            armorStand.setArms(true);
            armorStand.setRightArmPose(new EulerAngle(-1.6, 0, -0.2));
            armorStand.setSilent(true);
            armorStand.setCanPickupItems(false);
            armorStand.setCollidable(false);
            armorStand.setGravity(false);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setCustomName("soulstring shooter");

        });
        ArmorStand dummyText = player.getWorld().spawn(new Location(player.getWorld(), 0,256,0), ArmorStand.class, armorStand-> {
            armorStand.setCustomName(ChatColor.GREEN + player.getName()+"'s Soulstring");
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setCollidable(false);
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setInvulnerable(true);
        });


//        Vector dummyDirection = location.getDirection();
//        dummyDirection = dummyDirection.setY(0).normalize();
//        float yaw = location.getYaw();
//
//        //move dummy a bit to its right
//        dummyDirection.setX(-1*Math.sin(Math.PI*(yaw+90)/180));
//        dummyDirection.setZ(Math.cos(Math.PI*(yaw+90)/180));
//        Location tempLocation = location.clone().add(dummyDirection.multiply(-0.252).setY(-1.6));
//
//        //move dummy a bit backwards
//        dummyDirection = location.getDirection();
//        dummyDirection.setX(-1*Math.sin(Math.PI*(yaw+180)/180));
//        dummyDirection.setZ(Math.cos(Math.PI*(yaw+180)/180));
//        tempLocation.add(dummyDirection.multiply(-0.55).setY(0));
        BukkitTask soulStringEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, location.clone().add(0, 1, 0), 8, 0.5, 0.5, 0.5, 0);
        }, 0, 100);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            dummy.teleport(location.clone().add(0, -0.1, 0));
            dummyText.teleport(location);
        }, 2);
        BukkitTask shoot = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(!soulstringAiming.contains(dummy)){
                shootSoulstring(player, dummy);
            }
        }, 40, 20);
        Bukkit.getScheduler().runTaskLater(plugin, shoot::cancel, 385);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            dummy.remove();
            dummyText.remove();
            soulStringEffect.cancel();
            soulstringAiming.remove(dummy);
        }, 405);
    }

    public void shootSoulstring(Player player, ArmorStand dummy){
        List<Entity> entities = player.getNearbyEntities(10,10,10).stream().toList();
        Random rand = new Random();
        List<Entity> newEntities = new ArrayList<>();
        for(Entity hit: entities){
            if(!(hit instanceof LivingEntity) || hit instanceof Villager || hit instanceof Player || hit instanceof Silverfish
                    || hit instanceof ArmorStand || hit instanceof Phantom)
                continue;
            newEntities.add(hit);
        }
        if(newEntities.size() > 0){
            int toHit = rand.nextInt(newEntities.size());
            Entity entityToHit = newEntities.get(toHit);
            if(entityToHit != null){
                aimSoulstring(player, dummy, entityToHit);
            }
        }
    }

    public void aimSoulstring(Player player, ArmorStand dummy, Entity entity){
        double dummyX = dummy.getLocation().getX();
        double dummyY = dummy.getLocation().getY();
        double dummyZ = dummy.getLocation().getZ();
        soulstringAiming.add(dummy);
        BukkitTask followEntity = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            double entityX = entity.getLocation().getX();
            double entityY = entity.getLocation().getY();
            double entityZ = entity.getLocation().getZ();
            Vector direction = new Vector(entityX-dummyX, entityY-dummyY-0.5, entityZ-dummyZ);
            dummy.teleport(dummy.getLocation().setDirection(direction.normalize()));
        }, 0, 2);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            followEntity.cancel();
            Arrow arrow = dummy.launchProjectile(Arrow.class, dummy.getLocation().getDirection());
            arrow.setShooter(player);
            arrow.setGravity(false);
            arrow.setVelocity(arrow.getVelocity().multiply(1.5));
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                arrow.remove();
            }, 20);
            soulstringAiming.remove(dummy);
        }, 30);

    }

    public void explodeArrow(Arrow arrow){
        if(arrow.getShooter() instanceof Player){
            List<Entity> entities = arrow.getNearbyEntities(2,2,2);
            for(Entity hit : entities){
                if(hit instanceof LivingEntity && !(hit instanceof Player)){
                    ((LivingEntity) hit).damage(8.016, (Player) arrow.getShooter());
                }
            }
            arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 1.5f);
            arrow.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, arrow.getLocation(), 8, 0.5, 0.5, 0.5, 0);
        }
    }

    public void castHuntersEye(Player player, Entity entity, boolean noMana){
        int manaCost = 20;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(subtractMana(player, manaCost)){
                arrow.setCustomName("huntereye");
            }
        }
    }

    public void castViperBite(Player player, Entity entity, boolean noMana){
        int manaCost = 15;
        if(noMana){
            manaCost = 0;
        }
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

    public void castNeurotoxin(Player player, Entity entity, boolean noMana){
        int manaCost = 50;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(subtractMana(player, manaCost)){
                arrow.setCustomName("neurotoxin");
            }
        }
    }

    public void castSoulstring(Player player){
        if(playerSoulStringCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Soulstring");
        } else if (subtractMana(player, 150)) {
            summonSoulstring(player);
            playerSoulStringCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerSoulStringCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Soulstring");
                    playerSoulStringCooldowns.remove(player.getUniqueId().toString());
                }
            }, 1200);
        }
    }

    public void castRetreat(Player player, Entity entity, boolean noMana){
        int manaCost = 70;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            if(subtractMana(player, manaCost)){
                if(!playersRetreatBoosted.contains(player.getUniqueId().toString())) {
                    double oldSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();;
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        if(!playersRetreatBoosted.contains(player.getUniqueId().toString())){
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(oldSpeed*1.1);
                            playersRetreatBoosted.add(player.getUniqueId().toString());
                        }
                    }, 5);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(oldSpeed);
                        playersRetreatBoosted.remove(player.getUniqueId().toString());
                    }, 65);
                }

                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    Vector arrowVelocity = player.getLocation().getDirection();
                    Arrow arrow2 = player.launchProjectile(Arrow.class, arrowVelocity);
                    arrow2.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                }, 5);
                Vector velocity = player.getLocation().getDirection().setY(0).normalize();
                player.setVelocity(velocity.multiply(-0.8).setY(0.3));
            }
        }
    }

    public void castBlast(Player player, Entity entity, boolean noMana){
        int manaCost = 60;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            Arrow arrow = (Arrow) entity;
            if(subtractMana(player, manaCost)){
                arrow.setCustomName("blast");
            }
        }
    }

    public void barrageArrows(Location location, Player player){

        double[] arrowMapX = {-1,1,-2,2,0};
        double[] arrowMapZ = {-1,-2,0,0,2};
        double[] arrowMapX2 = {0,-2,2,-1,1};
        double[] arrowMapZ2 = {-2,-1,-1,0,2};
        double[] arrowMapX3 = {0,-1,2,-2,2};
        double[] arrowMapZ3 = {0,-2,-2,2,2};

        BukkitTask arrowTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(int i = 0; i < 5; i++){
                Location arrowLocation = location.clone().add(arrowMapX[i], 6, arrowMapZ[i]);
                location.getWorld().spawn(arrowLocation, Arrow.class, arrow -> {
                    arrow.setCustomName("barrage");
                    arrow.setVelocity(new Vector(0, -0.7, 0));
                });
            }
        }, 0, 30);
        BukkitTask arrowTask2 = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(int i = 0; i < 5; i++){
                Location arrowLocation = location.clone().add(arrowMapX2[i], 6, arrowMapZ2[i]);
                location.getWorld().spawn(arrowLocation, Arrow.class, arrow -> {
                    arrow.setCustomName("barrage");
                    arrow.setVelocity(new Vector(0, -0.7, 0));
                    arrow.setSilent(true);
                });
            }
        }, 10, 30);
        BukkitTask arrowTask3 = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(int i = 0; i < 5; i++){
                Location arrowLocation = location.clone().add(arrowMapX3[i], 6, arrowMapZ3[i]);
                location.getWorld().spawn(arrowLocation, Arrow.class, arrow -> {
                    arrow.setCustomName("barrage");
                    arrow.setVelocity(new Vector(0, -0.7, 0));
                    arrow.setSilent(true);
                });
            }
        }, 20, 30);

        BukkitTask damageTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            location.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, location, 4, 0,0,0,0);
            List<Entity> entities = location.getWorld().getNearbyEntities(location, 2.5, 5, 2.5).stream().toList();
            for(Entity hit : entities){
                if(hit instanceof LivingEntity && !(hit instanceof Player)){
                    ((LivingEntity) hit).damage(6.016, player);
                }
            }
        }, 5, 10);
        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            arrowTask.cancel();
            arrowTask2.cancel();
            arrowTask3.cancel();
            damageTask.cancel();
        }, 160);
    }

    public void castRoyalArtillery(Player player){
        if(playerRoyalArtilleryCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Royal Artillery");
        } else {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 30, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                plugin.magicListener.sendOutOfRangeMessage(player);
                return;
            }
            if (subtractMana(player, 0)) {//150
                player.getWorld().spawn(location.clone().add(0,1,0), Firework.class, firework -> {
                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BURST)
                            .flicker(false).trail(false).withColor(Color.AQUA, Color.RED).build());
                    meta.setPower(0);
                    firework.setFireworkMeta(meta);
                    firework.setSilent(true);
                    firework.detonate();
                });

                Location finalLocation = location.add(0,1.5,0);
                Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                    barrageArrows(finalLocation, player);
                }, 40);

                playerRoyalArtilleryCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerRoyalArtilleryCooldowns.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Royal Artillery");
                        playerRoyalArtilleryCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 1200);
            }
        }
    }


    public void castHuayraFury(Player player){
        if (playersHuayra.contains(player.getUniqueId().toString())){
            player.sendMessage(ChatColor.GREEN+"Huayra's Fury deactivated.");
            playerHuayraShootTask.get(player).cancel();
            playerHuayraEffectTask.get(player).cancel();
            playerHuayraShootTask.remove(player);
            playerHuayraEffectTask.remove(player);
            playersHuayra.remove(player.getUniqueId().toString());
        } else if(playerHuayraFuryCooldowns.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Huayra's Fury");
        } else if(subtractMana(player, 200)){
            player.sendMessage(ChatColor.GREEN+"Huayra's Fury activated.");
            playerHuayraShootTask.put(player, Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                ItemStack[] inventory = player.getInventory().getStorageContents();
                ItemStack shotArrow = null;
                int shotArrowSlot = -1;

                if(player.getInventory().getItemInOffHand() != null
                        && (player.getInventory().getItemInOffHand().getType().equals(Material.ARROW)
                        || player.getInventory().getItemInOffHand().getType().equals(Material.TIPPED_ARROW))) {
                    shotArrow = player.getInventory().getItemInOffHand();
                    shotArrowSlot = -2;
                } else {
                    for(ItemStack item : inventory){
                        if(item == null){
                            continue;
                        }
                        if(item.getType().equals(Material.ARROW) || item.getType().equals(Material.TIPPED_ARROW)){
                            shotArrow = item;
                            break;
                        }
                    }
                    if(shotArrow!=null){
                        shotArrowSlot = player.getInventory().first(shotArrow);
                    }
                }
                if(shotArrow != null){
                    Arrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection().multiply(2));
                    if(shotArrowSlot == -2){
                        player.getInventory().getItemInOffHand().setAmount(shotArrow.getAmount()-1);
                    } else {
                        player.getInventory().getItem(shotArrowSlot).setAmount(shotArrow.getAmount()-1);
                    }
                    if(shotArrow.hasItemMeta()){
                        ItemMeta meta = shotArrow.getItemMeta();
                        if(meta.hasDisplayName()){
                            switch(meta.getDisplayName()){
                                case "§aHunter's Eye" -> {
                                    if(!isSilenced(player)){
                                        castHuntersEye(player, arrow, true);
                                    }
                                }
                                case "§aViper's Bite" -> {
                                    if(!isSilenced(player)){
                                        if(checkLevel(player, 2)){
                                            castViperBite(player, arrow, true);
                                        }
                                    }
                                }
                                case "§aNeurotoxin" -> {
                                    if(!isSilenced(player)){
                                        if(checkLevel(player, 5)){
                                            castNeurotoxin(player, arrow, true);
                                        }
                                    }
                                }
                                case "§aRetreat" -> {
                                    if(!isSilenced(player)){
                                        if(checkLevel(player, 7)){
                                            castRetreat(player, arrow, true);
                                        }
                                    }
                                }
                                case "§aBlast" -> {
                                    if(!isSilenced(player)){
                                        if(checkLevel(player, 8)){
                                            castBlast(player, arrow, true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }, 5, 5));
            playersHuayra.add(player.getUniqueId().toString());
            playerHuayraEffectTask.put(player, Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation(), 4, 1, 0.5, 1, 0);
                player.damage(1);
            }, 0, 20));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerHuayraShootTask.containsKey(player)){
                    playerHuayraShootTask.get(player).cancel();
                    playerHuayraEffectTask.get(player).cancel();
                    playerHuayraShootTask.remove(player);
                    playerHuayraEffectTask.remove(player);
                    player.sendMessage(ChatColor.GREEN+"Huayra's Fury wore off.");
                    playersHuayra.remove(player.getUniqueId().toString());
                }
            }, 104);
            playerHuayraFuryCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerHuayraFuryCooldowns.contains(player.getUniqueId().toString())){
                    sendNoLongerCooldownMessage(player, "Huayra's Fury");
                    playerHuayraFuryCooldowns.remove(player.getUniqueId().toString());
                }
            }, 1800);
        }
    }

    public void shootToEntity(Entity entity, Entity source, Player player, boolean last){
        Location hitLocation = entity.getLocation().add(0,-0.5,0);
        Entity hit;
        if(!last){
            hit = targetWhistlingWind.get(player);
        } else {
            hit = player;
        }
        Location arrowLocation = hit.getLocation().add(0,-1,0);
        Vector direction = new Vector(hitLocation.getX()-arrowLocation.getX(), hitLocation.getY()-arrowLocation.getY(),
                hitLocation.getZ()-arrowLocation.getZ());
        arrowLocation.setDirection(direction.normalize());
        ArmorStand dummy = source.getWorld().spawn(arrowLocation, ArmorStand.class, armorStand -> {
                armorStand.setSilent(true);
                armorStand.setCollidable(false);
                armorStand.setGravity(false);
                armorStand.setInvisible(true);
                armorStand.setInvulnerable(true);
                armorStand.setCanPickupItems(false);
                armorStand.setCustomName("whistlingwindshooter");
        });
        source.getWorld().playSound(source.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.2f);
        if(!last) targetWhistlingWind.put(player, entity);

        SpectralArrow newArrow = dummy.launchProjectile(SpectralArrow.class, direction);
        newArrow.setCustomName("whistlingwind");
        newArrow.setShooter(player);
        newArrow.setGravity(false);
        newArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        newArrow.setSilent(true);
        newArrow.setVelocity(newArrow.getVelocity().multiply(2));
        dummy.remove();
        if(arrowLocation.distance(hitLocation) < 1.5){
            newArrow.remove();
            if(!last){
                if(entitiesWhistlingWind.containsKey(player)){
                    Entity target = targetWhistlingWind.get(player);
                    if(target.equals(entity)){
                        if (target instanceof LivingEntity)((LivingEntity) target).damage(20.016, player);
                        target.getWorld().spawnParticle(Particle.FLASH, target.getLocation(), 1, 0, 0, 0 ,0);
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 2f);
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                            directArrowToEntity(target, player);
                        }, 10);
                    }
                }
            }
        }  else {
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                newArrow.remove();
                if(!last){
                    if(entitiesWhistlingWind.containsKey(player)){
                        Entity target = targetWhistlingWind.get(player);
                        if(target.equals(entity)){
                            if (target instanceof LivingEntity)((LivingEntity) target).damage(20.016, player);
                            target.getWorld().spawnParticle(Particle.FLASH, target.getLocation(), 1, 0, 0, 0 ,0);
                            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 2f);
                            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                directArrowToEntity(target, player);
                            }, 10);
                        }
                    }
                }
            }, 20);

        }


    }

    public void directArrowToEntity(Entity entity, Player player){
        List<Entity> entities = entitiesWhistlingWind.get(player);
        if(entities != null){
            if(entities.size()==0){
                return;
            }
            Entity hit = entities.get(0);
            entities.remove(hit);
            shootToEntity(hit, entity, player, false);
            if(entities.size() == 0){
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    //fire arrow back to player
                    Location hitLocation = player.getLocation().add(0,-0.5,0);
                    Location arrowLocation = hit.getLocation().add(0,-1,0);
                    Vector direction = new Vector(hitLocation.getX()-arrowLocation.getX(), hitLocation.getY()-arrowLocation.getY(),
                            hitLocation.getZ()-arrowLocation.getZ());
                    arrowLocation.setDirection(direction.normalize());
                    ArmorStand dummy = player.getWorld().spawn(arrowLocation, ArmorStand.class, armorStand -> {
                        armorStand.setSilent(true);
                        armorStand.setCollidable(false);
                        armorStand.setGravity(false);
                        armorStand.setInvisible(true);
                        armorStand.setInvulnerable(true);
                        armorStand.setCanPickupItems(false);
                        armorStand.setCustomName("whistlingwindshooter");
                    });
                    SpectralArrow newArrow = dummy.launchProjectile(SpectralArrow.class, direction);
                    newArrow.setCustomName("return");
                    newArrow.setShooter(player);
                    newArrow.setGravity(false);
                    newArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    newArrow.setSilent(true);
                    newArrow.setVelocity(newArrow.getVelocity().multiply(2));
                    dummy.remove();
                    if(arrowLocation.distance(hitLocation) < 4){
                        newArrow.remove();
                    } else {
                        Bukkit.getScheduler().runTaskLater(plugin, newArrow::remove,20);
                    }
                },10);
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    entitiesWhistlingWind.remove(player);
                },8);
            } else {
                entitiesWhistlingWind.replace(player, entities);
            }
        }
    }

    public void castWhistlingWind(Player player, EntityShootBowEvent event, boolean noMana){
        int manaCost = 200;
        if(noMana){
            manaCost = 0;
        }
        if(event.getProjectile() instanceof SpectralArrow){
            SpectralArrow arrow = (SpectralArrow) event.getProjectile();
            if(subtractMana(player, manaCost)){
                arrow.setCustomName("whistlingwind");
                arrow.setGravity(false);

                Location location = player.getEyeLocation();
                Vector offset = player.getEyeLocation().getDirection().multiply(2);
                location.add(offset);
                if(!(location.getBlock().isEmpty() || location.getBlock().isLiquid())){
                    findAir(location);
                }

                arrow.remove();
                List <Entity> entities = player.getNearbyEntities(10,10,10);
                entities.removeIf(hit -> !(hit instanceof LivingEntity) || hit instanceof Player || hit instanceof ArmorStand || hit instanceof Villager);
                entitiesWhistlingWind.put(player, entities);
                if(entities.size() == 0){
                    entitiesWhistlingWind.remove(player);
                }
                targetWhistlingWind.put(player, player);
                directArrowToEntity(player, player);
            } else {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                givePlayerWhistlingWind(player, event.getConsumable());
            }

        }
    }
}

