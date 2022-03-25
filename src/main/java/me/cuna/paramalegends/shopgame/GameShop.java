package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameShop implements Listener {

    private final String NPCName;
    public final ParamaLegends plugin;
    public DataManager data;


    public GameShop(final ParamaLegends plugin, String NPCName){
        this.plugin = plugin;
        data = plugin.dataManager;
        this.NPCName = NPCName;
    }

    //Open gui when right click npc
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof ArmorStand) {
            if (event.getRightClicked().getName().equals(NPCName)){
                event.setCancelled(true);
                createGui(event.getPlayer());
                PlayerParama playerParama = plugin.getPlayerParama(event.getPlayer());
                event.getPlayer().openInventory(playerParama.getOpenShopGui().getGui());
            }
        }
    }

    //Open gui when right click npc
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity armorStand = event.getEntity();
        if(armorStand instanceof ArmorStand) {
            if(armorStand.getName().equals(NPCName)) event.setCancelled(true);
        }
    }

    //Handle events when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerParama playerParama = plugin.getPlayerParama(player);
        Inventory gui = playerParama.getOpenShopGui().getGui();
        
        //Check if item clicked is in gui
        if (event.getClickedInventory() == null)
            return;
        if (event.getInventory().equals(gui) && !event.getClickedInventory().equals(gui)) {
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

        // Get lectrum of player
        int lectrum = playerParama.getLectrum();

        //Purchase specified item
        if (event.getSlot() != 0){
            // Gets price of clicked item
            int price = getPrices().get(event.getSlot());
            if (lectrum < price) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
            } else {
                plugin.getPlayerParama(player).removeLectrum(price);
                if(giveItem(event)) updateLectrum(event);
            }
        }

    }

    //Purchase item from gui
    public boolean giveItem(InventoryClickEvent event){
        ItemStack newItem = event.getCurrentItem().clone();
        ItemMeta meta = newItem.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size()-1);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        newItem.setItemMeta(meta);
        event.getWhoClicked().getInventory().addItem(newItem);
        return true;
    }

    //Send player message when opening gui according to npc type
    public HashMap<Integer, Integer> getPrices(){
        return null;
    }

    //Send player message when opening gui according to npc type
    public String getNPCMessage(){
        return "";
    }

    //Create gui
    public void createGui(Player player){
        player.sendMessage("This NPC has no gui!");
    }

    //Update lectrum count after purchasing
    public void updateLectrum(InventoryClickEvent event){
        Inventory inv = event.getInventory();
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + plugin.getPlayerParama((Player) event.getWhoClicked()).getLectrum());
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(0, item);
        lore.clear();
    }

}
