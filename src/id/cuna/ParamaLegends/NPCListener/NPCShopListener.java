package id.cuna.ParamaLegends.NPCListener;

import com.mysql.fabric.xmlrpc.base.Param;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
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
    private final ParamaLegends plugin;
    private final int[] expansePrice = {400, 750, 1500, 2500, 4000, 8000, 10000};
    private final HashMap<Player, Inventory> gui = new HashMap<>();
    private static DataManager data;

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

                if(event.getCurrentItem().getType().equals(Material.DIAMOND)){
                    //expanse fund
                    int expanseFund = data.getConfig().getInt("world.expanseFund");
                    int expanseLevel = data.getConfig().getInt("world.expanseLevel");
                    expanseFund -= price;
                    if(expanseFund <= 0){
                        //expand land
                        expandLand(player);
                        expanseFund = expansePrice[expanseLevel+1];
                        data.getConfig().set("world.expanseLevel", expanseLevel+1);
                    }
                    data.getConfig().set("world.expanseFund", expanseFund);
                    data.saveConfig();
                    updateExpanseFund(event);
                    updateLectrum(event);
                } else if(giveItem(event.getWhoClicked().getInventory(), event.getCurrentItem())) updateLectrum(event);
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

    public void expandLand(Player player){
        Bukkit.broadcastMessage(ChatColor.GREEN+"The world around you feels safer.");
        int maxDepth = data.getConfig().getInt("world.maxdepth");
        if(maxDepth >= 10){
            maxDepth -= 10;
            data.getConfig().set("world.maxdepth", maxDepth);
            plugin.worldRuleListener.setMaxDepth(maxDepth);
        }

        //expand worldguard region
        // Get worldguard regions
        World worldGuard = BukkitAdapter.adapt(player.getWorld());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(worldGuard);
        ProtectedRegion safezone = regions.getRegion("safezone");
        //        regions.removeRegion("safezone");
        //create safe zone
        BlockVector3 min = safezone.getMinimumPoint();
        BlockVector3 max = safezone.getMaximumPoint();
        ProtectedRegion region = new ProtectedCuboidRegion("safezone", min.add(-16, 0, -16), max.add(16,0,16));
        region.setPriority(1);
        region.setFlag(Flags.GREET_MESSAGE, ChatColor.GREEN + "You feel slightly more protected...");
        region.setFlag(Flags.FAREWELL_MESSAGE, ChatColor.RED + "The wilderness exudes a threatening aura...");
        region.setFlag(Flags.HEALTH_REGEN, StateFlag.State.ALLOW);
        region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);

        regions.addRegion(region);
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
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + event.getWhoClicked().getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(0, item);
        lore.clear();
    }

    //update expanse fund
    public static void updateExpanseFund(InventoryClickEvent event){
        Inventory inv = event.getInventory();
        ItemStack item = new ItemStack(Material.DIAMOND, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Expanse Fund");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Expand safe zone region.");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Every player can contribute");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "to the fund.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Remaining lectrum required: " + data.getConfig().getInt("world.expanseFund"));
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "5 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(8, item);
        lore.clear();
    }

}
