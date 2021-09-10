package me.cuna.paramalegends.game;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class WorldRuleListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    private int maxDepth;


    public WorldRuleListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        maxDepth = data.getConfig().getInt("world.maxdepth");
    }

    public void setMaxDepth(int maxDepth){
        this.maxDepth = maxDepth;
    }

    //Disable crafting with materials that have custom name
    @EventHandler
    public void onCraft(CraftItemEvent event){
        Inventory inv = event.getInventory();

        //disable craftign wtih custom items
        for(ItemStack item : inv.getStorageContents()){
            if(item.hasItemMeta()){
                ItemMeta meta = item.getItemMeta();
                if(meta.hasLore() && meta.hasDisplayName()){
                    event.setCancelled(true);
                }
            }
        }

        //enable crafting of custom items
        if(inv.getStorageContents()[0].hasItemMeta()){
            ItemMeta meta = inv.getStorageContents()[0].getItemMeta();
            if(meta.getDisplayName().equals(ChatColor.COLOR_CHAR+"aMysterious Ooze")){
                ItemStack essence = inv.getStorageContents()[5];
                if(essence.hasItemMeta()){
                    ItemMeta essenceMeta = essence.getItemMeta();
                    event.setCancelled(!essenceMeta.getDisplayName().equals(ChatColor.COLOR_CHAR+"5Void Essence"));
                }
            }
            if(meta.getDisplayName().equals(ChatColor.COLOR_CHAR+"6Esoteric Pearl")) event.setCancelled(false);
            if(meta.getDisplayName().equals(ChatColor.COLOR_CHAR+"6Winery Barrel")) event.setCancelled(false);
        }
    }

    //Disable interacting with armor stands with custom names
    @EventHandler
    public void onInteractArmorStand(PlayerArmorStandManipulateEvent event){
        if(event.getRightClicked().getCustomName() != null){
            event.setCancelled(true);
        }
    }

    //Disable usage of certain game items
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().hasItemMeta()){
            ItemMeta meta = event.getItem().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.COLOR_CHAR+"5Void Essence")){
                event.setCancelled(true);
            }
        }
    }

    //Disable placing of certain game items
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.getItemInHand().hasItemMeta()){
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.COLOR_CHAR+"aEssence of Nature")){
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

    //Take player lectrum on death
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        int lectrum = data.getConfig().getInt("players."+player.getUniqueId().toString()+".lectrum");
        int lost = lectrum/10;
        if(lectrum >= 10) lectrum -= lost;
        data.getConfig().set("players."+player.getUniqueId().toString()+".lectrum", lectrum);
        player.sendMessage(ChatColor.RED+"You lost " + lost + " lectrum upon dying.");
    }

    //listen for mana potion drink
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event){
        if(event.getItem().getType().equals(Material.POTION)){
            if(event.getItem().getItemMeta() != null) {
                if(event.getItem().getItemMeta().getDisplayName().contains(ChatColor.COLOR_CHAR+"9Mana Potion")){
                    plugin.getPlayerParama(event.getPlayer()).addMana(100);
                } else if(event.getItem().getItemMeta().getDisplayName().contains(ChatColor.COLOR_CHAR+"9Greater Mana Potion")){
                    plugin.getPlayerParama(event.getPlayer()).addMana(200);
                }
            }
        }
    }

}
