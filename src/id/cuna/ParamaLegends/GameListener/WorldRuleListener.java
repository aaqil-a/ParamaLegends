package id.cuna.ParamaLegends.GameListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldRuleListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    private int maxDepth;


    public WorldRuleListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        maxDepth = data.getConfig().getInt("world.maxdepth");
    }

    //Disable nether portal teleport
    @EventHandler
    public void netherPortal(PlayerPortalEvent event){
        event.setCancelled(true);
    }

    //Disable crafting with materials that have custom name
    @EventHandler
    public void onCraft(CraftItemEvent event){
        Inventory inv = event.getInventory();
        for(ItemStack item : inv.getStorageContents()){
            if(item.hasItemMeta()){
                ItemMeta meta = item.getItemMeta();
                if(meta.hasLore() && meta.hasDisplayName()){
                    event.setCancelled(true);
                }
            }
        }
    }

    //Disable interacting with armor stands with custom names
    @EventHandler
    public void onInteractArmorStand(PlayerArmorStandManipulateEvent event){
        if(event.getRightClicked().getCustomName() != null){
            event.setCancelled(true);
        }
    }

    //Cancel all exp gained
    @EventHandler
    public void onPlayerXpChange(PlayerExpChangeEvent event){
        event.setAmount(0);
    }


    //Disable usage of certain game items
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().hasItemMeta()){
            ItemMeta meta = event.getItem().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals("§5Void Essence")){
                event.setCancelled(true);
            }
        }
    }

    //Disable players from mining below max depth
    @EventHandler
    public void onMine(BlockBreakEvent event){
        if(event.getBlock().getY() <= maxDepth){
            event.getPlayer().sendMessage(ChatColor.DARK_RED+"The earth at this depth is much too dense.");
            event.setCancelled(true);
        }
    }

}
