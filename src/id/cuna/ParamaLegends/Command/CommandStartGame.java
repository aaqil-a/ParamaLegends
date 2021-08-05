package id.cuna.ParamaLegends.Command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class CommandStartGame implements CommandExecutor {

    private final ParamaLegends plugin;
    private final int sceneLength = 400;
    public DataManager data;

    public CommandStartGame(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public Material randomTempleBlock(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        return switch(x){
            case 2 -> Material.CRACKED_STONE_BRICKS;
            case 3 -> Material.MOSSY_STONE_BRICKS;
            case 4 -> Material.DEEPSLATE_BRICKS;
            default -> Material.STONE_BRICKS;
        };
    }

    public void buildPillar(Location location){
        location.getBlock().setType(randomTempleBlock());
        location.add(0,1,0).getBlock().setType(randomTempleBlock());
        location.add(0,1,0).getBlock().setType(randomTempleBlock());
        location.add(0,1,0).getBlock().setType(Material.TORCH);
    }

    //Build starting temple
    public void buildTemple(Location location){
        org.bukkit.World world = location.getWorld();
        Location placeLocation;
        Random rand = new Random();
        //create ground level temple blocks
        for(int x = 0; x < 15; x++){
            placeLocation = location.clone().add(x,0,0);
            for(int z = 0; z < 15; z++){
                if((x < 3) || (z < 3) || (x > 11) || (z > 11)) {
                    int temp = rand.nextInt(2);
                    if (temp == 0){
                        placeLocation.add(0,0,1);
                        continue;
                    }
                }
                world.getHighestBlockAt(placeLocation).setType(randomTempleBlock());
                placeLocation.add(0,0,1);
            }
        }

        //create four pillars above ground
        placeLocation = world.getHighestBlockAt(location.clone().add(4,0,4))
                .getLocation().add(0,1,0);
        buildPillar(placeLocation);
        placeLocation = world.getHighestBlockAt(location.clone().add(10,0,4))
                .getLocation().add(0,1,0);
        buildPillar(placeLocation);
        placeLocation = world.getHighestBlockAt(location.clone().add(4,0,10))
                .getLocation().add(0,1,0);
        buildPillar(placeLocation);
        placeLocation = world.getHighestBlockAt(location.clone().add(10,0,10))
                .getLocation().add(0,1,0);
        buildPillar(placeLocation);

        //create vertical tunnel
        placeLocation = world.getHighestBlockAt(location.clone().add(7,0,7)).getLocation();
        for(int y = 0; y > -10; y--){
            placeLocation.clone().add(0,y,0).getBlock().setType(Material.AIR);
            placeLocation.clone().add(1,y,0).getBlock().setType(randomTempleBlock());
            placeLocation.clone().add(-1,y,0).getBlock().setType(randomTempleBlock());
            placeLocation.clone().add(0,y,1).getBlock().setType(randomTempleBlock());
            placeLocation.clone().add(0,y,-1).getBlock().setType(randomTempleBlock());
        }
        Location waterLocation = placeLocation.clone();
        placeLocation.getBlock().setType(Material.WATER);

        //Fill blocks of temple room
        int layers = -7;
        while(layers > -12){
            for(int x = 0; x < 9; x++){
                placeLocation = waterLocation.clone().add(x-4,layers,-4);
                for(int z = 0; z < 9; z++){
                    if((layers != -7) && (layers != -11) && (x != 0) && (x != 8) && (z != 0) && (z != 8))
                        placeLocation.getBlock().setType(Material.AIR);
                    else placeLocation.getBlock().setType(randomTempleBlock());
                    placeLocation.add(0,0,1);
                }
            }
            layers--;
        }
        waterLocation.clone().add(0,-7,0).getBlock().setType(Material.AIR);
        waterLocation.clone().add(0,-11,0).getBlock().setType(Material.AIR);

        //create armor stands
        spawnAllArmorStand(waterLocation);


//        //Create barrier around player
//        placeLocation = location.clone();
//        placeLocation.add(1,0,0).getBlock().setType(Material.BARRIER);
//        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
//        placeLocation.add(-1,-1,1).getBlock().setType(Material.BARRIER);
//        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
//        placeLocation.add(-1,-1,-1).getBlock().setType(Material.BARRIER);
//        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
//        placeLocation.add(1,-1,-1).getBlock().setType(Material.BARRIER);
//        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
//        location.clone().add(0,2,0).getBlock().setType(Material.BARRIER);
//        location.clone().add(0,-1,0).getBlock().setType(Material.BEDROCK);
//
//        placeLocation = location.clone();
//        //Create task to remove barrier after scene ends
//        Location finalPlaceLocation = placeLocation;
//        Bukkit.getScheduler().runTaskLater(plugin, ()->{
//            finalPlaceLocation.clone().add(0,2,0).getBlock().setType(Material.AIR);
//            finalPlaceLocation.clone().add(0,-1,0).getBlock().setType(Material.GRASS_BLOCK);
//            finalPlaceLocation.add(1,0,0).getBlock().setType(Material.AIR);
//            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
//            finalPlaceLocation.add(-1,-1,1).getBlock().setType(Material.AIR);
//            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
//            finalPlaceLocation.add(-1,-1,-1).getBlock().setType(Material.AIR);
//            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
//            finalPlaceLocation.add(1,-1,-1).getBlock().setType(Material.AIR);
//            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
//        }, sceneLength);


    }

    public void spawnAllArmorStand(Location location){
        //Create Destiny
        spawnArmorStand(location.clone().add(3.5,2,0.5), Material.END_PORTAL_FRAME,"§6Your Destiny");
        //Create magic
        spawnArmorStand(location.clone().add(3.5, -11, 0.5), Material.ENCHANTING_TABLE,"§5Ancient Tomes");
        //Create reaper
        spawnArmorStand(location.clone().add(-2, -11, 0.5),Material.GRINDSTONE,"§4Reaper Grindstone");
        //Create swordman
        spawnArmorStand(location.clone().add(0.5, -11, 3.5), Material.ANVIL,"§2Dull Anvil");
        //Create archery
        spawnArmorStand(location.clone().add(0.5, -11, -2), Material.FLETCHING_TABLE,"§aFletcher's Table");
    }

    public void spawnArmorStand(Location location, Material head, String name){
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        ArmorStand v = (ArmorStand) mob;

        v.setCustomName(name);
        v.setCustomNameVisible(true);
        v.setSilent(true);
        v.setGravity(false);
        v.setInvisible(true);
        v.setInvulnerable(true);
        v.setCollidable(false);
        v.getEquipment().setHelmet(new ItemStack(head));
    }

    //Teleport all players to location to begun scene
    public void teleportPlayers(Location location){
        //set direction to face built area
        location.setYaw(-44);
        location.setPitch(2.8f);
        for(Player player : plugin.getServer().getOnlinePlayers()){
            player.teleport(location);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 15, true, false, false)); //blindness effect
            player.setInvisible(true); // make players invisible
        }
    }

    //Play starting scene
    public void playScene() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(ChatColor.GOLD+"Wise Peculier: "+ChatColor.WHITE+"wake up "+player.getName()+" idk insert dialogue here i guess");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.setInvisible(false);
            }, sceneLength); //scene finished, remove effects
        }
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int size;
        if (sender instanceof Player){
            if(args.length == 0){
                size = 50;
            } else {
                size = Integer.getInteger(args[0]);
            }
            if(data.getConfig().getInt("world.level") == 0) {
                sender.sendMessage(ChatColor.RED+"Game is not setup. Use /setupgame to begin game setup.");
                return true;
            } else if(data.getConfig().getBoolean("world.started")) {
                sender.sendMessage(ChatColor.RED+"Game has already started. Remove plugin config.yml file to restart game.");
                return true;
            }

            Player player = (Player) sender;
            org.bukkit.World world = player.getWorld();

            List<Integer> startLocation = data.getConfig().getIntegerList("world.startlocation");
            Location location = new Location(player.getWorld(), startLocation.get(0), startLocation.get(1)+1, startLocation.get(2));

            //set world spawn point
            world.setSpawnLocation(location);

            // Create safe zone region
            World worldGuard = BukkitAdapter.adapt(player.getWorld());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(worldGuard);

            double locationX = location.getX();
            double locationY = location.getY();
            double locationZ = location.getZ();

            data.getConfig().set("world.startX", locationX);
            data.getConfig().set("world.startY", locationY);
            data.getConfig().set("world.startZ", locationZ);
            data.getConfig().set("world.startSize", size);

            data.getConfig().set("world.level", 1);
            data.getConfig().set("world.started", true);

            // Change global zone flags
            if(regions.hasRegion("__global__")) {
                ProtectedRegion global = regions.getRegion("__global__");
                global.setFlag(Flags.HEALTH_REGEN, StateFlag.State.DENY);
                global.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
            } else {
                player.sendMessage(ChatColor.RED+"Setup failed. Ensure global region exists by typing command '/rg flag __global__ natural-health-regen deny'");
                return true;
            }

            //create safe zone
            BlockVector3 min = BlockVector3.at(locationX-size, 0, locationZ-size);
            BlockVector3 max = BlockVector3.at(locationX+size, 256, locationZ+size);
            ProtectedRegion region = new ProtectedCuboidRegion("safezone", min, max);
            region.setPriority(1);
            region.setFlag(Flags.GREET_MESSAGE, ChatColor.GREEN + "You feel slightly more protected...");
            region.setFlag(Flags.FAREWELL_MESSAGE, ChatColor.RED + "The wilderness exudes a threatening aura...");
            region.setFlag(Flags.HEALTH_REGEN, StateFlag.State.ALLOW);
            region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
            regions.addRegion(region);

            buildTemple(location);
            teleportPlayers(location);
            playScene();

            data.saveConfig();
            return true;
        }
        return false;
    }


}
