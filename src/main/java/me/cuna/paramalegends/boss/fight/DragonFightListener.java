package me.cuna.paramalegends.boss.fight;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEnderDragon;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DragonFightListener implements Listener {

    private final ParamaLegends plugin;
    private final HashMap<Player, Integer> damageDealt = new HashMap<>();
    private final HashMap<Player, Integer> kills = new HashMap<>();
    private final HashMap<Player, Integer> deaths = new HashMap<>();
    private final HashMap<Player, Integer> damageTaken = new HashMap<>();


    public DragonFightListener(final ParamaLegends plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if(event.getEntity().getType().equals(EntityType.ENDER_DRAGON)){
            if(event.getEntity().getKiller()!=null) {
                Player player = event.getEntity().getKiller();
                if(kills.containsKey(player)){
                    kills.put(player, kills.get(player)+1);
                } else {
                    kills.put(player, 1);
                }
            }
            //send statistics
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
            for(Player player : plugin.getServer().getOnlinePlayers()){
                if(kills.get(player) == null) player.sendMessage(ChatColor.GREEN+"Your Kills: 0");
                else player.sendMessage(ChatColor.GREEN+"Your Kills: "+kills.get(player));
                if(deaths.get(player) == null) Bukkit.broadcastMessage(ChatColor.RED+"Your Deaths: Nobody");
                else Bukkit.broadcastMessage(ChatColor.RED+"Your Deaths: "+deaths.get(player));
                if(damageDealt.get(player) == null) player.sendMessage(ChatColor.GREEN+"Your Damage Dealt: 0");
                else player.sendMessage(ChatColor.GREEN+"Your Damage Dealt: "+damageDealt.get(player));
                if(damageTaken.get(player) == null) player.sendMessage(ChatColor.RED+"Your Damage Taken: 0");
                else player.sendMessage(ChatColor.RED+"Your Damage Taken: "+damageTaken.get(player));
            }
            for(Player player : plugin.getServer().getOnlinePlayers()){
                //give player rewards
                PlayerParama playerParama = plugin.getPlayerParama(player);
                playerParama.addLectrum(2000);
                player.sendMessage(ChatColor.GOLD+"+2000 Lectrum");
            }
        } else if(event.getEntity().getType().equals(EntityType.ENDERMAN)){
            if(event.getEntity().getKiller()!=null) {
                Player player = event.getEntity().getKiller();
                if(kills.containsKey(player)){
                    kills.put(player, kills.get(player)+1);
                } else {
                    kills.put(player, 1);
                }
            }
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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (damageTaken.containsKey(player)) {
                damageTaken.put(player, damageTaken.get(player) + (int) event.getDamage());
            } else {
                damageTaken.put(player, (int) event.getDamage());
            }
        } else if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (damageDealt.containsKey(player)) {
                damageDealt.put(player, damageDealt.get(player) + (int) event.getDamage());
            } else {
                damageDealt.put(player, (int) event.getDamage());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(deaths.containsKey(player)){
            deaths.put(player, deaths.get(player)+1);
        } else {
            deaths.put(player, 1);
        }
    }

    @EventHandler
    public void onDragonChangePhase(EnderDragonChangePhaseEvent event){
        EnderDragon dragon = event.getEntity();
        if(!dragon.getWorld().getPlayers().isEmpty()){
            //first time
            if(!dragon.hasMetadata("BUFFED")){
                dragon.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(1.4);
                dragon.setMetadata("BUFFED", new FixedMetadataValue(plugin, "BUFFED"));
                damageDealt.clear();
                damageTaken.clear();
                kills.clear();
                deaths.clear();
            }
            switch(event.getNewPhase()){
                //stun player being charged
                case CHARGE_PLAYER -> {
                    EntityLiving playerCraft = ((CraftEnderDragon) dragon).getHandle().getGoalTarget();
                    Player player = (Player) playerCraft;
                    if(player != null){
                        PlayerParama playerParama = plugin.getPlayerParama(player);
                        playerParama.setSilenced(true);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 10));
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                            playerParama.setSilenced(false);
                        }, 60);
                    }
                }
                //randomly strike players with lightning when circling
                case CIRCLING -> {
                    Random rand = new Random();
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getWorld().equals(dragon.getWorld())){
                            if(rand.nextInt(3)==0){
                                player.damage(30, dragon);
                                player.getWorld().strikeLightningEffect(player.getLocation());
                            }
                        }
                    }
                }
                //summon enderman to charge players
                case FLY_TO_PORTAL, STRAFING -> {
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getWorld().equals(dragon.getWorld())){
                            Location spawnLocation = player.getWorld().getHighestBlockAt(player.getLocation().add(10,0,10)).getLocation();
                            player.getWorld().spawn(spawnLocation, Enderman.class, enderman -> {
                                enderman.setTarget(player);
                            });
                        }
                    }
                }
                //make playesrs levitate
                case HOVER -> {
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getWorld().equals(dragon.getWorld())){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
                        }
                    }
                }
                //blind nearby players after roarign
                case ROAR_BEFORE_ATTACK -> {
                    for( Player player : Bukkit.getOnlinePlayers()){
                        if(player.getWorld().equals(dragon.getWorld())){
                            if(player.getLocation().distance(dragon.getLocation()) < 30){
                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 5));
                            }
                        }

                    }
                }
            }
            //slow and blind players upon landing
            if(Objects.equals(event.getCurrentPhase(), EnderDragon.Phase.LAND_ON_PORTAL)){
                for( Player player : Bukkit.getOnlinePlayers()){
                    if(player.getWorld().equals(dragon.getWorld())){
                        if(player.getLocation().distance(dragon.getLocation()) < 10){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 10));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 4));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCrystalDestroyed(EntityDamageEvent event){
        if(event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL)){
            //launch nearby players
            EnderCrystal crystal = (EnderCrystal) event.getEntity();
            List<Entity> nearby = crystal.getNearbyEntities(10,10,10);
            nearby.removeIf(hit -> !(hit instanceof Player));
            for(Entity entity : nearby){
                entity.setVelocity(entity.getLocation().toVector().subtract(crystal.getLocation().toVector()).setY(0).normalize().multiply(5));
            }
            //respawn crystal after 20 seconds
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                World world = event.getEntity().getWorld();
                world.spawn(event.getEntity().getLocation(), EnderCrystal.class);
                //spawn iron bar sides
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++){
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH).getLocation().add(0,y,0).getBlock().setType(Material.IRON_BARS);
                }
                for(int y=-1;y<2;y++) {
                    event.getEntity().getLocation().getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH).getLocation().add(0, y, 0).getBlock().setType(Material.IRON_BARS);
                }
                //spawn roof
                for(int x = -2; x<3; x++){
                    for(int z=-2;z<3;z++){
                        event.getEntity().getLocation().add(x,2,z).getBlock().setType(Material.IRON_BARS);
                    }
                }
            }, 400);
        }
    }
    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event){
        double damage = event.getDamage();
        //increase damage from dragon breath
        if(event.getCause().equals(EntityDamageEvent.DamageCause.DRAGON_BREATH) && event.getEntity().getType().equals(EntityType.PLAYER)){
            Player player = (Player) event.getEntity();
            damage = 10;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 1));
        }
        if(event.getEntityType().equals(EntityType.ENDER_DRAGON)){
            EnderDragon dragon = (EnderDragon) event.getEntity();
            //increase damage while charging
            if(dragon.getPhase().equals(EnderDragon.Phase.CHARGE_PLAYER)){
                damage += 20;
            }
        }
        //increase dragon damage
        damage += 15;
        event.setDamage(damage);
    }
}
