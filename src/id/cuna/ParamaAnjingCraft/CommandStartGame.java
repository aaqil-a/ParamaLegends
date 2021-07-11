package id.cuna.ParamaAnjingCraft;

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
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CommandStartGame implements CommandExecutor {

    private final ParamaAnjingCraft plugin;
    private final int sceneLength = 400;
    public DataManager data;

    public CommandStartGame(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Build starting area
    public void buildArea(Location location){
        Location placeLocation;


        //Fill blocks below ground layer
        int layers = -1;
        while(layers > -5){
            for(int x = 0; x < 10; x++){
                placeLocation = location.clone().add(x,layers,0);
                for(int z = 0; z < 11; z++){
                    if(placeLocation.getBlock().isPassable()){
                        placeLocation.getBlock().setType(Material.DIRT);
                    }
                    placeLocation.add(0,0,1);
                }
            }
            layers--;
        }

        //Build ground layer
        String[] blockMap = {
                "---C-------",
                "---PCC-CP--",
                "--CPPCCPCC-",
                "---CPCCPP--",
                "---CCCCPC--",
                "--PPPCCPP--",
                "-PCC-PCPP--",
                "--PC---PCC-",
                "--------PC-",
                "-----------"};
        for(int x = 0; x < blockMap.length; x++){
            placeLocation = location.clone().add(x,-1,0);
            for(int z = 0; z < blockMap[x].length(); z++) {
                switch(blockMap[x].charAt(z)){
                    case '-' ->{
                        placeLocation.getBlock().setType(Material.GRASS_BLOCK);
                    }
                    case 'C' -> {
                        placeLocation.getBlock().setType(Material.COARSE_DIRT);
                    }
                    case 'P' -> {
                        placeLocation.getBlock().setType(Material.PODZOL);
                    }
                }
                placeLocation.add(0,0,1);
            }
        }
        //Build first layer
        blockMap = new String[]{
                "-----W--W--",
                "-----------",
                "-----------",
                "--W--------",
                "-----C---W-",
                "-----------",
                "-----------",
                "-W---W-----",
                "---------F-",
                "-----------"};
        for(int x = 0; x < blockMap.length; x++){
             placeLocation = location.clone().add(x,0,0);
            for(int z = 0; z < blockMap[x].length(); z++) {
                switch(blockMap[x].charAt(z)){
                    case '-' ->{
                        placeLocation.getBlock().setType(Material.AIR);
                    }
                    case 'W' -> {
                        placeLocation.getBlock().setType(Material.SPRUCE_LOG);
                    }
                    case 'C' -> {
                        placeLocation.getBlock().setType(Material.CAMPFIRE);
                    }
                    case 'F' -> {
                        placeLocation.getBlock().setType(Material.SPRUCE_FENCE);
                    }
                }
                placeLocation.add(0,0,1);
            }
        }


        //Create barrier around player
        placeLocation = location.clone();
        placeLocation.add(1,0,0).getBlock().setType(Material.BARRIER);
        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
        placeLocation.add(-1,-1,1).getBlock().setType(Material.BARRIER);
        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
        placeLocation.add(-1,-1,-1).getBlock().setType(Material.BARRIER);
        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
        placeLocation.add(1,-1,-1).getBlock().setType(Material.BARRIER);
        placeLocation.add(0,1,0).getBlock().setType(Material.BARRIER);
        location.clone().add(0,2,0).getBlock().setType(Material.BARRIER);
        location.clone().add(0,-1,0).getBlock().setType(Material.BEDROCK);

        placeLocation = location.clone();
        //Create task to remove barrier after scene ends
        Location finalPlaceLocation = placeLocation;
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            finalPlaceLocation.clone().add(0,2,0).getBlock().setType(Material.AIR);
            finalPlaceLocation.clone().add(0,-1,0).getBlock().setType(Material.GRASS_BLOCK);
            finalPlaceLocation.add(1,0,0).getBlock().setType(Material.AIR);
            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
            finalPlaceLocation.add(-1,-1,1).getBlock().setType(Material.AIR);
            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
            finalPlaceLocation.add(-1,-1,-1).getBlock().setType(Material.AIR);
            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
            finalPlaceLocation.add(1,-1,-1).getBlock().setType(Material.AIR);
            finalPlaceLocation.add(0,1,0).getBlock().setType(Material.AIR);
        }, sceneLength);


        //Build 'umbrella' for odd reseller
        placeLocation = location.clone().add(8,1,9);
        placeLocation.getBlock().setType(Material.SPRUCE_FENCE);
        placeLocation.add(0,1,0).getBlock().setType(Material.SPRUCE_PLANKS);
        placeLocation.add(1,0,0).getBlock().setType(Material.SPRUCE_SLAB);
        placeLocation.add(0,0,1).getBlock().setType(Material.SPRUCE_SLAB);
        placeLocation.add(-1,0,0).getBlock().setType(Material.SPRUCE_SLAB);
        placeLocation.add(-1,0,0).getBlock().setType(Material.SPRUCE_SLAB);
        placeLocation.add(0,0,-1).getBlock().setType(Material.SPRUCE_SLAB);
        placeLocation.add(0,0,-1).getBlock().setType(Material.SPRUCE_SLAB);
        placeLocation.add(1,0,0).getBlock().setType(Material.SPRUCE_SLAB);
        placeLocation.add(1,0,0).getBlock().setType(Material.SPRUCE_SLAB);

        //Spawn NPCs at their respective location
        spawnAllNPC(location);

        //Spawn protective crystal
        placeLocation = location.clone().add(-2,0,-2);
        placeLocation.getWorld().spawn(placeLocation, ArmorStand.class, armorStand -> {
            armorStand.setCustomName("§6Void Nullifier");
            armorStand.setCustomNameVisible(true);
            armorStand.setSilent(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setCollidable(false);
            armorStand.setCanPickupItems(false);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.CRYING_OBSIDIAN));
            armorStand.setInvisible(true);
        });

    }

    public void spawnAllNPC(Location location){
        //Create Wise Peculier NPC
        spawnNPC(location.clone().add(2,0,2), Villager.Type.PLAINS, 0, null, "§6Wise Peculier");
        //Create Odd Reseller NPC
        spawnNPC(location.clone().add(7, 0, 8), Villager.Type.TAIGA, 0, null, "§eOdd Reseller");
        //Create Banished Magus NPC
        spawnNPC(location.clone().add(4, 0, 8), Villager.Type.SWAMP, 0, null, "§5Banished Magus");
        //Create Suspicious Peasant NPC
        spawnNPC(location.clone().add(8, 0, 0), Villager.Type.SNOW, 0, null, "§4Suspicious Peasant");
        //Create swordman NPC
        spawnNPC(location.clone().add(1, 0, 5), Villager.Type.SNOW, 5, Villager.Profession.WEAPONSMITH, "§2Retired Weaponsmith");
        //Create archery NPC
        spawnNPC(location.clone().add(6, 0, 5), Villager.Type.PLAINS, 5, Villager.Profession.FLETCHER, "§aAdept Ranger");
    }

    public void spawnNPC(Location location, Villager.Type type, int level, Villager.Profession profession, String name){
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        Villager v = (Villager) mob;

        v.setCustomName(name);
        v.setCustomNameVisible(true);
        v.setVillagerType(type);
        v.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100, false, false ,false));
        v.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100, false, false ,false));
        v.setSilent(true);
        v.getEntityId();
        v.setCollidable(false);
        if(profession != null){
            v.setProfession(profession);
        }
        if(level > 0){
            v.setVillagerLevel(level);
        }
    }

    //Teleport all players to location to begun scene
    public void teleportPlayers(Location location){
        //set direction to face built area
        location.setYaw(-44);
        location.setPitch(2.8f);
        for(Player player : plugin.getServer().getOnlinePlayers()){
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
            Player player = (Player) sender;
            org.bukkit.World world = player.getWorld();
            Location location = player.getLocation();

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


            // Change global zone flags
            if(regions.hasRegion("__global__")) {
                ProtectedRegion global = regions.getRegion("__global__");
                global.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
                global.setFlag(Flags.PLACE_VEHICLE, StateFlag.State.ALLOW);
                global.setFlag(Flags.USE, StateFlag.State.ALLOW);
                global.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
                global.setFlag(Flags.USE_ANVIL, StateFlag.State.ALLOW);
                global.setFlag(Flags.DAMAGE_ANIMALS, StateFlag.State.ALLOW);
                global.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
                global.setFlag(Flags.RIDE, StateFlag.State.ALLOW);
                global.setFlag(Flags.PVP, StateFlag.State.ALLOW);
                global.setFlag(Flags.SLEEP, StateFlag.State.ALLOW);
                global.setFlag(Flags.RESPAWN_ANCHORS, StateFlag.State.ALLOW);
                global.setFlag(Flags.TNT, StateFlag.State.ALLOW);
                global.setFlag(Flags.DESTROY_VEHICLE, StateFlag.State.ALLOW);
                global.setFlag(Flags.LIGHTER, StateFlag.State.ALLOW);
                global.setFlag(Flags.TRAMPLE_BLOCKS, StateFlag.State.ALLOW);
                global.setFlag(Flags.FROSTED_ICE_FORM, StateFlag.State.ALLOW);
                global.setFlag(Flags.ITEM_FRAME_ROTATE, StateFlag.State.ALLOW);
                global.setFlag(Flags.FIREWORK_DAMAGE, StateFlag.State.ALLOW);
                global.setFlag(Flags.DENY_MESSAGE, ChatColor.DARK_RED+"It is impossible to reshape the wilderness.");
                global.setFlag(Flags.GREET_MESSAGE, ChatColor.RED + "The wilderness exudes a threatening aura...");

            } else {
                player.sendMessage(ChatColor.RED+"Setup failed. Ensure global region exists by typing command '/rg flag __global__ passthrough deny'");
                return false;
            }

            //create safe zone
            BlockVector3 min = BlockVector3.at(locationX-size, 30, locationZ-size);
            BlockVector3 max = BlockVector3.at(locationX+size, 256, locationZ+size);
            ProtectedRegion region = new ProtectedCuboidRegion("safezone", min, max);
            region.setFlag(Flags.GREET_MESSAGE, ChatColor.GREEN + "You feel slightly more protected...");
            region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
            region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
            regions.addRegion(region);

            //Create depths zone
            min = BlockVector3.at(locationX-size, 0, locationZ-size);
            max = BlockVector3.at(locationX+size, 29, locationZ+size);
            ProtectedRegion depths = new ProtectedCuboidRegion("depths", min, max);
            depths.setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
            depths.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
            depths.setFlag(Flags.BLOCK_PLACE, StateFlag.State.ALLOW);
            depths.setFlag(Flags.ENTRY, StateFlag.State.ALLOW);
            depths.setFlag(Flags.DENY_MESSAGE, ChatColor.DARK_RED+"The earth at this depth is much too dense.");

            regions.addRegion(depths);

            buildArea(location);
            teleportPlayers(location);
            playScene();

            data.saveConfig();

            return true;
        }
        return false;
    }


}
