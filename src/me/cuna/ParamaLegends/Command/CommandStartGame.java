package me.cuna.ParamaLegends.Command;

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
import me.cuna.ParamaLegends.DataManager;
import me.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
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

    public Location getHighestNonTreeLocation(Location location){
        switch(location.getBlock().getType()){
            case ACACIA_LEAVES, AZALEA_LEAVES, BIRCH_LEAVES, DARK_OAK_LEAVES,
                    FLOWERING_AZALEA_LEAVES, JUNGLE_LEAVES, OAK_LEAVES, SPRUCE_LEAVES, MUSHROOM_STEM,
                    BROWN_MUSHROOM_BLOCK, RED_MUSHROOM_BLOCK, ACACIA_LOG, BIRCH_LOG, OAK_LOG, DARK_OAK_LOG,
                    JUNGLE_LOG, SPRUCE_LOG -> {
                return getHighestNonTreeLocation(location.add(0,-1,0));
            }
        }
        if(location.getBlock().isPassable() && !location.getBlock().isLiquid()) return getHighestNonTreeLocation(location.add(0,-1,0));
        return location;
    }

    //Build starting temple
    public void buildTemple(Location location){
        org.bukkit.World world = location.getWorld();
        Random rand = new Random();

        location.add(10,0,10);
        Location placeLocation;
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
                getHighestNonTreeLocation(world.getHighestBlockAt(placeLocation).getLocation()).getBlock().setType(randomTempleBlock());
                placeLocation.add(0,0,1);
            }
        }

        //create four pillars above ground
        placeLocation = getHighestNonTreeLocation(location.clone().add(4,0,4)).add(0,1,0);
        buildPillar(placeLocation);
        placeLocation = getHighestNonTreeLocation(location.clone().add(10,0,4)).add(0,1,0);
        buildPillar(placeLocation);
        placeLocation = getHighestNonTreeLocation(location.clone().add(4,0,10)).add(0,1,0);
        buildPillar(placeLocation);
        placeLocation = getHighestNonTreeLocation(location.clone().add(10,0,10)).add(0,1,0);
        buildPillar(placeLocation);

        //create vertical tunnel
        placeLocation = getHighestNonTreeLocation(world.getHighestBlockAt(location.clone().add(7,0,7)).getLocation());
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


    }

    public void spawnAllArmorStand(Location location){
        //Create Destiny
        Location destinyLocation = getHighestNonTreeLocation(location.getWorld().getHighestBlockAt(location.clone().add(3.5,0,0.5)).getLocation());
        spawnArmorStand(destinyLocation.clone().add(0.5, 0, 0.5), Material.END_PORTAL_FRAME,"§6Your Destiny").setCustomNameVisible(true);
        //Create shop
        destinyLocation = getHighestNonTreeLocation(location.getWorld().getHighestBlockAt(location.clone().add(-2.5,0,0.5)).getLocation());
        spawnArmorStand(destinyLocation.clone().add(0.5, 0, 0.5), Material.CHEST,"§6Odd Wares").setCustomNameVisible(true);
        //Create magic
        spawnArmorStand(location.clone().add(3.5, -11.3, 0.5), Material.ENCHANTING_TABLE,"§5Ancient Tomes");
        //Create reaper
        spawnArmorStand(location.clone().add(-2.5, -11.3, 0.5),Material.GRINDSTONE,"§4Reaper Grindstone");
        //Create swordman
        spawnArmorStand(location.clone().add(0.5, -11.3, 3.5), Material.ANVIL,"§2Dull Anvil");
        //Create archery
        spawnArmorStand(location.clone().add(0.5, -11.3, -2.5), Material.FLETCHING_TABLE,"§aFletcher's Table");
    }

    public ArmorStand spawnArmorStand(Location location, Material head, String name){
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        ArmorStand v = (ArmorStand) mob;

        v.setCustomName(name);
        v.setSilent(true);
        v.setGravity(false);
        v.setInvisible(true);
        v.setInvulnerable(true);
        v.setCanPickupItems(false);
        v.setCollidable(false);
        v.getEquipment().setHelmet(new ItemStack(head));
        return v;
    }

    //Teleport all players to location to begun scene
    public void teleportPlayers(Location location){
        //set direction to face built area
        location.setYaw(-44);
        location.setPitch(2.8f);
        for(Player player : plugin.getServer().getOnlinePlayers()){
            player.teleport(location);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 15, true, false, false)); //blindness effect
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

            // Get worldguard regions
            World worldGuard = BukkitAdapter.adapt(player.getWorld());
            World nether = BukkitAdapter.adapt(Bukkit.getServer().getWorld("world_nether"));


            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(worldGuard);
            RegionManager netherRegions = container.get(nether);

            if(!regions.hasRegion("__global__")){
                player.sendMessage(ChatColor.RED+"Setup failed. Ensure global region exists by typing command '/rg flag __global__ natural-health-regen deny'");
                return true;
            }
            if(!netherRegions.hasRegion("__global__")){
                player.sendMessage(ChatColor.RED+"Setup failed. Ensure nether global region exists by typing command '/rg flag __global__ natural-health-regen deny'");
                return true;
            }

            org.bukkit.World world = player.getWorld();

            List<Integer> startLocation = data.getConfig().getIntegerList("world.startlocation");
            Location location = new Location(player.getWorld(), startLocation.get(0), startLocation.get(1)+1, startLocation.get(2));

            //set world spawn point
            world.setSpawnLocation(location);


            double locationX = location.getX();
            double locationY = location.getY();
            double locationZ = location.getZ();

            data.getConfig().set("world.startX", locationX);
            data.getConfig().set("world.startY", locationY);
            data.getConfig().set("world.startZ", locationZ);
            data.getConfig().set("world.startSize", size);
            data.getConfig().set("world.expanseFund", 400);
            data.getConfig().set("world.expanseLevel", 0);

            data.getConfig().set("world.level", 1);
            data.getConfig().set("world.started", true);

            // Change global zone flags
            ProtectedRegion global = regions.getRegion("__global__");
            global.setFlag(Flags.HEALTH_REGEN, StateFlag.State.DENY);
            global.setFlag(Flags.BUILD, StateFlag.State.ALLOW);


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


            teleportPlayers(world.getHighestBlockAt(location).getLocation().add(0,1,0));
            buildTemple(location);

            data.saveConfig();
            return true;
        }
        return false;
    }


}
