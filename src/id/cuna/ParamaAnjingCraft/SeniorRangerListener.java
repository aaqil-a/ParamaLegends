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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SeniorRangerListener implements Listener {
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    public Inventory gui;


    public SeniorRangerListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Cancel damage of npc event
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§aAdept Ranger")){
            event.setCancelled(true);
        }
    }

    public void attackWithArrow(Player player, Entity source){
        player.getWorld().playSound(source.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 0f);
        Arrow arrow1 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(3,0,3), EntityType.ARROW);
        arrow1.setVelocity(new Vector(-0.5, 0, -0.5));
        Arrow arrow2 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(0,4,0), EntityType.ARROW);
        arrow2.setVelocity(new Vector(0,-1,0));
        Arrow arrow3 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(-3,0,3), EntityType.ARROW);
        arrow3.setVelocity(new Vector(0.5,0,-0.5));
        Arrow arrow4 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(3,0,-3), EntityType.ARROW);
        arrow4.setVelocity(new Vector(-0.5,0,0.5));
        Arrow arrow5 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(-3,0,-3), EntityType.ARROW);
        arrow5.setVelocity(new Vector(0.5,0,0.5));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            arrow1.remove();
            arrow2.remove();
            arrow3.remove();
            arrow4.remove();
            arrow5.remove();
            player.damage(10, source);
        }, 6);
    }

    //Cancel damage of npc event, kill attacker and create cloud of particles
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (event.getEntityType() == EntityType.VILLAGER && event.getEntity().getName().equals("§aAdept Ranger")) {
            if(damager instanceof Player){
                Player attacker = (Player) damager;
                attackWithArrow(attacker, event.getEntity());
            } else if(damager instanceof Arrow){
                Arrow arrow = (Arrow) damager;
                if (arrow.getShooter() instanceof Player) {
                    Player attacker = (Player) arrow.getShooter();
                    attackWithArrow(attacker, event.getEntity());
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
            if (event.getRightClicked().getName().equals("§aAdept Ranger")){
                event.setCancelled(true);
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Acquire a ranger's skills from far and wide.");
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
            case 2 -> 1;
            case 4 -> 3;
            case 6 -> 40;
            case 8 -> 80;
            case 10 -> 10;
            case 12 -> 200;
            case 14 -> 300;
            case 16 -> 30;
            case 20 -> 50;
            case 22, 24 -> 400;
            default -> Integer.MAX_VALUE;
        };

        switch(event.getSlot()){
            case 2,4,6,8,10,12,14,16,20,22,24-> {
                if (lectrum < price) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "Not enough lectrum!");
                } else {
                    lectrum -= price;
                    data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", lectrum);
                    data.saveConfig();
                    updateLectrum(event);
                    ItemStack newItem = event.getCurrentItem().clone();
                    ItemMeta meta = newItem.getItemMeta();
                    List<String> lore = meta.getLore();
                    lore.remove(lore.size()-1);
                    meta.setLore(lore);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    newItem.setItemMeta(meta);
                    event.getWhoClicked().getInventory().addItem(newItem);
                }
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

    //Create shop gui
    public void createGui(Player player){
        gui = Bukkit.createInventory(null,27, "§aRanger Gear");

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(0, item);
        lore.clear();

        //Hunter's Eye
        ItemStack tippedArrowItem = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta tippedArrowMeta = (PotionMeta) tippedArrowItem.getItemMeta();
        tippedArrowMeta.setColor(Color.YELLOW);
        tippedArrowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aHunter's Eye");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Tag an enemy, revealing them");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "for some time and increasing");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "its incoming damage.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 20");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 1");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "1 Lectrum");
        tippedArrowMeta.setLore(lore);
        tippedArrowItem.setItemMeta(tippedArrowMeta);
        gui.setItem(2, tippedArrowItem);
        lore.clear();

        // Viper's Bite;
        tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aViper's Bite");
        tippedArrowMeta.setColor(Color.GREEN);
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Poisons your target for a short");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "duration.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 15");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 2");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "3 Lectrum");
        tippedArrowMeta.setLore(lore);
        tippedArrowItem.setItemMeta(tippedArrowMeta);
        gui.setItem(4, tippedArrowItem);
        lore.clear();

        // Totsuka's Creation
        item.setType(Material.COBWEB);
        meta.setDisplayName(ChatColor.RESET + "§aTotsuka's Creation");
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Throw a web to the targeted");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "location, rooting enemies caught");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "in its location.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 40");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 30 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 3");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "40 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();

        // Wind Boost
        item.setType(Material.FEATHER);
        meta.setDisplayName(ChatColor.RESET + "§aWind Boost");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Harness the power of wind to");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "boost your arrows, dealing more");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "damage and knockback.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 60");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 30 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 4");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "80 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

        // Neurotoxin
        tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aNeurotoxin");
        tippedArrowMeta.setColor(Color.LIME);
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbue your arrow with neurotoxin,");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "poisoning and slowing targets for");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "a medium duration.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 50");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 5");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        tippedArrowMeta.setLore(lore);
        tippedArrowItem.setItemMeta(tippedArrowMeta);
        gui.setItem(10, tippedArrowItem);
        lore.clear();

        // Soulstring
        item.setType(Material.TRIPWIRE_HOOK);
        meta.setDisplayName(ChatColor.RESET + "§aSoulstring");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summons a bow companion that");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "periodically shoots very sharp");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "arrows at nearby enemies.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 150");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1 minute");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 6");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(12, item);
        lore.clear();

        // Retreat
        tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aRetreat");
        tippedArrowMeta.setColor(Color.WHITE);
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Shoots two arrows with the first");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "dealing less damage. After shooting,");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "you fall back a short distance and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "your speed is temporarily increased.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 70");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 7");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
        tippedArrowMeta.setLore(lore);
        tippedArrowItem.setItemMeta(tippedArrowMeta);
        gui.setItem(14, tippedArrowItem);
        lore.clear();


        // Huayra's Fury
        item.setType(Material.SKELETON_SKULL);
        meta.setDisplayName(ChatColor.RESET + "§aHuayra's Fury");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "You reawaken the wrath of Huayra,");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "sacrificing some of your life force");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "to shoot 20 consecutive arrows at");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "great speeds without consuming mana.");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "You cannot move while firing.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 300");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1.5 minutes");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 7");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(16, item);
        lore.clear();

        // Blast
        tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aBlast");
        tippedArrowMeta.setColor(Color.RED);
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbue your arrow with gunpowder,");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making it explode on impact.");;
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 60");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 8");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "50 Lectrum");
        tippedArrowMeta.setLore(lore);
        tippedArrowItem.setItemMeta(tippedArrowMeta);
        gui.setItem(20, tippedArrowItem);
        lore.clear();

        // Royal Artillery
        item.setType(Material.NAUTILUS_SHELL);
        meta.setDisplayName(ChatColor.RESET + "§aRoyal Artillery");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Calls in a continuous arrow");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "barrage on the targeted location.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 300");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1 minute");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 9");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(22, item);
        lore.clear();


        // Whistling Wind
        item.setType(Material.SPECTRAL_ARROW);
        meta.setDisplayName(ChatColor.RESET + "§aWhistling Wind");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "An arrow that directs itself towards");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "every enemy around its shooter and");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "returns after.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 200");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 10");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(24, item);
        lore.clear();

    }
}
