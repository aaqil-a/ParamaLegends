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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WiseOldManListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;
    public Inventory gui;
    public Inventory gui2;
    private int[] xpNeeded = {0,230,370,480,580,600,720,750,890,930, Integer.MAX_VALUE};


    public WiseOldManListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //cancel damage to npc
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§6Wise Peculier")){
            event.setCancelled(true);
        }
    }

    //Damage player when attacking npc
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§6Wise Peculier")) {
            if(damager instanceof Player){
                damager.getWorld().strikeLightningEffect(damager.getLocation());
                ((Player) damager).damage(10, event.getEntity());
            } else if(damager instanceof Arrow){
                Arrow arrow = (Arrow) damager;
                if (arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();
                    player.getWorld().strikeLightningEffect(player.getLocation());
                    player.damage(10, event.getEntity());
                }
            } else {
                damager.getWorld().strikeLightningEffect(event.getEntity().getLocation());
            }
            event.setCancelled(true);
        }
    }

    //open gui when right clicking npc
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Villager) {
            if (event.getRightClicked().getName().equals("§6Wise Peculier")){
                createGui(player);
                player.openInventory(gui);
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Your destiny unravels before you.");
            }
        }
    }

    //Event called when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event){
        //Determine if item clicked is in gui
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

        //Open destiny gui depending on item pressed
        if (event.getSlot() == 0){
            player.sendMessage("i am penis");
        } else if (event.getSlot() == 4){
            createGui2(player, "magic");
            player.closeInventory();
            player.openInventory(gui2);
        }
    }

    //Create main gui
    public void createGui(Player player){
        gui = Bukkit.createInventory(null,9, "§5Your Destiny");

        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        int level;
        int exp;


        // Swordsmanship
        meta.setDisplayName(ChatColor.RESET + "Swordsmanship");
        List<String> lore = new ArrayList<String>();
        level = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".swordsmanship");
        exp = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".swordsmanshipexp");
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(0, item);
        lore.clear();

        // Archery
        item.setType(Material.BOW);
        meta.setDisplayName(ChatColor.RESET + "Archery");
        level = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".archery");
        exp = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".archeryexp");
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        // Magic
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Magic");
        level = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".magic");
        exp = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".magicexp");
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        // Mining
        item.setType(Material.IRON_PICKAXE);
        meta.setDisplayName(ChatColor.RESET + "Mining");
        level = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".mining");
        exp = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".miningexp");
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();

        // Farming
        item.setType(Material.IRON_HOE);
        meta.setDisplayName(ChatColor.RESET + "Farming");
        level = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".farming");
        exp = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".farmingexp");
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

    }

    //Create gui of destiny
    public void createGui2(Player player, String skill){
        gui2 = Bukkit.createInventory(null,54, "§5Your "+skill.substring(0,1).toUpperCase() + skill.substring(1)+" Destiny");
        int playerLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+"."+skill);
        int itemLocations[] = {0, 48, 50, 40, 30, 31, 32, 22, 12, 14, 4};

        ItemStack item = new ItemStack(Material.LIME_WOOL);
        ItemMeta meta = item.getItemMeta();


        // Level 1
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "" +ChatColor.DARK_PURPLE + "Level 1");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getString("destinylevelunlock." + skill + "." + 1));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui2.setItem(itemLocations[1], item);
        lore.clear();

        //Level 2-10
        for(int i = 2; i <= 10; i++){
            if(playerLevel >= i){
                item.setType(Material.LIME_WOOL);
            } else {
                item.setType(Material.RED_WOOL);
            }
            meta.setDisplayName(ChatColor.RESET + "" +ChatColor.DARK_PURPLE + "Level "+i);
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getString("destinylevelunlock." + skill + "." + i));
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui2.setItem(itemLocations[i], item);
            lore.clear();
        }
    }

}
