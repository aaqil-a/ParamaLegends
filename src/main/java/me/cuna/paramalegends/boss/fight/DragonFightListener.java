package me.cuna.paramalegends.boss.fight;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEnderDragon;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class DragonFightListener implements Listener {

    private final ParamaLegends plugin;
    private final HashMap<Player, Integer> damageDealt = new HashMap<>();
    private final HashMap<Player, Integer> kills = new HashMap<>();
    private final HashMap<Player, Integer> damageTaken = new HashMap<>();
    private final Random rand = new Random();
    private int crystalCount;
    List<Player> alive = new ArrayList<>();



    public DragonFightListener(final ParamaLegends plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        crystalCount = 0;
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
            plugin.experienceListener.setWorldLevel(4);
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
                for(Player player : dragon.getWorld().getPlayers()){
                    player.sendMessage(ChatColor.DARK_PURPLE+"End crystals seem to fuel the dragon's reign.");
                }
                crystalCount = 10;
                alive.clear();
                alive.addAll(Bukkit.getOnlinePlayers());
                return;
            }
            switch(event.getNewPhase()){
                //stun player being charged
                case CHARGE_PLAYER -> {
                    EntityLiving playerCraft = ((CraftEnderDragon) dragon).getHandle().G();
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
                                player.getWorld().strikeLightningEffect(player.getLocation());
                                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 1));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
                            }
                        }
                    }
                }
                //summon enderman to charge players
                case FLY_TO_PORTAL, STRAFING -> {
                    for(Player player : event.getEntity().getWorld().getPlayers()){
                        Location spawnLocation = player.getWorld().getHighestBlockAt(player.getLocation().add(10,0,10)).getLocation();
                        player.getWorld().spawn(spawnLocation, Enderman.class, enderman -> {
                            enderman.setTarget(player);
                        });
                        player.sendMessage(ChatColor.DARK_PURPLE+"The dragon commands her followers to her aid.");
                    }
                }
                //make playesrs levitate
                case HOVER -> {
                    for(Player player : Bukkit.getOnlinePlayers()){
                        if(player.getWorld().equals(dragon.getWorld())){
                            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
                            player.sendMessage(ChatColor.DARK_PURPLE+"The dragon levitates you to the air, preparing to slam you down.");
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
                            player.sendMessage(ChatColor.DARK_PURPLE+"The dragon's mighty roar blinds everyone who dares defy her.");
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
                        player.sendMessage(ChatColor.DARK_PURPLE+"The dragon's descent paralyzes all who are near.");
                    }
                }
            }
        } else {
            // world is empty, remove metadata
            if(dragon.hasMetadata("BUFFED")) dragon.removeMetadata("BUFFED", plugin);
            alive.clear();
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
                // send message
                for(Player player : event.getEntity().getWorld().getPlayers()){
                    if(rand.nextInt(2) == 0){
                        player.sendMessage(ChatColor.DARK_PURPLE+"The dragon summons a crystal to her aid.");
                    } else {
                        player.sendMessage(ChatColor.DARK_PURPLE+"Another crystal has come to aid the dragon.");
                    }
                }
                if(crystalCount<10)crystalCount++;
                if (crystalCount == 6) {
                    for (Player player : event.getEntity().getWorld().getPlayers()) {
                        player.sendMessage(ChatColor.DARK_PURPLE + "The dragon has regained her full might. Your attacks wouldn't suffice even a tiny bit.");
                    }
                }
                event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 4f, 1f);
            }, 800);
            if(crystalCount>0)crystalCount--;
            switch(crystalCount) {
                case 6 -> {
                    for(Player player : event.getEntity().getWorld().getPlayers()){
                        player.sendMessage(ChatColor.DARK_PURPLE+"Her shield has diminished, yet the crystals still fuels her might.");
                    }
                }
                case 4 -> {
                    for(Player player : event.getEntity().getWorld().getPlayers()){
                        player.sendMessage(ChatColor.DARK_PURPLE+"The crystals seem to be unable to heal the dragon, though it still gives her strength.");
                    }
                }
                case 2 -> {
                    for(Player player : event.getEntity().getWorld().getPlayers()){
                        player.sendMessage(ChatColor.DARK_PURPLE+"You sense the dragon getting weaker.");
                    }
                }
                case 1 -> {
                    for(Player player : event.getEntity().getWorld().getPlayers()){
                        player.sendMessage(ChatColor.DARK_PURPLE+"The crystals could barely give her any strength, you sense her staggering and distressed.");
                    }
                }
            }
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
        event.setDamage(damage);
    }

    @EventHandler
    public void onPlayerDamagedByDragon(EntityDamageByEntityEvent event){
        if(event.getEntityType().equals(EntityType.PLAYER) && event.getDamager().getType().equals(EntityType.ENDER_DRAGON)){
            double damage = event.getDamage();
            EnderDragon dragon = (EnderDragon) event.getDamager();
            if(dragon.getPhase().equals(EnderDragon.Phase.CHARGE_PLAYER)){
                damage += 20;
            }
            if(crystalCount > 4) damage += 5;
            damage += 15;
            event.setDamage(damage);
        }
    }

    @EventHandler
    public void onDragonDamaged(EntityDamageByEntityEvent event){
        if(event.getEntityType().equals(EntityType.ENDER_DRAGON)){
            if(crystalCount > 6) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDragonHeal(EntityRegainHealthEvent event) {
        if(event.getEntityType().equals(EntityType.ENDER_DRAGON)){
            event.setCancelled(true);
        }
    }

    public void loseFight(){
        alive.clear();
        //send statistics
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
        for(Player player : plugin.getServer().getOnlinePlayers()){
            if(kills.get(player) == null) player.sendMessage(ChatColor.GREEN+"Your Kills: 0");
            else player.sendMessage(ChatColor.GREEN+"Your Kills: "+kills.get(player));
            if(damageDealt.get(player) == null) player.sendMessage(ChatColor.GREEN+"Your Damage Dealt: 0");
            else player.sendMessage(ChatColor.GREEN+"Your Damage Dealt: "+damageDealt.get(player));
            if(damageTaken.get(player) == null) player.sendMessage(ChatColor.RED+"Your Damage Taken: 0");
            else player.sendMessage(ChatColor.RED+"Your Damage Taken: "+damageTaken.get(player));
            player.teleport(new Location(player.getServer().getWorld("world"), 329, 64, -158));
            player.setGameMode(GameMode.SURVIVAL);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player =  event.getEntity();
        if(alive.contains(player)){
            player.sendMessage(ChatColor.RED+"You died. Wait for fight to end to respawn.");
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(alive.remove(event.getPlayer())){
            if(alive.isEmpty()) {
                loseFight();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(alive.contains(event.getPlayer())){
            alive.remove(event.getPlayer());
            event.setRespawnLocation(new Location(event.getPlayer().getWorld(), 100, 49, 0));
            if(alive.isEmpty()) {
                loseFight();
            }
        }
    }
}
