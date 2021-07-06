package id.cuna.ParamaAnjingCraft;

import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.Random;

public class SwordsmanListener implements Listener{
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    private final HashMap<Player, Integer> playerSwordsmanLevel = new HashMap<Player, Integer>();

    public SwordsmanListener(ParamaAnjingCraft plugin) {
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Get player's Sworrdsman level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        playerSwordsmanLevel.put(player, data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaper"));
    }

    //Remove player from plugin memory on leave
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        playerSwordsmanLevel.remove(player);
    }

    // Mana Handler
    public boolean subtractMana(Player player, int manaCost){
        int currMana = player.getLevel();
        if(manaCost <= currMana){
            player.setLevel(currMana - manaCost);
            return true;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "Not enough mana.");
            return false;
        }
    }

    //Cooldown Handler
    public void sendCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.GRAY + " is on cooldown.");
    }
    public void sendNoLongerCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            Random rand = new Random();
            int coatedRandom = rand.nextInt(100);
            switch (item.getType()){
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {

                }
            }
        }
    }
}
