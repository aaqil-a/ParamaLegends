package me.cuna.paramalegends.boss.fight;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RaidFightListener implements Listener {

    private ParamaLegends plugin;
    private DataManager data;

    public RaidFightListener(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.getData();
    }

    List<LivingEntity> entities = new ArrayList<>(); //list to store entities spawned. must not exceed 100
    private int entitiesSlain = 0;
    private int playerCount = 0;
    private boolean raidBossSpawned;
    private BossBar raidBossBar;
    double startX;
    double startZ;
    double startSize;
    BukkitTask spawnTask;
    BukkitTask endRaidTask;

    public void raidFight(World world){
        entitiesSlain = 0;
        playerCount = world.getPlayers().size();
        entities.clear();
        startX = data.getConfig().getDouble("world.startX");
        startZ = data.getConfig().getDouble("world.startZ");
        startSize = data.getConfig().getDouble("world.startSize");
        raidBossSpawned = false;
        createRaidBossBar(world);
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            spawnSquadron(world);
        }, 0, 1200);
        endRaidTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            if(world.getTime() > 23000 || world.getTime() < 13000) endRaid();
        }, 0, 100);
    }

    //End raid if currently occuring
    public void endRaid(){
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"Cursed beings return to the void.");
        plugin.raidSummonListener.setRaidOccuring(false);
        spawnTask.cancel();
        endRaidTask.cancel();
        raidBossBar.removeAll();
        entitiesSlain = 0;
        for(LivingEntity entity : entities){
            entity.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, entity.getEyeLocation(), 8, 0.5, 0.5, 0.5, 0);
            entity.remove();
        }
        if(plugin.experienceListener.getWorldLevel() < 2){
            plugin.experienceListener.setWorldLevel(2);
        }
        entities.clear();

    }

    public void createRaidBossBar(World world){
        raidBossBar = Bukkit.createBossBar(ChatColor.DARK_PURPLE+"Void Raid", BarColor.PURPLE, BarStyle.SOLID, BarFlag.CREATE_FOG);
        for(Player player : world.getPlayers()) raidBossBar.addPlayer(player);
        raidBossBar.setVisible(true);
        raidBossBar.setProgress(1);
    }

    // Spawn mob "squadron" for each player
    // Each squadron consists of: 2 zombies, 2 skeletons, 2 spiders, 1 phantom
    // Each squadron has a 25% chance to spawn a destroyer
    // Each squadron is guaranteed to spawn a destroyer if targeted player is below a block
    // Squadron spawns at a random location inside safe zone
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

                Random rand = new Random();
                //Spawn squadron
                Bukkit.getScheduler().runTaskLater(plugin, ()->{

                    //Determine whether to spawn destroyer or not
                    if((rand.nextInt(4) == 1) || (player.getLocation().getY() < world.getHighestBlockYAt(player.getLocation())))
                        entities.add(world.spawn(spawnLocation, Zombie.class, zombie -> {
                            zombie.setTarget(player);
                            zombie.setCustomName(ChatColor.COLOR_CHAR+"5Void Destroyer");
                            zombie.setCustomNameVisible(true);
                        }));

                    //Spawn remaining squadron
                    spawnLocation.add(-1,0,-1);
                    final String[] spawnMap = new String[]{"SZS","RPR","-Z-"};
                    for(int x = 0; x < 3; x++){
                        for(int z = 0; z < 3; z++){
                            switch(spawnMap[x].charAt(z)) {
                                case 'S' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,1,0), Skeleton.class, skeleton -> {
                                    skeleton.setTarget(player);
                                    skeleton.setCustomName(ChatColor.COLOR_CHAR+"5Void Skeleton");
                                }));
                                case 'Z' ->  entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,1,0), Zombie.class, zombie -> {
                                    zombie.setTarget(player);
                                    zombie.setCustomName(ChatColor.COLOR_CHAR+"5Void Zombie");
                                }));
                                case 'R' ->  entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,1,0), Spider.class, spider -> {
                                    spider.setTarget(player);
                                    spider.setCustomName(ChatColor.COLOR_CHAR+"5Void Spider");
                                }));
                                case 'P' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,3,0), Phantom.class, phantom -> {
                                    phantom.setTarget(player);
                                    phantom.setCustomName(ChatColor.COLOR_CHAR+"5Void Phantom");
                                }));
                            }
                            spawnLocation.add(0,0,1);
                        }
                        spawnLocation.add(1,0,0);
                    }
                    spawnAnimation.cancel();
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

    //Destroys block around entity (for void destroyer)
    public void destroyBlocks(LivingEntity entity){
        entity.getWorld().createExplosion(entity.getLocation(), 2F, false, true, entity);
    }

    // One raid boss spawns at a random location in the safe zone
    // Players must kill the raid boss to obtain 'void essence'
    public void spawnRaidBoss(World world){
        raidBossSpawned = true;
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"A frightening presence peers into you.");
        spawnTask.cancel();
        Location spawnLocation = new Location(world, startX, world.getHighestBlockYAt((int)startX, (int)startZ), startZ);

        //Despawn current entities
        for(LivingEntity entity : entities){
            entity.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, entity.getEyeLocation(), 8, 0.5, 0.5, 0.5, 0);
            entity.remove();
        }
        entities.clear();

        //Spawn new entity
        entities.add(world.spawn(spawnLocation.add(0,1,0), Wither.class, wither -> {
            wither.setCustomName(ChatColor.COLOR_CHAR+"5Void Nullifier");
            wither.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(750);
            wither.setHealth(750);
            wither.setCustomNameVisible(true);
            wither.setGlowing(true);
        }));
        raidBossBar.removeAll();
        raidBossBar.setVisible(false);
    }

    //Only make void entities target players
    @EventHandler
    public void onChangeTarget(EntityTargetLivingEntityEvent event){
        if(entities.contains(event.getEntity()) || entities.contains(event.getTarget())){
            if(!(event.getTarget() instanceof Player)){
                event.setCancelled(true);
            }
        }
    }

    //Add amount of void enitites slain if void entitiy is killed
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(entities.contains(event.getEntity())){
            entitiesSlain++;
            double progress = raidBossBar.getProgress()-(1/((double)playerCount*35));
            if(progress < 0) progress = 0;
            raidBossBar.setProgress(progress);
            if(entitiesSlain >= playerCount*35 && !raidBossSpawned){
                spawnRaidBoss(event.getEntity().getWorld());
            }
            entities.remove(event.getEntity());
        }
        if(event.getEntity().getCustomName() != null){
            //Explode if void destroyer dies
            if(event.getEntity().getCustomName().contains("Void Destroyer")) destroyBlocks(event.getEntity());
            //End raid and drop void essence if void nullifier dies
            else if(event.getEntity().getCustomName().equals(ChatColor.COLOR_CHAR+"5Void Nullifier")){
                endRaid();
                raidBossBar.setProgress(0);
                ItemStack voidEssence = new ItemStack(Material.ENDER_EYE);
                ItemMeta meta = voidEssence.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"5Void Essence");
                meta.addEnchant(Enchantment.DURABILITY, 10, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY+"A cryptic orb that emits");
                lore.add(ChatColor.GRAY+"a sinister aura.");
                meta.setLore(lore);
                voidEssence.setItemMeta(meta);
                event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), voidEssence).setCustomName("Void Essence");
            }
        }
    }
}
