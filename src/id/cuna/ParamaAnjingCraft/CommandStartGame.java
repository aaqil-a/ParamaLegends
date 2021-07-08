package id.cuna.ParamaAnjingCraft;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.loot.LootTable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class CommandStartGame implements CommandExecutor {

    private final ParamaAnjingCraft plugin;

    public CommandStartGame(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            org.bukkit.World world = player.getWorld();
            Location location = player.getLocation();


            //Create Wise Peculier NPC
            LivingEntity mob = (LivingEntity)player.getWorld().spawnEntity(location, EntityType.VILLAGER);
            Villager v = (Villager) mob;

            v.setCustomName("§6Wise Peculier");
            v.setCustomNameVisible(true);
            v.setVillagerType(Villager.Type.PLAINS);
            v.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            v.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100));

            //Create Odd Reseller NPC
            LivingEntity mob2 = (LivingEntity)player.getWorld().spawnEntity(location.clone().add(2, 0, 2), EntityType.VILLAGER);
            Villager shop = (Villager) mob2;

            shop.setCustomName("§eOdd Reseller");
            shop.setCustomNameVisible(true);
            shop.setVillagerType(Villager.Type.TAIGA);
            shop.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            shop.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100));

            //Create Banished Magus NPC
            LivingEntity mob3 = (LivingEntity)player.getWorld().spawnEntity(location.clone().add(-2, 0, 2), EntityType.VILLAGER);
            Villager mage = (Villager) mob3;

            mage.setCustomName("§5Banished Magus");
            mage.setCustomNameVisible(true);
            mage.setVillagerType(Villager.Type.SWAMP);
            mage.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            mage.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100));

            //Create SusNPC NPC
            LivingEntity mob4 = (LivingEntity)player.getWorld().spawnEntity(location.clone().add(2, 0, -2), EntityType.VILLAGER);
            Villager sus = (Villager) mob4;

            sus.setCustomName("§4Suspicious Peasant");
            sus.setCustomNameVisible(true);
            sus.setVillagerType(Villager.Type.SNOW);
            sus.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            sus.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100));

            //Create swordman NPC
            LivingEntity mob5 = (LivingEntity)player.getWorld().spawnEntity(location.clone().add(-2, 0, -2), EntityType.VILLAGER);
            Villager swordman = (Villager) mob5;

            swordman.setCustomName("§2Retired Weaponsmith");
            swordman.setCustomNameVisible(true);
            swordman.setVillagerType(Villager.Type.SNOW);
            swordman.setVillagerLevel(5);
            swordman.setProfession(Villager.Profession.WEAPONSMITH);
            swordman.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            swordman.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100));

            //Create archery NPC
            LivingEntity mob6 = (LivingEntity)player.getWorld().spawnEntity(location.clone().add(-4, 0, -2), EntityType.VILLAGER);
            Villager archer = (Villager) mob6;

            archer.setCustomName("§aAdept Ranger");
            archer.setCustomNameVisible(true);
            archer.setVillagerType(Villager.Type.PLAINS);
            archer.setVillagerLevel(5);
            archer.setProfession(Villager.Profession.FLETCHER);
            archer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            archer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 100));

            // Create safe zone region
            World worldGuard = BukkitAdapter.adapt(player.getWorld());
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(worldGuard);

            double locationX = location.getX();
            double locationZ = location.getZ();

            BlockVector3 min = BlockVector3.at(locationX-20, 0, locationZ-20);
            BlockVector3 max = BlockVector3.at(locationX+20, 256, locationZ+20);
            ProtectedRegion region = new ProtectedCuboidRegion("safezone", min, max);
            region.setFlag(Flags.GREET_MESSAGE, ChatColor.GREEN + "You feel slightly more protected...");
            region.setFlag(Flags.FAREWELL_MESSAGE, ChatColor.RED + "The wilderness exudes a threatening aura...");
            region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
            region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
            regions.addRegion(region);


            // Change global zone flags
            ProtectedRegion global = regions.getRegion("__global__");
            global.setFlag(Flags.PASSTHROUGH, StateFlag.State.DENY);
            global.setFlag(Flags.DENY_MESSAGE, "");
            global.setFlag(Flags.HEALTH_REGEN, StateFlag.State.DENY);

            return true;
        }
        return false;
    }


}
