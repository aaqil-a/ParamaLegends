package id.cuna.ParamaAnjingCraft;

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
import java.util.List;

public class BanishedMagusListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;
    public Inventory gui;


    public BanishedMagusListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Cancel damage of npc event
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§5Banished Magus")){
            event.setCancelled(true);
        }
    }

    //Cancel damage of npc event, kill attacker and create cloud of particles
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§5Banished Magus")) {
            if(damager instanceof Player){
                Entity cloudEntity = damager.getLocation().getWorld().spawnEntity(damager.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
                AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
                cloud.setParticle(Particle.CRIT_MAGIC);
                cloud.setDuration(1);
                ((Player) damager).damage(10, event.getEntity());
            } else if(damager instanceof Arrow){
                Arrow arrow = (Arrow) damager;
                if (arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();
                    Entity cloudEntity = player.getLocation().getWorld().spawnEntity(damager.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
                    AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
                    cloud.setParticle(Particle.CRIT_MAGIC);
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
            if (event.getRightClicked().getName().equals("§5Banished Magus")){
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Even the wisdom of wise mages can be bartered for.");
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
            case 16 -> 300;
            case 20 -> 400;
            case 24 -> 600;
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
        gui = Bukkit.createInventory(null,27, "§5Magic Tomes");

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

        // Ice Ball
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Fling Earth");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Gathers nearby earth into");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "a dense cube, then flinging");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "it towards whatever may be");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "in its path.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 10");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 3 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 1");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        // Ignite
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Ignite");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Sets targets ablaze within a");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "small area, dealing damage");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "over time.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 20");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 7 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 2");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "20 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        // Gust
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Gust");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Pushes away targets within an");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "area of where you are facing.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 30");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 3");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();


        // Life Drain
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Life Drain");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Periodically drains the life of");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "nearby beings when activated.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 20 per second");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 4");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "60 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

        // Blink
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Blink");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Instantaneously teleport to");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the targeted location.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 30");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 15 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 5");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "80 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(10, item);
        lore.clear();

        // Summon Lightning
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Summon Lightning");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Call upon the wrath of the");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "thundergod to summon lightning");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "and smite all enemies in an");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "area.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 150");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 30 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 6");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(12, item);
        lore.clear();

        // Illusory orb
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Illusory Orb");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Casts an arcane orb that hurts");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "any being it comes into contact");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "with. Casting the spell while an");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "orb is travelling teleports the");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "caster to its location.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 100");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 7");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(14, item);
        lore.clear();

        // Dragon breath
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Dragon's Breath");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbuing your essence with draconic");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "powers, you unleash a wide dragon");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "breath attack in front of you");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "that harms all beings it comes");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "into contact with.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 200");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 20 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 8");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(16, item);
        lore.clear();

        // Voices of the Damned
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Voices of the Damned");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summons a portal to the realm of");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the damned. Creatures will appear");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "from the portal and attack any");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "creatures that are in sight.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 400");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1 minute");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 9");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(20, item);
        lore.clear();

        // Nova
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Nova");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "爆裂, 爆裂, la la la");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 600");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 2 minutes");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 10");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "600 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(24, item);
        lore.clear();

    }


}
