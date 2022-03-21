package me.cuna.paramalegends.boss.fight;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.boss.BossManager;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
;
import java.util.*;

public class BloodMoonListener implements Listener{

    private ParamaLegends plugin;
    private DataManager data;
    public BukkitTask spawnMobs = null;

    public BloodMoonListener(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.dataManager;
    }

    public List<LivingEntity> entities = new ArrayList<>();
    private BossBar bloodMoonBossBar;
    private int entitiesSlain = 0;
    private final HashMap<Player, Integer> damageDealt = new HashMap<>();
    private final HashMap<Player, Integer> deaths = new HashMap<>();
    private final HashMap<Player, Integer> kills = new HashMap<>();
    private final HashMap<Player, Integer> damageTaken = new HashMap<>();
    private Random rand = new Random();
    private boolean crimeraSpawned = false;


    public void bossFight(World world){
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        plugin.bossManager.bloodMoonSummon.setBloodMoonOccuring(true);
        Bukkit.broadcastMessage(ChatColor.DARK_RED+"The moon smolders crimson in everlasting torment.");
        createBloodMoonBossBar();
        entitiesSlain = 0;
        damageDealt.clear();
        deaths.clear();
        kills.clear();
        damageTaken.clear();
        entities.clear();
        crimeraSpawned = false;
        spawnMobs = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            if(world.getTime() > 23000 || world.getTime()<13000){
                //time expired
                endFight(false);
            } else {
                spawnSquadron(world);
            }
        }, 100, 800);
    }

    public void spawnSquadron(World world){
        for (Player player : world.getPlayers()){
            if(entities.size() < 150){
                Location spawnLocation = randomLocation(player);
                //Create spawning animation
                world.playSound(spawnLocation, Sound.BLOCK_PORTAL_AMBIENT, 2f, 1f);
                BukkitTask spawnAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    world.spawnParticle(Particle.FLAME, spawnLocation, 4, 0.5, 0.5, 0.5, 0);
                    world.spawnParticle(Particle.SMOKE_NORMAL, spawnLocation, 4, 0.5, 0.5, 0.5, 0);
                }, 0, 20);

                //Spawn squadron
                Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                    if (plugin.bossManager.bloodMoonSummon.isBloodMoonOccuring()) {
                        //Determine whether to spawn ghast or not
                        if ((rand.nextInt(4) == 1) || (player.getLocation().getY() < world.getHighestBlockYAt(player.getLocation())))
                            entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0, 10, 0), Ghast.class, ghast -> {
                                ghast.setTarget(player);
                                ghast.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                                        .setBaseValue(40);
                                ghast.setHealth(40);
                                ghast.setCustomName(ChatColor.COLOR_CHAR + "4Blood Moon Ghast");
                            }));

                        spawnLocation.add(-1, 0, -1);
                        final String[] spawnMap = new String[]{"ZPZ", "SPH"};
                        for (int x = 0; x < 2; x++) {
                            for (int z = 0; z < 3; z++) {
                                switch (spawnMap[x].charAt(z)) {
                                    case 'Z' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0, 1, 0), Zombie.class, zombie -> {
                                        zombie.setTarget(player);
                                        zombie.setCustomName(ChatColor.COLOR_CHAR + "4Blood Moon Zombie");
                                        Objects.requireNonNull(zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(0.3);
                                    }));
                                    case 'P' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0, 1, 0), PigZombie.class, pigZombie -> {
                                        pigZombie.setTarget(player);
                                        pigZombie.setAngry(true);
                                        pigZombie.setAnger(9999999);
                                        pigZombie.setCustomName(ChatColor.COLOR_CHAR + "4Blood Moon Pigman");
                                        pigZombie.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                                                .setBaseValue(50);
                                        pigZombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                                                .setBaseValue(16);
                                        pigZombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                                                .setBaseValue(0.3);
                                        pigZombie.setHealth(50);
                                    }));
                                    case 'S' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0, 1, 0), Spider.class, spider -> {
                                        spider.setTarget(player);
                                        spider.setCustomName(ChatColor.COLOR_CHAR + "4Blood Moon Spider");
                                        spider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                                                .setBaseValue(0.4);
                                    }));
                                    case 'H' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0, 1, 0), Zoglin.class, zoglin -> {
                                        zoglin.setTarget(player);
                                        zoglin.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                                                .setBaseValue(32);
                                        zoglin.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                                                .setBaseValue(100);
                                        zoglin.setHealth(100);
                                        zoglin.setCustomName(ChatColor.COLOR_CHAR + "4Blood Moon Zoglin");
                                        zoglin.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 0));
                                    }));
                                }
                                spawnLocation.add(0, 0, 1);
                            }
                            spawnLocation.add(1, 0, 0);
                        }
                        spawnAnimation.cancel();
                    }
                }, 100);
            }
        }
    }

    //Get random location to spawn mob
    public Location randomLocation(Player player){
        Random rand = new Random();
        int offsetX = 10 - rand.nextInt(20);
        int offsetZ = 10 - rand.nextInt(20);
        if(offsetX > 0) offsetX += 10;
        else offsetX -= 10;
        if(offsetZ > 0) offsetZ += 10;
        else offsetZ -= 10;
        int spawnX = (int) player.getLocation().getX() + offsetX;
        int spawnZ = (int) player.getLocation().getZ() + offsetZ;
        return new Location(player.getWorld(), spawnX, player.getWorld().getHighestBlockYAt(spawnX, spawnZ), spawnZ).add(0,1,0);
    }

    public void createBloodMoonBossBar(){
        bloodMoonBossBar = Bukkit.createBossBar(ChatColor.DARK_RED+"Blood Moon", BarColor.RED, BarStyle.SOLID, BarFlag.CREATE_FOG);
        for(Player player : Bukkit.getOnlinePlayers()) bloodMoonBossBar.addPlayer(player);
        bloodMoonBossBar.setVisible(true);
        bloodMoonBossBar.setProgress(1);
    }

    public void endFight(boolean victory){
        Objects.requireNonNull(plugin.getServer().getWorld("world")).setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
        if(spawnMobs != null)spawnMobs.cancel();
        spawnMobs = null;
        if(bloodMoonBossBar != null){
            bloodMoonBossBar.removeAll();
            bloodMoonBossBar.setVisible(false);
        }
        plugin.bossManager.bloodMoonSummon.setBloodMoonOccuring(false);
        //Despawn current entities
        for(LivingEntity entity : entities){
            entity.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, entity.getEyeLocation(), 8, 0.5, 0.5, 0.5, 0);
            entity.remove();
        }
        entities.clear();
        Bukkit.broadcastMessage(ChatColor.GOLD+""+ ChatColor.BOLD+"Most and Least Valuable Players");
        Player mostKills = getPlayer(kills);
        if(mostKills == null) Bukkit.broadcastMessage(ChatColor.GREEN+"Most Kills: Nobody");
        else Bukkit.broadcastMessage(ChatColor.GREEN+"Most Kills: "+mostKills.getName());
        Player mostDeaths = getPlayer(deaths);
        if(mostDeaths == null) Bukkit.broadcastMessage(ChatColor.RED+"Most Deaths: Nobody");
        else Bukkit.broadcastMessage(ChatColor.RED+"Most Deaths: "+mostDeaths.getName());
        Player mostDamageDealt = getPlayer(damageDealt);
        if(mostDamageDealt == null) Bukkit.broadcastMessage(ChatColor.GREEN+"Most Damage Dealt: Nobody");
        else Bukkit.broadcastMessage(ChatColor.GREEN+"Most Damage Dealt: "+mostDamageDealt.getName());
        Player mostDamageTaken = getPlayer(damageTaken);
        if(mostDamageTaken == null) Bukkit.broadcastMessage(ChatColor.RED+"Most Damage Taken: Nobody");
        else Bukkit.broadcastMessage(ChatColor.RED+"Most Damage Taken: "+mostDamageTaken.getName());
        Bukkit.broadcastMessage(ChatColor.GOLD+""+ ChatColor.BOLD+"Your Statistics");
        sendPlayerStatistics();
        kills.clear();
        deaths.clear();
        damageDealt.clear();
        damageTaken.clear();
        if(victory){
            //give player rewards
            for(Player player : plugin.getServer().getOnlinePlayers()){
                PlayerParama playerParama = plugin.playerManager.getPlayerParama(player);
                playerParama.addLectrum(400);
                player.sendMessage(ChatColor.GOLD+"+400 Lectrum");
            }
        }
    }

    public void sendPlayerStatistics(){
        for(Player player : plugin.getServer().getOnlinePlayers()){
            if(kills.get(player) == null) player.sendMessage(ChatColor.GREEN+"Your Kills: 0");
            else player.sendMessage(ChatColor.GREEN+"Your Kills: "+kills.get(player));
            if(deaths.get(player)==null) player.sendMessage(ChatColor.RED+"Your Deaths: 0");
            else player.sendMessage(ChatColor.RED+"Your Deaths: "+deaths.get(player));
            if(damageDealt.get(player) == null) player.sendMessage(ChatColor.GREEN+"Your Damage Dealt: 0");
            else player.sendMessage(ChatColor.GREEN+"Your Damage Dealt: "+damageDealt.get(player));
            if(damageTaken.get(player) == null) player.sendMessage(ChatColor.RED+"Your Damage Taken: 0");
            else player.sendMessage(ChatColor.RED+"Your Damage Taken: "+damageTaken.get(player));
        }
    }

    public Player getPlayer(HashMap<Player, Integer> map){
        int max = -1;
        Player maxPlayer = null;
        for(Player player : map.keySet()){
            if(map.get(player) > max){
                max = map.get(player);
                maxPlayer = player;
            }
        }
        return maxPlayer;
    }

    public void spawnRaidBoss(World world){
        Bukkit.broadcastMessage(ChatColor.DARK_RED+"The monsters of the night seek their revenge.");
        Player target = world.getPlayers().get(rand.nextInt(world.getPlayers().size()));
        Location spawnLocation = target.getLocation();
        crimeraSpawned = true;
        //Spawn new entity
        entities.add(world.spawn(spawnLocation.add(0,20,0), Ghast.class, ghast -> {
            ghast.setCustomName(ChatColor.COLOR_CHAR + "4Crimera");
            ghast.setTarget(target);
            ghast.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(1200);
            ghast.setHealth(1200);
            ghast.setCustomNameVisible(true);
            ghast.setGlowing(true);
            ghast.setRemoveWhenFarAway(false);
        }));
        bloodMoonBossBar.setProgress(1);
        bloodMoonBossBar.setTitle(ChatColor.DARK_RED+"Crimera");
    }

    //Add amount of void enitites slain if void entitiy is killed
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(entities.contains(event.getEntity())){
            if(!crimeraSpawned){
                entitiesSlain++;
                double progress = bloodMoonBossBar.getProgress()-(1/((double)100));
                if(progress < 0) progress = 0;
                bloodMoonBossBar.setProgress(progress);
                if(entitiesSlain >= 100){
                    spawnRaidBoss(event.getEntity().getWorld());
                }
            }
            if(event.getEntity().getKiller() != null){
                Player player = event.getEntity().getKiller();
                if(kills.containsKey(player)){
                    kills.put(player, kills.get(player)+1);
                } else {
                    kills.put(player, 1);
                }
            }
            entities.remove(event.getEntity());
        }
        if(event.getEntity().getCustomName() != null){
            if(event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"4Crimera")){
                endFight(true);
                // give items
                dropSanguineGear(event.getEntity().getLocation());
                dropSanguineGear(event.getEntity().getLocation());
            }
        }
    }

    public void dropSanguineGear(Location location){
        ItemStack gear = new ItemStack(Material.BOW);
        List<String> lore = new ArrayList<>();
        switch(rand.nextInt(6)){
            case 0 -> {
                 ItemMeta meta = gear.getItemMeta();
                 meta.setDisplayName(ChatColor.COLOR_CHAR+"4Blood Rain Bow");
                 meta.addEnchant(Enchantment.ARROW_DAMAGE, 4, true);
                 meta.addEnchant(Enchantment.DURABILITY, 3, true);
                 lore.add(ChatColor.DARK_RED+"Summon a Blood Rain");
                 lore.add(ChatColor.DARK_GRAY+"Mana Cost: "+plugin.gameClassManager.archery.bloodRain.getManaCost());
                 lore.add(ChatColor.DARK_GRAY+"Cooldown: "+plugin.gameClassManager.archery.bloodRain.getCooldown()/20+" seconds");
                 meta.setLore(lore);
                 gear.setItemMeta(meta);
            }
            case 1 -> {
                gear.setType(Material.DIAMOND_HELMET);
                ItemMeta meta = gear.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"4Sanguine Helmet");
                meta.removeEnchant(Enchantment.ARROW_DAMAGE);
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                meta.addEnchant(Enchantment.DURABILITY, 3, true);

                lore.add(ChatColor.DARK_RED+"+1 Health");
                meta.setLore(lore);
                gear.setItemMeta(meta);
            }
            case 2 -> {
                gear.setType(Material.DIAMOND_CHESTPLATE);
                ItemMeta meta = gear.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"4Sanguine Chestplate");
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                meta.addEnchant(Enchantment.DURABILITY, 3, true);
                lore.add(ChatColor.DARK_RED+"+2 Health");
                meta.setLore(lore);
                gear.setItemMeta(meta);
            }
            case 3 -> {
                gear.setType(Material.DIAMOND_LEGGINGS);
                ItemMeta meta = gear.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"4Sanguine Leggings");
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                meta.addEnchant(Enchantment.DURABILITY, 3, true);
                lore.add(ChatColor.DARK_RED+"+2 Health");
                meta.setLore(lore);
                gear.setItemMeta(meta);
            }
            case 4 -> {
                gear.setType(Material.DIAMOND_BOOTS);
                ItemMeta meta = gear.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"4Sanguine Boots");
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                meta.addEnchant(Enchantment.DURABILITY, 3, true);
                meta.addEnchant(Enchantment.PROTECTION_FALL, 4, true);
                lore.add(ChatColor.DARK_RED+"+1 Health");
                meta.setLore(lore);
                gear.setItemMeta(meta);
            }
            default -> {
                gear.setType(Material.GOLDEN_HOE);
                ItemMeta meta = gear.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"4Vampire Knives");
                meta.removeEnchant(Enchantment.PROTECTION_FALL);
                meta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL);
                meta.addEnchant(Enchantment.DURABILITY, 3, true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("scythebonus", 3, AttributeModifier.Operation.ADD_NUMBER));
                lore.add(ChatColor.DARK_RED+"Can be thrown");
                lore.add(ChatColor.DARK_GRAY+"Mana Cost: "+plugin.gameClassManager.reaper.vampireKnives.getManaCost());
                meta.setLore(lore);
                gear.setItemMeta(meta);
            }
        }
        Objects.requireNonNull(location.getWorld()).dropItem(location,gear);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(plugin.bossManager.bloodMoonSummon.isBloodMoonOccuring()){
            if(event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if(damageTaken.containsKey(player)){
                    damageTaken.put(player, damageTaken.get(player)+(int)event.getDamage());
                } else {
                    damageTaken.put(player, (int) event.getDamage());
                }
            } else if(event.getDamager() instanceof Player) {
                Player player = (Player) event.getDamager();
                if(damageDealt.containsKey(player)){
                    damageDealt.put(player, damageDealt.get(player)+(int)event.getDamage());
                } else {
                    damageDealt.put(player, (int) event.getDamage());
                }
            } else if(event.getEntity().getType().equals(EntityType.GHAST)
                    && event.getEntity().getCustomName() != null
                    && event.getEntity().getCustomName().contains("Crimera")){
                Ghast ghast = (Ghast) event.getEntity();
                bloodMoonBossBar.setProgress(ghast.getHealth()/(double)1200);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(plugin.bossManager.bloodMoonSummon.isBloodMoonOccuring()){
            Player player = event.getEntity();
            if(deaths.containsKey(player)){
                deaths.put(player, deaths.get(player)+1);
            } else {
                deaths.put(player, 1);
            }
        }
    }

    @EventHandler
    public void onChangeTarget(EntityTargetLivingEntityEvent event){
        if(entities.contains(event.getEntity()) || entities.contains(event.getTarget())){
            if(!(event.getTarget() instanceof Player)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        if(plugin.bossManager.bloodMoonSummon.isBloodMoonOccuring()){
            bloodMoonBossBar.addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onAdvancementDone(PlayerAdvancementDoneEvent event){
        if(plugin.bossManager.bloodMoonSummon.isBloodMoonOccuring()){
            Advancement advancement = event.getAdvancement();
            for(String c: advancement.getCriteria()) {
                event.getPlayer().getAdvancementProgress(advancement).revokeCriteria(c);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByFireball(EntityDamageByEntityEvent event){
        if(event.getDamager().getType().equals(EntityType.FIREBALL)){
            Fireball ball = (Fireball) event.getDamager();
            if(ball.getShooter() instanceof Ghast){
                Ghast ghast = (Ghast) ball.getShooter();
                if(ghast.getCustomName() != null && (ghast.getCustomName().contains("Crimera") || ghast.getCustomName().contains("Blood Moon"))){
                    event.setDamage(event.getDamage()*2);
                }
            }
        }
    }

    @EventHandler
    public void onFireballHit(ProjectileHitEvent event){
        if(event.getEntityType().equals(EntityType.FIREBALL)){
            if(event.getEntity().getShooter() instanceof Ghast){
                Ghast ghast = (Ghast) event.getEntity().getShooter();
                if(ghast.getCustomName() != null && ghast.getCustomName().contains("Crimera")){
                    Player target = ghast.getWorld().getPlayers().get(rand.nextInt(ghast.getWorld().getPlayers().size()));
                    Location hitLocation = target.getLocation();
                    for(int x = -2; x<3; x++){
                        for(int z = -2; z<3; z++){
                            target.getWorld().spawn(target.getWorld().getHighestBlockAt(target.getLocation().add(x*2, 0, z*2)).getLocation().add(0,20,0), Fireball.class, fireball->{
                                fireball.setVelocity(new Vector(0, -1, 0));
                                fireball.setYield(0);
                                fireball.setIsIncendiary(true);
                            });
                        }
                    }
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        for(Entity entity : Objects.requireNonNull(hitLocation.getWorld()).getNearbyEntities(hitLocation, 2, 2, 2)){
                            if(entity instanceof LivingEntity){
                                ((LivingEntity) entity).damage(10, ghast);
                            }
                            if(entity instanceof Player){
                                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                            }
                        }
                    }, 30);
                    Player target2 = ghast.getWorld().getPlayers().get(rand.nextInt(ghast.getWorld().getPlayers().size()));
                    for(int x = -2; x<3; x++){
                        for(int z = -2; z<3; z++){
                            target2.getWorld().spawn(target2.getWorld().getHighestBlockAt(target2.getLocation().add(x*2, 0, z*2)).getLocation().add(0,20,0), Fireball.class, fireball->{
                                fireball.setVelocity(new Vector(0, -1, 0));
                                fireball.setYield(0);
                                fireball.setIsIncendiary(true);
                            });
                        }
                    }
                    Location hitLocation2 = target2.getLocation();
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        for(Entity entity : Objects.requireNonNull(hitLocation2.getWorld()).getNearbyEntities(hitLocation2, 2, 2, 2)){
                            if(entity instanceof LivingEntity){
                                ((LivingEntity) entity).damage(10, ghast);
                            }
                            if(entity instanceof Player){
                                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                            }
                        }
                    }, 30);
                }
            }
        }
    }

    @EventHandler
    public void onGhastDamage(EntityDamageEvent event){
        if(event.getEntityType().equals(EntityType.GHAST)){
            if(event.getEntity().getCustomName() != null
                    && event.getEntity().getCustomName().contains("4Crimera")){
                if(event.getDamage()>200){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onGhastShoot(ProjectileLaunchEvent event){
        if(event.getEntityType().equals(EntityType.FIREBALL)){
            if(event.getEntity().getShooter() instanceof Ghast){
                Ghast ghast = (Ghast) event.getEntity().getShooter();
                if(ghast.getCustomName() != null && ghast.getCustomName().contains("Blood Moon")){
                    Fireball ball = (Fireball) event.getEntity();
                    ball.setYield(0);
                    ball.setIsIncendiary(true);
                } else if(ghast.getCustomName() != null && ghast.getCustomName().contains("Crimera")){
                    Fireball ball = (Fireball) event.getEntity();
                    ball.setYield(0);
                    ball.setIsIncendiary(true);
                }
            }
        }
    }
}
