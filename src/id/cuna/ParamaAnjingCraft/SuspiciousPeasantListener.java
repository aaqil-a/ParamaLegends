package id.cuna.ParamaAnjingCraft;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
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
import java.util.List;

public class SuspiciousPeasantListener implements Listener {
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    public Inventory gui;


    public SuspiciousPeasantListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Cancel damage of npc event
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§4Suspicious Peasant")){
            event.setCancelled(true);
        }
    }

    //Cancel damage of npc event, kill attacker and create cloud of particles
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§4Suspicious Peasant")) {
            if(damager instanceof Player){
                Entity cloudEntity = damager.getLocation().getWorld().spawnEntity(damager.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
                AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
                cloud.setParticle(Particle.CRIT);
                cloud.setDuration(1);
                ((Player) damager).damage(10, event.getEntity());
            } else if(damager instanceof Arrow){
                Arrow arrow = (Arrow) damager;
                if (arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();
                    Entity cloudEntity = player.getLocation().getWorld().spawnEntity(damager.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
                    AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
                    cloud.setParticle(Particle.CRIT);
                    cloud.setDuration(1);
                    player.damage(10, event.getEntity());
                }
            }
            event.setCancelled(true);
        }
    }

    //Open gui when right click npc
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Villager) {
            if (event.getRightClicked().getName().equals("§4Suspicious Peasant")){
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Lu Goblok Ini peasant");
                createGui(player);
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
        Player player = (Player) event.getWhoClicked();
        int lectrum = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum");

        // Gets price of clicked item
        int price = switch (event.getSlot()) {
            case 4 -> 5;
            case 10 -> 20;
            case 12 -> 30;
            case 14 -> 60;
            case 16 -> 80;
            default -> Integer.MAX_VALUE;
        };

        // Check if player has empty tome
        boolean hasHoe = false;
        boolean hasScythe = false;
        ItemStack hoe  = null;
        ItemStack scythe = null;
        int hoeSlot = 0;
        int scytheSlot = 0;
        ItemStack[] playerInv = player.getInventory().getStorageContents();
        for (ItemStack item : playerInv) {
            if(item != null){
                if(item.getType().equals(Material.IRON_HOE) || item.getType().equals(Material.WOODEN_HOE) || item.getType().equals(Material.STONE_HOE)
                || item.getType().equals(Material.DIAMOND_HOE) || item.getType().equals(Material.GOLDEN_HOE) || item.getType().equals(Material.NETHERITE_HOE)){
                    if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Scythe")){
                        hasScythe = true;
                        scythe = item;
                    } else if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Hoe")) {
                        hasHoe = true;
                        hoe = item;
                    } else if (!item.hasItemMeta()){
                        hasHoe = true;
                        hoe = item;
                    }
                }
            }
            if(!hasScythe) scytheSlot++;
            if(!hasHoe) hoeSlot++;
        }
        if (lectrum < price) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "Not enough lectrum!");
        }
        if (event.getSlot()== 4){
           if (hasHoe){
               lectrum -= price;
               data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", lectrum);
               data.saveConfig();
               updateLectrum(event);
               hoeToScythe(event, hoe);
               player.getInventory().clear(hoeSlot);
           }else {
               player.closeInventory();
               player.sendMessage(ChatColor.RED + "A hoe is required to upgrade!");
           }
            //Purchase specified tome
        }else if (event.getSlot() != 0){
            if (!hasScythe) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "A scythe is required to enchant!");
            }else {
                lectrum -= price;
                data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", lectrum);
                data.saveConfig();
                updateLectrum(event);
                enchantItem(event, event.getCurrentItem(), scythe);
                player.getInventory().clear(scytheSlot);
            }
        }
    }


    public void hoeToScythe (InventoryClickEvent event, ItemStack item){
        ItemStack newItem = item.clone();
        int newDamage = 0;
        String name = "";
        switch (newItem.getType()) {
            case WOODEN_HOE -> {
                newDamage = 4;
                name = "§4Wooden Scythe";
            }
            case GOLDEN_HOE -> {
                newDamage = 4;
                name = "§4Golden Scythe";
            }
            case IRON_HOE -> {
                newDamage = 6;
                name = "§4Iron Scythe";
            }
            case STONE_HOE -> {
                newDamage = 5;
                name = "§4Stone Scythe";
            }
            case DIAMOND_HOE -> {
                newDamage = 7;
                name = "§4Diamond Scythe";
            }
            case NETHERITE_HOE -> {
                newDamage = 8;
                name = "§4Netherite Scythe";
            }
        }
        if (newDamage > 0){
            ItemMeta meta = newItem.getItemMeta();
            List<String> lore = meta.getLore();
            if(lore == null) {
                lore = new ArrayList<String>();
                lore.add("");
                lore.add(ChatColor.GRAY+ "When in Main Hand:");
                lore.add(" " + ChatColor.DARK_GREEN + "" + newDamage + " Attack Damage");
            }
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setDisplayName(name);
            newItem.setItemMeta(meta);
        }
        event.getWhoClicked().getInventory().addItem(newItem);
    }

    //Update lectrum count after purchasing
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

    //Give purchased item and erase price from lore
    public void enchantItem(InventoryClickEvent event, ItemStack item, ItemStack hoe){
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size()-1);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ItemStack newHoe = hoe.clone();
        newHoe.setItemMeta(meta);
        event.getWhoClicked().getInventory().addItem(newHoe);
    }

    //Create shop gui
    public void createGui(Player player){
        gui = Bukkit.createInventory(null,27, "§1Everything About Me Is Sus !");

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

        item.setType(Material.ANVIL);
        meta.setDisplayName(ChatColor.RESET + "§4Enchant to Scythe");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Upgrade hoe to scythe.");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "5 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        // Hidden Strike
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§4Hidden Strike");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Stab the enemy at a critical");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "location causing critical damage");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "also inflicting 'Coated Blade'");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "on the next attack.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Crit Damage : 50%");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown : 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(10, item);
        lore.clear();

        // Blinding Sand
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§4Blinding Sand");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Throw sand into the enemy");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making them temporarily confused");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Duration : 7 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 16 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "20 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(12, item);
        lore.clear();

        // Gut Punch
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§4Gut Punch");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Deal base damage on the");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "opponents current HP and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "inflict high discomfort on");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "opponent.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Duration : 3 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 9 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(14, item);
        lore.clear();


        // Forbidden Slash
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§4Forbidden Slash");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Making a decisive slash at");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the enemy dealing damage and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making them do less damage.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Damage : Normal attack + Critical Armor");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Duration : 4 Seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 20 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "60 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(16, item);
        lore.clear();

    }
}
