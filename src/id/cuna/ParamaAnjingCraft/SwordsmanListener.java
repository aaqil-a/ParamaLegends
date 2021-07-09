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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

public class SwordsmanListener implements Listener{
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    private final HashMap<Player, Integer> playerSwordsmanLevel = new HashMap<Player, Integer>();
    private final HashMap<Player, Integer> playerCrippleAttackCount = new HashMap<Player, Integer>();
    private final HashMap<Player, Integer> calamityLoadingProgress = new HashMap<Player, Integer>();
    private final HashMap<Player, BukkitTask> playerCheckVelocityTasks = new HashMap<Player, BukkitTask>();
    private final List<String> playerShieldCooldown = new ArrayList<String>();
    private final List<String> playerPhoenixCooldown = new ArrayList<String>();
    private final List<String> playerEnrageCooldown = new ArrayList<String>();
    private final List<String> playerOnslaughtCooldown = new ArrayList<String>();
    private final List<String> playerCrueltyCooldown = new ArrayList<String>();
    private final List<String> playerSuperconductedCooldown = new ArrayList<String>();
    private final List<String> playerCalamityCooldown = new ArrayList<String>();
    private final List<Player> playersShielded = new ArrayList<Player>();
    private final List<Player> playersEnraging = new ArrayList<Player>();
    private final List<Player> playersCalamity = new ArrayList<Player>();
    private final List<Entity> entitiesTerrified = new ArrayList<Entity>();
    private final List<Entity> entitiesBlinded = new ArrayList<Entity>();

    public SwordsmanListener(ParamaAnjingCraft plugin) {
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Get player's Sworrdsman level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        playerSwordsmanLevel.put(player, data.getConfig().getInt("players." + player.getUniqueId().toString() + ".swordsmanship"));
        playerCrippleAttackCount.put(player, 0);
    }

    //Remove player from plugin memory on leave
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        playerSwordsmanLevel.remove(player);
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
        if(playerSwordsmanLevel.get(player) < level){
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

    public void spawnCritParticles(Location location){
        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location.add(0,1,0), 1, 0.5, 0.5, 0.5, 0);
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
    }

    //Change player's level in playerMagicLevel hashmap when leveling up
    public void levelUp(Player player){
        int curLevel = playerSwordsmanLevel.get(player);
        playerSwordsmanLevel.replace(player, curLevel+1);
    }

