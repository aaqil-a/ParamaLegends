package id.cuna.ParamaLegends.BossListener.BossTypeListener;

import com.sk89q.worldedit.WorldEdit;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RaidListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public boolean isRaidOccuring = false;
    private BoundingBox safeZoneBox;

    public RaidListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Listen for summoning item usage
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().hasItemMeta()){
            if(event.getAction().equals(Action.RIGHT_CLICK_AIR)){
                event.setCancelled(true);
                return;
            }
            ItemMeta meta = event.getItem().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals("§6Esoteric Pearl")){
                if(!isRaidOccuring){
                    if(event.getPlayer().getWorld().getTime() < 13000 || event.getPlayer().getWorld().getTime() > 23000){
                        event.getPlayer().sendMessage("§6Esoteric Pearl"+ChatColor.GRAY+" can only be used at night.");
                    } else {
                        //Check if player inside safe zone
                        if(safeZoneCheck(event.getPlayer())) {
                            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"Shrieks and cries can be heard in the distance.");
                            isRaidOccuring = true;
                            event.getPlayer().getWorld().setTime(14000);
                            raidFight(event.getPlayer().getWorld());
                        } else event.getPlayer().sendMessage("§6Esoteric Pearl"+ChatColor.GRAY+" can only be used nearby the §6Occult Altar"+ChatColor.GRAY+".");
                    }
                } else event.getPlayer().sendMessage("§6Esoteric Pearl"+ChatColor.GRAY+" cannot be used during a raid.");
                event.setCancelled(true);
            }
        }
    }

    public boolean safeZoneCheck(Player player){
        double startX = data.getConfig().getDouble("world.startX");
        double startZ = data.getConfig().getDouble("world.startZ");
        BoundingBox safeZoneBox = new BoundingBox(startX+10, 0, startZ+10, startX-10, 256, startZ-10);
        return safeZoneBox.contains(player.getLocation().toVector());
    }

    List<Entity> entities = new ArrayList<>(); //list to store entities spawned. must not exceed 100
    private int entitiesSlain = 0;
    private int playerCount = 0;
    double startX;
    double startZ;
    double startSize;
    int worldLevel;
    BukkitTask spawnTask;
    public void raidFight(World world){
        entitiesSlain = 0;
        playerCount = world.getPlayers().size();
        entities.clear();
        startX = data.getConfig().getDouble("world.startX");
        startZ = data.getConfig().getDouble("world.startZ");
        startSize = data.getConfig().getDouble("world.startSize");
        worldLevel = data.getConfig().getInt("world.level");
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            spawnSquadron(world, startX, startZ, startSize, worldLevel);
        }, 0, 1200);
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"Cursed beings return to the void.");
            isRaidOccuring = false;
            spawnTask.cancel();
            for(Entity entity : entities){
                entity.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, entity.getLocation(), 8, 0.5, 0.5, 0.5, 0);
                entity.remove();
            }
            entities.clear();
        }, 9000);
    }


    // Spawn mob "squadron" for each player
    // Each squadron consists of: 2 zombies, 2 skeletons, 2 spiders, 1 phantom
    // Each squadron has a 25% chance to spawn a destroyer
    // Each squadron is guaranteed to spawn a destroyer if targeted player is below a block
    // Squadron spawns at a random location inside safe zone
    public void spawnSquadron(World world, double X, double Z, double size, int worldLevel){
        for (Player player : world.getPlayers()){
            //Get random location to spawn mob
            Random rand = new Random();
            int offsetX = (int) size - rand.nextInt((int)size*2);
            int offsetZ = (int) size - rand.nextInt((int) size*2);
            int spawnX = (int) X + offsetX;
            int spawnZ = (int) Z + offsetZ;
            Location spawnLocation = new Location(world, spawnX, world.getHighestBlockYAt(spawnX, spawnZ), spawnZ);
            spawnLocation.add(0,1,0);
            //Create spawning animation
            BukkitTask spawnAnimation = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                world.spawnParticle(Particle.FLAME, spawnLocation, 4, 0.5, 0.5, 0.5, 0);
                world.spawnParticle(Particle.SMOKE_NORMAL, spawnLocation, 4, 0.5, 0.5, 0.5, 0);
                world.playSound(spawnLocation, Sound.BLOCK_PORTAL_AMBIENT, 1f, 1f);
            }, 0, 20);
            boolean spawnDestroyer = (rand.nextInt(4) == 1);
            //Spawn squadron
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                Bukkit.broadcastMessage("spawning squadron at " + spawnLocation.toVector().toString());
                if(spawnDestroyer) world.spawn(spawnLocation, ArmorStand.class, armorStand -> {

                });
                spawnLocation.add(-1,0,-1);
                final String[] spawnMap = new String[]{"SZS","RPR","-Z-"};
                for(int x = 0; x < 3; x++){
                    for(int z = 0; z < 3; z++){
                        switch(spawnMap[x].charAt(z)) {
                            case 'S' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,1,0), Skeleton.class, skeleton -> skeleton.setTarget(player)));
                            case 'Z' ->  entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,1,0), Zombie.class, zombie -> zombie.setTarget(player)));
                            case 'R' ->  entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,1,0), Spider.class, spider -> spider.setTarget(player)));
                            case 'P' -> entities.add(world.spawn(world.getHighestBlockAt(spawnLocation).getLocation().add(0,1,0), Phantom.class, phantom -> phantom.setTarget(player)));
                        }
                        spawnLocation.add(0,0,1);
                    }
                    spawnLocation.add(1,0,0);
                }
                spawnAnimation.cancel();
            }, 100);
        }
    }

    // One raid boss spawns at a random location in the safe zone
    // Players must kill the raid boss to obtain 'void essence'
    public void spawnRaidBoss(World world){
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE+"You feel a frightening presence.");
        spawnTask.cancel();
    }

    @EventHandler
    public void onChangeTarget(EntityTargetLivingEntityEvent event){
        if(entities.contains(event.getEntity())){
            if(!(event.getTarget() instanceof Player)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(event.getEntity().getKiller() != null){
            if(entities.contains(event.getEntity())){
                entitiesSlain++;
                if(entitiesSlain >= playerCount*35){
                    spawnRaidBoss(event.getEntity().getWorld());
                }
                entities.remove(event.getEntity());
            }
        }
    }


}
