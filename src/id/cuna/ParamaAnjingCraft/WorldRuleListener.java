package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WorldRuleListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;

    public WorldRuleListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }


    //Disable breeding
    @EventHandler
    public void breedAnimal(EntityBreedEvent event){
        event.setCancelled(true);
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


}