    public void playCritSound(Player player){
        player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1f, 1f);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            Random rand = new Random();
            double damage = event.getDamage();
            switch (item.getType()){
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    //Deal crit damage according to player level
                    int playerLevel = playerSwordsmanLevel.get(attacker);
                    int critRoll = rand.nextInt(100);
                    if(playersEnraging.contains(attacker)){
                        if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                            damage = event.getDamage()*1.4;
                        }
                        if(critRoll < 90){
                            damage = event.getDamage()*2.6;
                            spawnCritParticles(event.getEntity().getLocation());
                            playCritSound(attacker);
                        }
                    } else if(playersCalamity.contains(attacker)){
                        if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                            damage = event.getDamage()*1.4;
                        } else {
                            damage = event.getDamage()*1.7;
                            playCritSound(attacker);
                        }
                        spawnCritParticles(event.getEntity().getLocation());
                    } else if(playerLevel < 4){
                        if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                            return;
                        }
                        if(critRoll < 20){
                            damage = event.getDamage()*1.4;
                            spawnCritParticles(event.getEntity().getLocation());
                            playCritSound(attacker);
                        }
                    } else {
                        if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)){
                            damage = event.getDamage()*1.4;
                        } else if(critRoll < 35){
                            damage = event.getDamage()*1.7;
                            spawnCritParticles(event.getEntity().getLocation());
                            playCritSound(attacker);
                        }
                    }
                    //Check if attack cripples and add to counter
                    if(playerLevel >= 2){
                        int crippleCount = playerCrippleAttackCount.get(attacker);
                        crippleCount++;
                        if(crippleCount >= 5){
                            crippleCount = 0;
                            if(event.getEntity() instanceof LivingEntity){
                                LivingEntity crippled = (LivingEntity) event.getEntity();
                                crippled.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 4, false, false, false));
                                BukkitTask bleed = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                    plugin.experienceListener.addExp(attacker, "swordsmanship", 1);
                                    crippled.damage(1.072);
                                }, 0, 20);
                                Bukkit.getScheduler().runTaskLater(plugin, bleed::cancel, 82);
                            }
                        }
                        playerCrippleAttackCount.put(attacker, crippleCount);
                    }
                }
            }
            if(entitiesTerrified.contains(event.getEntity())){
                damage = plugin.increasedIncomingDamage(damage, 1.5);
            }
            event.setDamage(damage);
        }
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            //Reflect damage if shields up
            if(playersShielded.contains(player)){
                if(event.getDamager() instanceof Damageable){
                    Damageable attacker = (Damageable) event.getDamager();
                    plugin.experienceListener.addExp(player, "swordsmanship", 1);
                    attacker.damage(Math.floor(event.getDamage()*0.15)+0.072);
                }
            }
            if(entitiesTerrified.contains(event.getDamager())){
                Random rand = new Random();
                if(rand.nextInt(100) < 60){
                    event.setCancelled(true);
                }
            }
            if(entitiesBlinded.contains(event.getDamager())){
                event.setCancelled(true);
            }
        }
        if(event.getDamager() instanceof Firework){
            event.setCancelled(true);
        }
    }

    //When player is damaged by anything
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            //Reduce damage if shields up
            if(playersShielded.contains(player)){
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0,1,0), 8, 0.25, 0.25, 0.25, 0, Material.IRON_BLOCK.createBlockData());
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.4f, 0f);
                event.setDamage(event.getDamage()*0.2);
            }
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

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        //Check if held item is book
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (item.getItemMeta() != null)
            switch (item.getItemMeta().getDisplayName()) {
                case "§2Shields Up" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 3) && !isSilenced(player))
                        castShieldsUp(player);
                }
                case "§2Phoenix Dive" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 5) && !isSilenced(player))
                        castPhoenixDive(player);
                }
                case "§2Enrage" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 6) && !isSilenced(player))
                        castEnrage(player);
                }
                case "§2Onslaught" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 7) && !isSilenced(player))
                        castOnslaught(player);
                }
                case "§2Terrifying Cruelty" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 8) && !isSilenced(player))
                        castTerrifyingCruelty(player);
                }
                case "§2Superconducted" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 9) && !isSilenced(player))
                        castSuperconducted(player);
                }
                case "§2Calamity" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 10) && !isSilenced(player))
                        castCalamity(player);
                }
            }


    }


    public void shieldAnimation(Player player){
        ArmorStand sword1 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(-90,0);
            armorStand.setArms(true);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });
        ArmorStand sword2 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(90,0);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });
        ArmorStand sword3 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(180,0);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });
        ArmorStand sword4 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(0,0);
            armorStand.setRightArmPose(new EulerAngle(1.6,-1.6,0));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.SHIELD));
        });

        Location swordLocation1 = player.getLocation().add(0.5,1,-0.8);
        swordLocation1.setDirection(new Vector(1, 0, 0));
        Location swordLocation2 = player.getLocation().add(-0.5,1,0.8);
        swordLocation2.setDirection(new Vector(-1, 0, 0));
        Location swordLocation3 = player.getLocation().add(-0.8,1,-0.5);
        swordLocation3.setDirection(new Vector(0, 0, -1));
        Location swordLocation4 = player.getLocation().add(0.8,1,0.5);
        swordLocation4.setDirection(new Vector(0, 0, 1));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            sword1.teleport(swordLocation1);
            sword2.teleport(swordLocation2);
            sword3.teleport(swordLocation3);
            sword4.teleport(swordLocation4);
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            sword1.teleport(sword1.getLocation().add(0, -0.6, 0));
            sword2.teleport(sword2.getLocation().add(0,-0.6,0));
            sword3.teleport(sword3.getLocation().add(0,-0.6,0));
            sword4.teleport(sword4.getLocation().add(0,-0.6,0));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            swordAnimation.cancel();
            sword1.remove();
            sword2.remove();
            sword3.remove();
            sword4.remove();
        }, 10);

    }

    public void castShieldsUp(Player player){
        if(playerShieldCooldown.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Shields Up");
        } else {
            if(subtractMana(player, 50)){
                player.sendMessage(ChatColor.GREEN+"Shields Up activated.");
                shieldAnimation(player);
                playersShielded.add(player);
//                BukkitTask shieldEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
//                    Entity cloudEntity = player.getLocation().getWorld().spawnEntity(player.getLocation().add(0,1.5,0), EntityType.AREA_EFFECT_CLOUD);
//                    AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
//                    cloud.setParticle(Particle.TOTEM);
//                    cloud.setDuration(1);
//                    cloud.setGravity(false);
//                    //player.getWorld().spawnParticle(Particle.TOTEM, player.getEyeLocation(), 8, 0.5, 0.5, 0);
//                }, 0, 10);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.GREEN+"Shields Up wore off.");
                    playersShielded.remove(player);
                    //shieldEffect.cancel();
                }, 122);
                playerShieldCooldown.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerShieldCooldown.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Shields Up");
                        playerShieldCooldown.remove(player.getUniqueId().toString());
                    }
                }, 400);
            }
        }
    }

    public void castPhoenixDive(Player player){
        if(playerPhoenixCooldown.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Phoenix Dive");
        } else {
            if(subtractMana(player, 100)){
                Vector dive = player.getLocation().getDirection().setY(0).normalize();
                dive.setY(1);
                player.setVelocity(dive);
                player.getWorld().spawnParticle(Particle.LAVA, player.getEyeLocation(), 16, 1, 0.5, 1, 0);
                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 16, 1, 0.5, 1, 0);
                playerCheckVelocityTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if(player.getVelocity().getX() == 0d && player.getVelocity().getZ() == 0d){
                        player.getWorld().spawnParticle(Particle.LAVA, player.getLocation(), 16, 1, 0.5, 1, 0);
                        player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 16, 1, 0.5, 1, 0);
                        List<Entity> entities = player.getNearbyEntities(2.5,2.5,2.5);
                        for(Entity burned : entities){
                            if(burned instanceof Player){
                                continue;
                            }
                            if(burned instanceof Damageable){
                                BukkitTask burnEntity = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                    plugin.experienceListener.addExp(player, "swordsmanship", 1);
                                    burned.getWorld().spawnParticle(Particle.SMALL_FLAME, burned.getLocation().add(0,1,0), 5, 0.5, 0.5, 0.5, 0);
                                    ((Damageable) burned).damage(2.072, player);
                                }, 0, 20);
                                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                    burnEntity.cancel();
                                },  62);
                            }
                        }
                        playerCheckVelocityTasks.get(player).cancel();
                        playerCheckVelocityTasks.remove(player);
                    }
                }, 3, 1));
                playerPhoenixCooldown.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerPhoenixCooldown.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Phoenix Dive");
                        playerPhoenixCooldown.remove(player.getUniqueId().toString());
                    }
                }, 300);
            }
        }
    }

    public void castEnrage(Player player){
        if(playerEnrageCooldown.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Enrage");
        } else {
            if(subtractMana(player, 150)){
                player.sendMessage(ChatColor.GREEN+"Enrage activated.");
                BukkitTask enrageEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation(), 4, 1, 0.5, 1, 0);
                }, 0, 20);
                playersEnraging.add(player);
                plugin.getPlayersSilenced().add(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.GREEN+"Enrage wore off.");
                    playersEnraging.remove(player);
                    plugin.getPlayersSilenced().remove(player);
                    enrageEffect.cancel();
                }, 220);
                playerEnrageCooldown.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerEnrageCooldown.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Enrage");
                        playerEnrageCooldown.remove(player.getUniqueId().toString());
                    }
                }, 900);
            }
        }
    }

    public void swordsAnimation(Player player){
        ArmorStand sword1 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(-90,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });
        ArmorStand sword2 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(90,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });
        ArmorStand sword3 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(180,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });
        ArmorStand sword4 = player.getWorld().spawn(new Location(player.getWorld(), 0, 256, 0), ArmorStand.class, armorStand -> {
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setRotation(0,0);
            armorStand.setRightArmPose(new EulerAngle(-0.15,0,1.6));

            EntityEquipment equipment1 = armorStand.getEquipment();
            equipment1.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
        });

        Location swordLocation1 = player.getLocation().add(0,-0.2,-0.8);
        swordLocation1.setDirection(new Vector(1, 0, 0));
        Location swordLocation2 = player.getLocation().add(0,-0.2,0.8);
        swordLocation2.setDirection(new Vector(-1, 0, 0));
        Location swordLocation3 = player.getLocation().add(-0.8,-0.2,0);
        swordLocation3.setDirection(new Vector(0, 0, -1));
        Location swordLocation4 = player.getLocation().add(0.8,-0.2,0);
        swordLocation4.setDirection(new Vector(0, 0, 1));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            sword1.teleport(swordLocation1);
            sword2.teleport(swordLocation2);
            sword3.teleport(swordLocation3);
            sword4.teleport(swordLocation4);
        }, 1);
        BukkitTask swordAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            sword1.teleport(sword1.getLocation().add(0.5, 0, 0));
            sword2.teleport(sword2.getLocation().add(-0.5,0,0));
            sword3.teleport(sword3.getLocation().add(0,0,-0.5));
            sword4.teleport(sword4.getLocation().add(0,0,0.5));
        }, 0, 1);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            swordAnimation.cancel();
            sword1.remove();
            sword2.remove();
            sword3.remove();
            sword4.remove();
        }, 10);

    }

    public void castOnslaught(Player player){
        if(playerOnslaughtCooldown.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Onslaught");
        } else {
            if(subtractMana(player, 150)){
                swordsAnimation(player);

                List<Entity> entities = player.getNearbyEntities(3.5,3,3.5);
                BukkitTask onslaught = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    for(Entity hit : entities){
                        if(hit instanceof Damageable && !(hit instanceof ArmorStand)){
                            plugin.experienceListener.addExp(player, "swordsmanship", 1);
                            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, hit.getLocation().add(0, 1,0), 1, 0, 0, 0, 0);
                            ((Damageable) hit).damage(12.072, player);
                        }
                    }
                }, 0, 3);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    onslaught.cancel();
                }, 19);
                playerOnslaughtCooldown.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerOnslaughtCooldown.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Onslaught");
                        playerOnslaughtCooldown.remove(player.getUniqueId().toString());
                    }
                }, 480);
            }
        }
    }

    public void castTerrifyingCruelty(Player player){
        if(playerCrueltyCooldown.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Terrifying Cruelty");
        } else {
            if(subtractMana(player, 200)){
                List<Entity> entities = player.getNearbyEntities(3.5,3,3.5);
                player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, player.getLocation().add(0,1,0), 8, 0.5, 0.5, 0.5, 0);
                for(Entity hit : entities){
                    if(hit instanceof Damageable && !(hit instanceof Player) && !(hit instanceof ArmorStand)){
                        BukkitTask hitEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            hit.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, hit.getLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);
                        }, 20, 20);
                        plugin.experienceListener.addExp(player, "swordsmanship", 1);
                        entitiesTerrified.add(hit);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            entitiesTerrified.remove(hit);
                            hitEffect.cancel();
                        }, 120);
                    }
                }
                playerCrueltyCooldown.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCrueltyCooldown.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Terrifying Cruelty");
                        playerCrueltyCooldown.remove(player.getUniqueId().toString());
                    }
                }, 600);
            }
        }
    }

    public void createFireworkEffect(Location location, Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(location.add(new Vector(0,1,0)), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.STAR)
                .flicker(false).trail(false).withColor(Color.WHITE, Color.NAVY, Color.SILVER).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.setSilent(true);
        firework.detonate();
    }

    public void castSuperconducted(Player player){
        if(playerSuperconductedCooldown.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Superconducted");
        } else {
            if(subtractMana(player, 300)){
                List<Entity> entities = player.getNearbyEntities(5,4,5);
                createFireworkEffect(player.getLocation(), player);
                List<Entity> toDamage = new ArrayList<Entity>();
                for(Entity hit : entities){
                    if(hit instanceof LivingEntity && !(hit instanceof Player) && !(hit instanceof ArmorStand)){
                        entitiesBlinded.add(hit);
                        ((LivingEntity) hit).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8, 5, false, false, false));
                        toDamage.add(hit);
                    }
                }
                int delay = 10;
                for(Entity damaged : toDamage){
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if(damaged instanceof Damageable){
                            plugin.experienceListener.addExp(player, "swordsmanship", 1);
                            ((Damageable) damaged).damage(20.072, player);
                            createFireworkEffect(damaged.getLocation(), player);
                        }
                    }, delay);
                    delay += 10;
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for(Entity hit: entities){
                        if(hit instanceof LivingEntity && !(hit instanceof Player)){
                            entitiesBlinded.remove(hit);
                        }
                    }
                }, 160);
                playerSuperconductedCooldown.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerSuperconductedCooldown.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Superconducted");
                        playerSuperconductedCooldown.remove(player.getUniqueId().toString());
                    }
                }, 1200);
            }
        }
    }

    public void castCalamity(Player player){
        if(playerCalamityCooldown.contains(player.getUniqueId().toString())){
            sendCooldownMessage(player, "Calamity");
        } else {
            if(subtractMana(player, 500)){
                playersCalamity.add(player);

//                Location location = player.getEyeLocation();
//                Vector offset = player.getEyeLocation().getDirection().setY(0).normalize().multiply(2.5);
//                location.add(offset);
//                ArmorStand dummyText = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND);;
//                dummyText.setCustomName(ChatColor.GRAY+"||||||||||||");
//                dummyText.setCustomNameVisible(true);
//                dummyText.setVisible(false);
//                dummyText.setGravity(false);
//                dummyText.setInvulnerable(true);
//                Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                    dummyText.teleport(location.add(new Vector(0,-2,0)));
//                }, 1);
//                calamityLoadingProgress.put(player,0);
//                BukkitTask loadingEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
//                    StringBuilder dummyTextName = new StringBuilder(ChatColor.GREEN + "");
//                    int ballProgress = calamityLoadingProgress.get(player);
//                    int ballToLoad = 12-ballProgress;
//                    dummyTextName.append("|".repeat(ballProgress));
//                    dummyTextName.append(ChatColor.GRAY);
//                    if(ballToLoad>0){
//                        dummyTextName.append("|".repeat(ballToLoad));
//                    }
//                    dummyText.setCustomName(dummyTextName.toString());
//                    calamityLoadingProgress.put(player, ballProgress+2);
//                },2, 10);
//                BukkitTask followPlayer = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
//                    Location newLocation = player.getEyeLocation();
//                    Vector newOffset = player.getEyeLocation().getDirection().multiply(2.5);
//                    newLocation.add(newOffset);
//
//                    dummyText.teleport(newLocation.add(new Vector(0,-2,0)));
//                },2, 1);
//                Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                    loadingEffect.cancel();
//                    followPlayer.cancel();
//                    dummyText.remove();
//                }, 62);

                BukkitTask calamity = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    List<Entity> entities = player.getNearbyEntities(10,10,10);
                    List<Entity> toDamage = new ArrayList<Entity>();
                    for(Entity hit : entities){
                        if(hit instanceof LivingEntity && !(hit instanceof Player) && !(hit instanceof ArmorStand)) {
                            toDamage.add(hit);
                        }
                    }
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getEyeLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);

                    Random rand = new Random();
                    if(toDamage.size() > 0){
                        Entity striked = toDamage.get(rand.nextInt(toDamage.size()));
                        striked.getWorld().strikeLightningEffect(striked.getLocation());
                        striked.getWorld().spawnParticle(Particle.FLASH, striked.getLocation().add(new Vector(0,1,0)), 5);
                        ((Damageable) striked).damage(30.072, player);
                        plugin.experienceListener.addExp(player, "swordsmanship", 1);
                    }
                }, 62, 20);
                BukkitTask calamityEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation().add(0,1,0), 1, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getEyeLocation().add(0,1.5,0), 1, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getEyeLocation().add(0,1.5,0), 1, 0.5, 0.5, 0.5, 0);
                }, 0, 5);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    calamity.cancel();
                    playersCalamity.remove(player);
                    calamityEffect.cancel();
                }, 302);
                playerCalamityCooldown.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCalamityCooldown.contains(player.getUniqueId().toString())){
                        sendNoLongerCooldownMessage(player, "Calamity");
                        playerCalamityCooldown.remove(player.getUniqueId().toString());
                    }
                }, 2400);
            }
        }
    }

}

