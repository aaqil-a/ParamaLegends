package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RaidListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;

    public RaidListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public void spawnZombie(Location location, LivingEntity target){
        location.getWorld().spawn(location, Zombie.class, zombie -> {
            zombie.setTarget(target);
            zombie.setRemoveWhenFarAway(false);
        });
    }

    public Location highestBlockAt(Location location){
        return location.getWorld().getHighestBlockAt(location).getLocation().clone();
    }

    public void raidLevel1(LivingEntity target, Location location){
        double worldSize = data.getConfig().getDouble("world.startSize");
        spawnZombie(highestBlockAt(location).add(-worldSize, 0, 0), target);
    }

    public void startRaid(Player player){
        Entity target = null;
        double x = data.getConfig().getDouble("world.startX");
        double y = data.getConfig().getDouble("world.startY");
        double z = data.getConfig().getDouble("world.startZ");
        Location location = new Location(player.getWorld(), x, y, z);
        for(Entity entity : player.getWorld().getNearbyEntities(location, 5,5,5)){
            if(entity.getCustomName() != null && entity.getCustomName().equals("§6Void Nullifier")){
                target = entity;
                break;
            }
        }
        if(target != null){
            Bukkit.broadcastMessage("raid begun, target foudn at " + target.getLocation().toString());
            switch (data.getConfig().getInt("world.level")){
                case 1 -> raidLevel1( (LivingEntity) target, location);
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.DARK_RED+"Error. Void Nullifier not found.");
        }

    }

}
