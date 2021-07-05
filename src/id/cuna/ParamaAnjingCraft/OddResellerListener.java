package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class OddResellerListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;;
    public Inventory gui;
    public Inventory gui2;

    public OddResellerListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    // Cancel damage to npc
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§2Odd Reseller")){
            event.setCancelled(true);
        }
    }

    // Send message to player when damaging npc
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§2Odd Reseller")) {
            damager.sendMessage(ChatColor.GOLD + "He appears to be safeguarded by a sturdy shield.");
            event.setCancelled(true);
        }
    }

    // Open gui when right clickign npc
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Villager) {
            if (event.getRightClicked().getName().equals("§2Odd Reseller")){
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Wares from all over the world are for sale.");
                createGui(player);
                player.openInventory(gui);
            }
        }
    }

    // Event called when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event){
        //Check if item clicked is in gui
        if (event.getClickedInventory() == null)
            return;
        if(event.getInventory().equals(gui) && !event.getClickedInventory().equals(gui)){
            event.setCancelled(true);
            return;
        }
        if (!event.getClickedInventory().equals(gui))
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int lectrum = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum");
        int price = Integer.MAX_VALUE;

        //Get price of item clicked
        switch(event.getSlot()){
            case 2:
                price = 10;
                break;
        }

        //Purchase item
        if(event.getSlot() != 0) {
            if(lectrum < price){
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
            } else {
                lectrum -= price;
                data.getConfig().set("players."+player.getUniqueId().toString()+".lectrum", lectrum);
                data.saveConfig();
                updateLectrum(event);
                purchaseItem(event, event.getCurrentItem());
            }
        }
    }

    //Update lectrum after purchasing item
    public void updateLectrum(InventoryClickEvent event){
        Inventory inv = event.getInventory();
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + event.getWhoClicked().getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(0, item);
        lore.clear();
    }

    //Give purchased item to player while erasing price from lore
    public void purchaseItem(InventoryClickEvent event, ItemStack item){
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size()-1);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        newItem.setItemMeta(meta);
        event.getWhoClicked().getInventory().addItem(newItem);
    }

    //Create gui of shop
    public void createGui(Player player){
        gui = Bukkit.createInventory(null,9, "§5Odd Reseller's Wares");


        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(0, item);
        lore.clear();


        // Empty Tome
        item.setType(Material.BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Empty Tome");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "An empty book surging");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "with magical potential.");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();


    }



}
