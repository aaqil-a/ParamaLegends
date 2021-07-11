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

public class RetiredWeaponsmithListener implements Listener {
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    public Inventory gui;


    public RetiredWeaponsmithListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Cancel damage of npc event
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§2Retired Weaponsmith")){
            event.setCancelled(true);
        }
    }

    //Cancel damage of npc event, kill attacker and create cloud of particles
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§2Retired Weaponsmith")) {
            if(damager instanceof Player){
                Entity cloudEntity = damager.getLocation().getWorld().spawnEntity(damager.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
                AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
                cloud.setParticle(Particle.DAMAGE_INDICATOR);
                cloud.setDuration(1);
                ((Player) damager).damage(10, event.getEntity());
                ((Player) damager).playSound(damager.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            } else if(damager instanceof Arrow){
                Arrow arrow = (Arrow) damager;
                if (arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();
                    Entity cloudEntity = player.getLocation().getWorld().spawnEntity(damager.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
                    AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
                    cloud.setParticle(Particle.DAMAGE_INDICATOR);
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
            if (event.getRightClicked().getName().equals("§2Retired Weaponsmith")){
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "A weaponsmith's artistry is on offer.");
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
            case 2 -> 30;
            case 4 -> 100;
            case 6 -> 200;
            case 8 -> 250;
            case 10 -> 300;
            case 12 -> 400;
            case 14 -> 500;
            default -> Integer.MAX_VALUE;
        };


        //Purchase specified tome
        if (event.getSlot() != 0){
            if (lectrum < price) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
            } else {
                lectrum -= price;
                data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", lectrum);
                data.saveConfig();
                updateLectrum(event);
                enchantItem(event, event.getCurrentItem());
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
        gui = Bukkit.createInventory(null,18, "§2Swordsman Buffs");

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

        // Shields Up
        item.setType(Material.SHIELD);
        meta.setDisplayName(ChatColor.RESET + "§2Shields Up");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Reduces incoming damage and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "reflects some back at the");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "enemy.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 50");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 20 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 3");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        // Phoenix Dive
        item.setType(Material.FIRE_CHARGE);
        meta.setDisplayName(ChatColor.RESET + "§2Phoenix Dive");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Leaps through the air and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "deals burn damage over time");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "upon landing.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 100");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 15 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 5");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "100 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        // Enrage
        item.setType(Material.BLAZE_POWDER);
        meta.setDisplayName(ChatColor.RESET + "§2Enrage");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Tremendously increase critical");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "damage and critical chance but");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "be unable to cast abilities");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "during the duration.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 150");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 45 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 6");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();

        // Onslaught
        item.setType(Material.GUNPOWDER);
        meta.setDisplayName(ChatColor.RESET + "§2Onslaught");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Strike enemies around you");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "with a flurry of attacks");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "of astonishing speed.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 150");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 12 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 7");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "250 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

        // Terrifying Cruelty
        item.setType(Material.MAGMA_CREAM);
        meta.setDisplayName(ChatColor.RESET + "§2Terrifying Cruelty");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Radiate an intimidating aura");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "causing enemies to be afraid,");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "taking more damage and missing");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "their attacks.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 200");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 30 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 8");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(10, item);
        lore.clear();

        // Superconducted
        item.setType(Material.IRON_SWORD);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.RESET + "§2Superconducted");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Shocks all enemies around");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "you with chaotic discharges that");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "temporarily blinds them.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 300");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1 minute");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 9");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(12, item);
        lore.clear();

        // Calamity
        item.setType(Material.NETHER_STAR);
        meta.setDisplayName(ChatColor.RESET + "§2Calamity");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summon a raging chaotic storm");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "that strikes down enemies around");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "you. Your hits are guaranteed");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "critical and incoming damage is");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "reduced for its duration.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 500");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 2 minutes");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 10");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "500 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(14, item);
        lore.clear();

    }
}
