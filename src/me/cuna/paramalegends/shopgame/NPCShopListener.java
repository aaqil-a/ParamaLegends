package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
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

public class NPCShopListener implements Listener {

    private final String NPCName;
    public final ParamaLegends plugin;
    private final HashMap<Player, Inventory> gui = new HashMap<>();
    public DataManager data;


    public NPCShopListener(final ParamaLegends plugin, String NPCName){
        this.plugin = plugin;
        data = plugin.getData();
        this.NPCName = NPCName;
    }

    //Open gui when right click npc
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof ArmorStand) {
            if (event.getRightClicked().getName().equals(NPCName)){
                event.setCancelled(true);
                gui.put(player, createGui(player, data));
                player.openInventory(gui.get(player));
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

        //Check if item clicked is in gui
        if (event.getClickedInventory() == null)
            return;
        if (event.getInventory().equals(gui.get(player)) && !event.getClickedInventory().equals(gui.get(player))) {
            event.setCancelled(true);
            return;
        }
        if (!event.getClickedInventory().equals(gui.get(player)))
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        event.setCancelled(true);

        // Get lectrum of player
        int lectrum = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum");

        //Purchase specified item
        if (event.getSlot() != 0){
            // Gets price of clicked item
            int price = getPrices().get(event.getSlot());
            if (lectrum < price) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
            } else {
                lectrum -= price;
                data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", lectrum);
                data.saveConfig();
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

    //Attack player when npc attacked according to npc type
    public void NPCAttack(Player player, Entity npc){
        player.damage(10, npc);
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
    public Inventory createGui(Player player, DataManager data){
        player.sendMessage("This NPC has no gui!");
        return null;
    }

    //Update lectrum count after purchasing
    public void updateLectrum(InventoryClickEvent event){
        Inventory inv = event.getInventory();
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + event.getWhoClicked().getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(0, item);
        lore.clear();
    }

}
