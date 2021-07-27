package id.cuna.ParamaLegends.NPCListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NPCShopListener implements Listener {

    private final String NPCName;
    private Inventory gui;
    private static DataManager data;

    public NPCShopListener(final ParamaLegends plugin, String NPCName){
        data = plugin.getData();
        this.NPCName = NPCName;
    }

    //Open gui when right click npc
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Villager) {
            if (event.getRightClicked().getName().equals(NPCName)){
                event.setCancelled(true);
                player.sendMessage(getNPCMessage());
                gui = createGui(player, data);
                player.openInventory(gui);
            }
        }
    }

    //Handle events when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event) {
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
        Player player = (Player) event.getWhoClicked();
        int lectrum = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum");
        // Gets price of clicked item
        int price = getPrices().get(event.getSlot());

        //Purchase specified item
        if (event.getSlot() != 0){
            if (lectrum < price) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
            } else {
                lectrum -= price;
                data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", lectrum);
                data.saveConfig();
                if(giveItem(event.getWhoClicked().getInventory(), event.getCurrentItem())) updateLectrum(event);
            }
        }

    }

    //Purchase item from gui
    public boolean giveItem(Inventory inventory, ItemStack item){
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size()-1);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        newItem.setItemMeta(meta);
        inventory.addItem(newItem);
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
    public static void updateLectrum(InventoryClickEvent event){
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

}
