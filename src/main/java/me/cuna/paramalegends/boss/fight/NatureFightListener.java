package me.cuna.paramalegends.boss.fight;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.pathfinder.PathEntity;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;


import java.util.*;

public class NatureFightListener implements Listener {

    private ParamaLegends plugin;
    private DataManager data;
    private final HashMap<Player, Integer> damageDealt = new HashMap<>();
    private final HashMap<Player, Integer> kills = new HashMap<>();
    private final HashMap<Player, Integer> damageTaken = new HashMap<>();


    public NatureFightListener(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.getData();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    List<Entity> entities = new ArrayList<>(); //list to store entities spawned. must not exceed 100
    List<Player> alive = new ArrayList<>();
    private int playerCount = 0;
    private BossBar bossBar;
    double startX;
    double startY;
    double startZ;
    Slime boss;
    int curWave;
    int waveSize;
    boolean secondPhase;
    BukkitTask spawnTask;
    BukkitTask freezeTime;
    int deathCount;

    public void bossFight(World world, Location location){
        playerCount = world.getPlayers().size();
        damageDealt.clear();
        kills.clear();
        damageTaken.clear();
        entities.clear();
        alive.clear();
        startX = location.getX();
        startY = location.getY();
        startZ = location.getZ();
        deathCount = 0;
        secondPhase = false;
        alive.addAll(Bukkit.getOnlinePlayers());
        double time = world.getTime();
        freezeTime = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            world.setTime((long) time);
        }, 0, 200);
        curWave = 1;
        Bukkit.getScheduler().runTaskLater(plugin, ()->spawnWave(world), 100);
    }

    public void spawnSkeleton(World world, Location spawnLocation, Player player){
        entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,2,0), Skeleton.class, skeleton -> {
            skeleton.setTarget(player);
            Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                    .setBaseValue(Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue()*1.2);
            skeleton.setCustomName(ChatColor.COLOR_CHAR+"aNature Skeleton");
        }));
    }
    public void spawnSlime(World world, Location spawnLocation, Player player){
        entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,2,0), Slime.class, slime -> {
            slime.setTarget(player);
            slime.setSize(3);
            Objects.requireNonNull(slime.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(30);
            slime.setHealth(30);
            Objects.requireNonNull(slime.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                    .setBaseValue(Objects.requireNonNull(slime.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getValue()*2);
            slime.setCustomName(ChatColor.COLOR_CHAR+"aNature Slime");
        }));
    }
    public void spawnPhantom(World world, Location spawnLocation, Player player){
        entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,4,0), Phantom.class, phantom -> {
            phantom.setSilent(true);
            phantom.setTarget(player);
            Objects.requireNonNull(phantom.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
                    .setBaseValue(8);
            Objects.requireNonNull(phantom.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(10);
            phantom.setHealth(10);
            phantom.setCustomName(ChatColor.COLOR_CHAR+"aNature Phantom");
        }));
    }
    public void spawnWitch(World world, Location spawnLocation, Player player){
        entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,2,0), Witch.class, witch -> {
            witch.setTarget(player);
            Objects.requireNonNull(witch.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(140);
            witch.setHealth(140);
            witch.setCustomName(ChatColor.COLOR_CHAR+"aNature Witch");
        }));
    }
    public void spawnZombieHorse(World world, Location spawnLocation, Player player){
        entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,2,0), ZombieHorse.class, zombieHorse -> {
            Objects.requireNonNull(zombieHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(100);
            zombieHorse.setHealth(100);
            zombieHorse.setAdult();
            zombieHorse.setCustomName(ChatColor.COLOR_CHAR+"aNature Zombie Horse");
            zombieHorseAttack(zombieHorse, player);
        }));
    }
    //spawn waves
    public void spawnWave(World world){
        Random rand = new Random();
        switch(curWave){
            // First Wave
            case 1,2 -> {
                waveSize = 2*playerCount;
                if (curWave % 2 == 0) {
                    createBossBar(world, "Second Wave");
                } else {
                    createBossBar(world, "First Wave");
                }
                final String[] spawnMap = new String[]{"---", "-KS"};
                if(spawnTask != null)spawnTask.cancel();
                spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!alive.contains(player)){
                            player = alive.get(rand.nextInt(alive.size()));
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2f, 1f);
                        Location spawnLocation = randomLocation(player);
                        if(entities.size() < 150) {
                            for (int x = 0; x < 2; x++) {
                                for (int z = 0; z < 3; z++) {
                                    switch (spawnMap[x].charAt(z)) {
                                        case 'S' -> spawnSkeleton(world, spawnLocation, player);
                                        case 'K' -> spawnSlime(world, spawnLocation, player);
                                    }
                                    spawnLocation.add(0, 0, 1);
                                }
                                spawnLocation.add(1, 0, 0);
                            }
                        }
                    }
                }, 100, 1200);

            }
            //second wave
            case 3,4 -> {
                waveSize = 3*playerCount;
                if (curWave % 2 == 0) {
                    createBossBar(world, "Fourth Wave");
                } else {
                    createBossBar(world, "Third Wave");
                }
                final String[] spawnMap = new String[]{"-P-","-KS"};
                if(spawnTask != null)spawnTask.cancel();
                spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!alive.contains(player)){
                            player = alive.get(rand.nextInt(alive.size()));
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2f, 1f);
                        if (entities.size() < 150) {
                            Location spawnLocation = randomLocation(player);
                            for (int x = 0; x < 2; x++) {
                                for (int z = 0; z < 3; z++) {
                                    switch (spawnMap[x].charAt(z)) {
                                        case 'S' -> spawnSkeleton(world, spawnLocation, player);
                                        case 'K' -> spawnSlime(world, spawnLocation, player);
                                        case 'P' -> spawnPhantom(world, spawnLocation, player);
                                    }
                                    spawnLocation.add(0, 0, 1);
                                }
                                spawnLocation.add(1, 0, 0);
                            }
                        }
                    }
                }, 100, 1200);
            }
            //third wave
            case 5,6 -> {
                waveSize = 4*playerCount;
                if (curWave % 2 == 0) {
                    createBossBar(world, "Sixth Wave");
                } else {
                    createBossBar(world, "Fifth Wave");
                }
                if(spawnTask != null)spawnTask.cancel();
                spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!alive.contains(player)){
                            player = alive.get(rand.nextInt(alive.size()));
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2f, 1f);
                        final String[] spawnMap = new String[]{"-P-","WKS"};
                        if(entities.size() < 150) {
                            Location spawnLocation = randomLocation(player);
                            for(int x = 0; x < 2; x++){
                                for(int z = 0; z < 3; z++){
                                    switch(spawnMap[x].charAt(z)) {
                                        case 'S' -> spawnSkeleton(world, spawnLocation, player);
                                        case 'K' -> spawnSlime(world, spawnLocation, player);
                                        case 'P' -> spawnPhantom(world, spawnLocation, player);
                                        case 'W' -> spawnWitch(world, spawnLocation, player);
                                    }
                                    spawnLocation.add(0,0,1);
                                }
                                spawnLocation.add(1,0,0);
                            }
                        }
                    }
                }, 100, 1200);
            }
            //fourth wave
            case 7,8 -> {
                waveSize = 5*playerCount;
                if (curWave % 2 == 0) {
                    createBossBar(world, "Eighth Wave");
                } else {
                    createBossBar(world, "Seventh Wave");
                }
                    final String[] spawnMap = new String[]{"ZPW","-KS"};
                    if(spawnTask != null)spawnTask.cancel();
                    spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            if(!alive.contains(player)){
                                player = alive.get(rand.nextInt(alive.size()));
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2f, 1f);
                            if(entities.size() < 150) {
                                Location spawnLocation = randomLocation(player);
                                for(int x = 0; x < 2; x++) {
                                    for (int z = 0; z < 3; z++) {
                                        switch (spawnMap[x].charAt(z)) {
                                            case 'S' -> spawnSkeleton(world, spawnLocation, player);
                                            case 'K' -> spawnSlime(world, spawnLocation, player);
                                            case 'P' -> spawnPhantom(world, spawnLocation, player);
                                            case 'W' -> spawnWitch(world, spawnLocation, player);
                                            case 'Z' -> spawnZombieHorse(world, spawnLocation, player);
                                        }
                                        spawnLocation.add(0, 0, 1);
                                    }
                                    spawnLocation.add(1, 0, 0);
                                }
                            }
                        }
                    }, 100, 1200);
                }
            //fifth wave
            case 9,10 -> {
                waveSize = 7*playerCount;
                if (curWave % 2 == 0) {
                    createBossBar(world, "Tenth Wave");
                } else {
                    createBossBar(world, "Ninth Wave");
                }
                if(spawnTask != null)spawnTask.cancel();
                final String[] spawnMap = new String[]{"ZPW","-KS", "ZK-"};
                spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!alive.contains(player)){
                            player = alive.get(rand.nextInt(alive.size()));
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 2f, 1f);
                        if (entities.size() < 150) {
                            Location spawnLocation = randomLocation(player);
                            for (int x = 0; x < 3; x++) {
                                for (int z = 0; z < 3; z++) {
                                    switch (spawnMap[x].charAt(z)) {
                                        case 'S' -> spawnSkeleton(world, spawnLocation, player);
                                        case 'K' -> spawnSlime(world, spawnLocation, player);
                                        case 'P' -> spawnPhantom(world, spawnLocation, player);
                                        case 'W' -> spawnWitch(world, spawnLocation, player);
                                        case 'Z' -> spawnZombieHorse(world, spawnLocation, player);
                                    }
                                    spawnLocation.add(0, 0, 1);
                                }
                                spawnLocation.add(1, 0, 0);
                            }
                        }
                    }
                }, 100, 1200);
            }
        }
    }

    //End fight if currently occuring
    public void endFight(boolean win){
        spawnTask.cancel();
        Bukkit.broadcastMessage(ChatColor.GREEN+"Hostile creatures return to nature.");
        if(boss != null) {
            boss.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, boss.getLocation(), 8, 0.5, 0.5, 0.5, 0);
            boss.remove();
        }
        plugin.natureSummonListener.setFightOccuring(false);
        bossBar.removeAll();
        for(Entity entity : entities){
            if(entity != null){
                entity.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, entity.getLocation(), 8, 0.5, 0.5, 0.5, 0);
                entity.remove();
            }
        }
        if(plugin.experienceListener.getWorldLevel() < 3 && win){
            plugin.experienceListener.setWorldLevel(3);
        }
        //set gamemode back to survival and tp back
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getGameMode().equals(GameMode.SPECTATOR)){
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(new Location(player.getWorld(), startX, startY, startZ));
            }
        }
        entities.clear();
        freezeTime.cancel();
        Bukkit.broadcastMessage(ChatColor.GOLD+""+ ChatColor.BOLD+"Most and Least Valuable Players");
        Player mostKills = getPlayer(kills);
        if(mostKills == null) Bukkit.broadcastMessage(ChatColor.GREEN+"Most Kills: Nobody");
        else Bukkit.broadcastMessage(ChatColor.GREEN+"Most Kills: "+mostKills.getName());
        Player mostDamageDealt = getPlayer(damageDealt);
        if(mostDamageDealt == null) Bukkit.broadcastMessage(ChatColor.GREEN+"Most Damage Dealt: Nobody");
        else Bukkit.broadcastMessage(ChatColor.GREEN+"Most Damage Dealt: "+mostDamageDealt.getName());
        Player mostDamageTaken = getPlayer(damageTaken);
        if(mostDamageTaken == null) Bukkit.broadcastMessage(ChatColor.RED+"Most Damage Taken: Nobody");
        else Bukkit.broadcastMessage(ChatColor.RED+"Most Damage Taken: "+mostDamageTaken.getName());
        Bukkit.broadcastMessage(ChatColor.GOLD+""+ ChatColor.BOLD+"Your Statistics");
        sendPlayerStatistics();

        if(win){
            for(Player player : plugin.getServer().getOnlinePlayers()){
                //give player rewards
                PlayerParama playerParama = plugin.getPlayerParama(player);
                playerParama.addLectrum(1000);
                player.sendMessage(ChatColor.GOLD+"+1000 Lectrum");
            }
        }
    }

    public void sendPlayerStatistics(){
        for(Player player : plugin.getServer().getOnlinePlayers()){
            if(kills.get(player) == null) player.sendMessage(ChatColor.GREEN+"Your Kills: 0");
            else player.sendMessage(ChatColor.GREEN+"Your Kills: "+kills.get(player));
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

    public void createBossBar(World world, String name){
        bossBar = Bukkit.createBossBar(ChatColor.GREEN+name, BarColor.GREEN, BarStyle.SOLID);
        for(Player player : world.getPlayers()) bossBar.addPlayer(player);
        bossBar.setVisible(true);
        bossBar.setProgress(1);
    }

    //Get random location to spawn mob
    public Location randomLocation(Player player){
        Random rand = new Random();
        int offsetX = 5 - rand.nextInt(10);
        int offsetZ = 5 - rand.nextInt(10);
        if(offsetX > 0) offsetX += 5;
        else offsetX -= 5;
        if(offsetZ > 0) offsetZ += 5;
        else offsetZ -= 5;
        int spawnX = (int) player.getLocation().getX() + offsetX;
        int spawnZ = (int) player.getLocation().getZ() + offsetZ;
        return new Location(player.getWorld(), spawnX, player.getWorld().getHighestBlockYAt(spawnX, spawnZ), spawnZ).add(0,1,0);
    }

    public void spawnBoss(World world){
        //Despawn current entities
        for(Entity entity : entities){
            if(entity != null){
                entity.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, entity.getLocation(), 8, 0.5, 0.5, 0.5, 0);
                entity.remove();
            }
        }
        entities.clear();
        bossBar.removeAll();
        bossBar.setVisible(false);

        //spawn slime king
        Random rand = new Random();
        Location spawnLocation = world.getHighestBlockAt(alive.get(rand.nextInt(alive.size())).getLocation()).getLocation();
        boss = world.spawn(spawnLocation, Slime.class, slime ->{
            slime.setSize(10);
            slime.setCustomName(ChatColor.COLOR_CHAR+"aKing Slime");
            Objects.requireNonNull(slime.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                        .setBaseValue(2000);
            slime.setHealth(2000);
            slime.setAware(true);
            Objects.requireNonNull(slime.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE))
                    .setBaseValue(6);
            slime.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
            bossThrowSlimeball(slime);
            bossJumpAttack(slime);
            slimePhaseAttack(slime);
        });
        createBossBar(world, "King Slime");
    }
    //task for slime throw slimeball attack
    public void bossThrowSlimeball(Slime slime) {
        Random rand = new Random();
        new BukkitRunnable(){
            @Override
            public void run(){
                if(alive.size()<=0 || slime.isDead()){
                    cancel();
                    return;
                }
                if(slime.hasMetadata("PHASING") || slime.hasMetadata("JUMPING")){
                    return;
                }
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(rand.nextInt(2)==0){
                        Location targetLocation = player.getEyeLocation();
                        Vector velocity = targetLocation.toVector().subtract(slime.getEyeLocation().toVector()).normalize().multiply(2);
                        Snowball ball = slime.launchProjectile(Snowball.class, velocity);
                        ball.setMetadata("slimekingball", new FixedMetadataValue(plugin, true));
                        ball.setItem(new ItemStack(Material.SLIME_BALL));
                        ball.setGravity(false);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 120);
    }
    //task for slime jump attack
    public void bossJumpAttack(Slime slime) {
        new BukkitRunnable(){
            final Random rand = new Random();
            final FixedMetadataValue jumping = new FixedMetadataValue(plugin, true);
            BukkitRunnable landing;
            @Override
            public void run(){
                if(alive.size()<=0 || slime.isDead()){
                    cancel();
                    return;
                }
                if(slime.hasMetadata("PHASING")){
                    return;
                }
                Player target = alive.get(rand.nextInt(alive.size()));
                Location targetLocation = target.getLocation();
                Vector velocity = targetLocation.toVector().subtract(slime.getLocation().toVector()).setY(0).normalize().setY(2);
                slime.setVelocity(velocity);
                slime.setTarget(target);
                slime.setMetadata("JUMPING", jumping);

                if(landing != null && !landing.isCancelled()){
                    landing.cancel();
                }
                //listen for when slime lands
                landing = new BukkitRunnable(){
                    @Override
                    public void run() {
                        if((slime.getVelocity().getX() == 0 && slime.getVelocity().getZ() == 0) || slime.getVelocity().getY() == 0 || slime.isOnGround()){
                            for(Entity entity :slime.getNearbyEntities(3, 5, 3)){
                                if(entity instanceof Player){
                                    Player hit = (Player) entity;
                                    hit.damage(12, slime);
                                }
                            }
                            slime.removeMetadata("JUMPING", plugin);
                            cancel();
                        }
                    }
                };
                landing.runTaskTimer(plugin, 10, 10);
            }
        }.runTaskTimer(plugin, 0, 300);
    }

    //task for slime bomber
    public void slimeBomberAttack(Slime slime){
        new BukkitRunnable(){
            World world = slime.getWorld();
            @Override
            public void run(){
                if(alive.size()<=0 || slime.isDead()){
                    cancel();
                    return;
                }
                int delay;
                if(slime.hasMetadata("JUMPING")){
                    delay = 100;
                } else if(slime.hasMetadata("PHASING")){
                    return;
                } else {
                    delay = 0;
                }
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    Location location = slime.getLocation();
                    for(Player player : Bukkit.getOnlinePlayers()){
                        Location targetLocation = player.getLocation();
                        Vector locationOffset = targetLocation.toVector().subtract(slime.getLocation().toVector()).normalize().multiply(1.5);
                        entities.add(world.spawn(location.add(locationOffset), Slime.class, bomber->{
                            bomber.setTarget(player);
                            bomber.setSize(2);
                            Objects.requireNonNull(bomber.getAttribute(Attribute.GENERIC_MAX_HEALTH))
                                    .setBaseValue(50);
                            bomber.setHealth(50);
                            Objects.requireNonNull(bomber.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED))
                                    .setBaseValue(Objects.requireNonNull(bomber.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue()*1.5);
                            bomber.setCustomName(ChatColor.COLOR_CHAR+"aSlime Bomber");
                        }));
                    }
                }, delay);
            }
        }.runTaskTimer(plugin, 0, 200);
    }

    //task for underground phase attack
    public void slimePhaseAttack(Slime slime){
        new BukkitRunnable(){
            World world = slime.getWorld();
            final Random rand = new Random();
            final FixedMetadataValue phasing = new FixedMetadataValue(plugin, true);
            @Override
            public void run(){
                if(alive.size()<=0 || slime.isDead()){
                    cancel();
                    return;
                }
                int delay;
                if(slime.hasMetadata("JUMPING")){
                    delay = 100;
                } else {
                    delay = 0;
                }
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    slime.setMetadata("PHASING", phasing);
                    slime.setAware(false);
                    //digging downwards
                    BukkitTask digging = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                        slime.teleport(slime.getLocation().add(0,-1,0));
                    }, 10, 10);
                    //set invulnerable when underground
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        slime.setInvisible(true);
                        slime.setInvulnerable(true);
                        for (PotionEffect effect : slime.getActivePotionEffects())
                            slime.removePotionEffect(effect.getType());
                        digging.cancel();

                    }, 100);
                    //select random player and tp slime to the palyer
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    if(alive.size()>0 && !slime.isDead()){
                        Player target = alive.get(rand.nextInt(alive.size()));
                        slime.teleport(target.getLocation().subtract(0,10,0));
                    }
                    }, 190);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        if(alive.size()>0 && !slime.isDead()){
                            Player target = alive.get(rand.nextInt(alive.size()));
                            slime.setVelocity(new Vector(0, 1, 0));
                            slime.setTarget(target);
                            slime.teleport(target.getLocation());
                            slime.setAware(true);
                            slime.setInvisible(false);
                            slime.setInvulnerable(false);
                            slime.removeMetadata("PHASING", plugin);
                        }
                    }, 200);
                }, delay);
            }
        }.runTaskTimer(plugin, 0, 1200);
    }

    //task for zombie horse to attack player
    public void zombieHorseAttack(ZombieHorse horse, Player player) {
        new BukkitRunnable(){
            int cooldown = 0    ;
            Player target = player;
            @Override
            public void run(){
                //cancel task if horse is dead
                if(horse.isDead()) cancel();
                //make horse follow player
                Location location = player.getLocation();
                Object pObject = ((CraftEntity) horse).getHandle();
                PathEntity path = ((EntityInsentient) pObject).getNavigation().a(location.getX(), location.getY(), location.getZ(), 1);
                if (path != null) {
                    ((EntityInsentient) pObject).getNavigation().a(path, 2.0D);
                }
                //change target if player dead
                if(!alive.contains(target)){
                    if(alive.size()>0){
                        Random rand = new Random();
                        target = alive.get(rand.nextInt(alive.size()));
                    } else {
                        horse.remove();
                        cancel();
                    }
                }
                // reduce attack tick cooldown
                if(cooldown > 0) cooldown--;
                //attack player if in range
                if(horse.getLocation().distance(player.getLocation()) < 1.5 && cooldown <= 0){
                    player.damage(8, horse);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0, true));
                    cooldown = 8;
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    //victory firework effect
    public void createFireworkEffect(Location location){
        Firework firework = (Firework) Objects.requireNonNull(location.getWorld())
                .spawnEntity(location.add(new Vector(0,1,0)), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
                .flicker(true).trail(true).withColor(Color.WHITE, Color.GREEN, Color.YELLOW)
                .withTrail().withFlicker().build());
        meta.setPower(2);
        firework.setFireworkMeta(meta);
        Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 20);
    }

    //king slime ball attack listener
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if(event.getEntity().hasMetadata("slimekingball")){
            if(event.getHitEntity() != null && event.getHitEntity() instanceof Player){
                Player player = (Player) event.getHitEntity();
                player.damage(8, boss);
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2));
            }
        }
    }

    //Only make  entities target players
    @EventHandler
    public void onChangeTarget(EntityTargetLivingEntityEvent event){
        if(entities.contains(event.getEntity()) || entities.contains(event.getTarget())){
            if(!(event.getTarget() instanceof Player)){
                event.setCancelled(true);
            }
        }
        if(event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"aKing Slime")){
            if(!(event.getTarget() instanceof Player)){
                event.setCancelled(true);
            }
        }
    }

    //Add amount of void enitites slain if void entitiy is killed
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(plugin.natureSummonListener.isFightOccuring()){
            if(entities.contains(event.getEntity())){
                if(!bossBar.getTitle().contains("King Slime")){
                    double progress = bossBar.getProgress()-(1/((double) waveSize));
                    if(progress < 0) progress = 0;
                    bossBar.setProgress(progress);
                }
                if(event.getEntity().getKiller() != null){
                    Player player = event.getEntity().getKiller();
                    if(kills.containsKey(player)){
                        kills.put(player, kills.get(player)+1);
                    } else {
                        kills.put(player, 1);
                    }
                }
                deathCount++;
                entities.remove(event.getEntity());
                if(deathCount >= waveSize){
                    bossBar.removeAll();
                    deathCount = 0;
                    curWave++;
                    for(Entity entity : entities){
                        if(entity != null){
                            entity.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, entity.getLocation(), 8, 0.5, 0.5, 0.5, 0);
                            entity.remove();
                        }
                    }
                    spawnTask.cancel();
                    if(curWave<=10){
                        Bukkit.getScheduler().runTaskLater(plugin, ()->spawnWave(event.getEntity().getWorld()), 200);
                    } else {
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                            Bukkit.broadcastMessage(ChatColor.GREEN+"The wrath of nature grows stronger.");
                            for(Player player : Bukkit.getOnlinePlayers()){
                                player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1f, 1f);
                            }
                        }, 100);
                        Bukkit.getScheduler().runTaskLater(plugin, ()->spawnBoss(event.getEntity().getWorld()), 200);
                    }
                }
            }
            if(event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"aKing Slime")){
                event.getEntity().getWorld().setStorm(true);
                createFireworkEffect(event.getEntity().getLocation());
                endFight(true);
                ItemStack natureEssence = new ItemStack(Material.SLIME_BLOCK);
                ItemMeta meta = natureEssence.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"aEssence of Nature");
                meta.addEnchant(Enchantment.DURABILITY, 10, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY+"A slimy ooze that radiates");
                lore.add(ChatColor.GRAY+"the energy of life.");
                meta.setLore(lore);
                natureEssence.setItemMeta(meta);
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), natureEssence).setCustomName("Essence of Nature");
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(plugin.natureSummonListener.isFightOccuring()){
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
            }
            //check if slime or phantom and apply poison if so
            if(event.getEntity() instanceof Player && entities.contains(event.getDamager())){
                if(event.getDamager() instanceof Slime) {
                    Player player = (Player) event.getEntity();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, true));
                }
                if(event.getDamager() instanceof Phantom){
                    Player player = (Player) event.getEntity();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, true));
                }
                if(event.getDamager().getCustomName() != null && event.getDamager().getCustomName().contains("Slime Bomber")){
                    Slime slime = (Slime) event.getDamager();
                    World world = slime.getWorld();
                    world.createExplosion(slime.getLocation(), 1F, false, false);
                    for(Entity poisoned : slime.getNearbyEntities(1,1,1)){
                        if(poisoned instanceof Player){
                            ((Player) poisoned).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0, true));
                        }
                    }
                    slime.remove();
                }
            }
            //make entities only vulnerable to players
            if(entities.contains(event.getEntity()) && !(event.getDamager() instanceof Player)){
                event.setCancelled(true);
            }
            if(event.getDamager() instanceof Arrow){
                Arrow arrow = (Arrow) event.getDamager();
                if(arrow.getShooter() instanceof Player){
                    event.setCancelled(false);
                }
            }
            //check if boss and update boss bar if so
            if(event.getEntity().getCustomName() != null && event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"aKing Slime")){
                Slime slime = (Slime) event.getEntity();
                bossBar.setProgress(slime.getHealth()/2000);
                if(slime.getHealth() <= 1000 && !secondPhase){
                    secondPhase = true;
                    slimeBomberAttack(boss);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if(plugin.natureSummonListener.isFightOccuring()){
            Player player=  event.getEntity();
            alive.remove(player);
            if(alive.size() == 0) {
                endFight(false);
            }
            else {
                player.sendMessage(ChatColor.RED+"You died. Wait for fight to end to respawn.");
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(plugin.natureSummonListener.isFightOccuring()){
            event.setRespawnLocation(new Location(event.getPlayer().getWorld(), startX, startY, startZ));
        }
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event){
        if(event.getEntity().getCustomName() != null){
            if(event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"aNature Slime")
                    || event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"aKing Slime")
                    || event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"aSlime Bomber"))
                event.setCancelled(true);
        }
    }
    //cancel day burning of mobs
    @EventHandler
    public void onCombust(EntityCombustEvent event){
        if(plugin.natureSummonListener.isFightOccuring() && entities.contains(event.getEntity())){
            if(!(event instanceof EntityCombustByBlockEvent || event instanceof EntityCombustByEntityEvent)){
                event.setCancelled(true);
            }
        }
    }

    public int getPlayerCount(){
        return playerCount;
    }
}
