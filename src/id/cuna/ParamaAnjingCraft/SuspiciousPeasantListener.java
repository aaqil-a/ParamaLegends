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
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§1Suspicious Peasant")){
            event.setCancelled(true);
        }
    }

    //Cancel damage of npc event, kill attacker and create cloud of particles
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§1Suspicious Peasant")) {
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
            if (event.getRightClicked().getName().equals("§1Suspicious Peasant")){
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Lu Goblok Ini peasant");
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
            case 2 -> 10;
            case 4 -> 20;
            case 6 -> 30;
            case 8 -> 60;
            case 10 -> 80;
            case 12,14 -> 200;
            default -> Integer.MAX_VALUE;
        };

        // Check if player has empty tome
        boolean hasHoe = false;
        ItemStack hoe  = null;
        int hoeSlot = 0;
        ItemStack[] playerInv = player.getInventory().getStorageContents();
        for (ItemStack item : playerInv) {
            if(item != null){
                if(item.getType().equals(Material.IRON_HOE) || item.getType().equals(Material.WOODEN_HOE) || item.getType().equals(Material.STONE_HOE)
                || item.getType().equals(Material.DIAMOND_HOE) || item.getType().equals(Material.GOLDEN_HOE) || item.getType().equals(Material.NETHERITE_HOE)){
                    hasHoe = true;
                    hoe = item;
                    break;
                }
            }
            hoeSlot++;
        }

        //Purchase specified tome
        if (event.getSlot() != 0){
            if (!hasHoe) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "A hoe is required to enchant!");
            } else if (lectrum < price) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
            } else {
                lectrum -= price;
                data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", lectrum);
                data.saveConfig();
                updateLectrum(event);
                enchantItem(event, event.getCurrentItem());
                player.getInventory().clear(hoeSlot);
            }
        }
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
    public void enchantItem(InventoryClickEvent event, ItemStack item){
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size()-1);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        newItem.setItemMeta(meta);
        event.getWhoClicked().getInventory().addItem(newItem);
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

        // Hidden Strike
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§5Hidden Strike");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Stab the enemy at a critical");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "location causing critical damage");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "also inflicting 'Coated Blade'");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "on the next attack.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Damage : Normal Attack + 50% of Normal Attack");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown : 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        // Blinding Sand
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§5Blinding Sand");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Throw sand into the enemy");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making them temporarily confused");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Duration : 7 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 16 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "20 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        // Gut Punch
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§5Gut Punch");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Deal base damage on the");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "opponents current HP and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "inflict high discomfort on");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "opponent.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Duration : 3 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 9 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();


        // Forbidden Slash
        item.setType(Material.DIAMOND_HOE);
        meta.setDisplayName(ChatColor.RESET + "§5Forbidden Slash");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Making a decisive slash at");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the enemy dealing damage and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making them do less damage.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Damage : Normal attack + Critical Armor");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Duration : 4 Seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 20 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "60 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

    }
}
