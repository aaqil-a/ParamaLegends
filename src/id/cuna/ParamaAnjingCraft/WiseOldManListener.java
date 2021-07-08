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
                event.setCancelled(true);
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
        if(event.getInventory().equals(gui2)){
            event.setCancelled(true);
            return;
        }
        if (!event.getClickedInventory().equals(gui) && !event.getClickedInventory().equals(gui2))
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();

        //Open destiny gui depending on item pressed
        if (event.getSlot() == 0){
            createGui2(player, "swordsmanship");
            player.closeInventory();
            player.openInventory(gui2);
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

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        List<String> lore;

        for(int i = 1; i <= 10; i++){
            if(playerLevel >= i){
                item.setType(Material.LIME_WOOL);
            } else {
                item.setType(Material.RED_WOOL);
            }
            meta.setDisplayName(ChatColor.RESET + "" +ChatColor.DARK_PURPLE + "Level "+i);
            lore = getLores(skill, i);
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui2.setItem(itemLocations[i], item);
            lore.clear();
        }
    }

    public List<String> getLores(String skill, int level){
        List <String> lore = new ArrayList<>();
        switch(skill){
            case "magic" -> {
                switch(level){
                    case 1 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Fling Earth");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Gathers nearby earth into");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "a dense cube, then flinging");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "it towards whatever may be");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "in its path.");
                    }
                    case 2 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Ignite");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Sets targets ablaze within a");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "small area, dealing damage");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "over time.");
                    }
                    case 3 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Gust");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Pushes away targets within an");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "area of where you are facing.");
                    }
                    case 4 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Life Drain");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Periodically drains the life of");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "nearby beings when activated.");
                    }
                    case 5 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Blink");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Instantaneously teleport to");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "the targeted location.");
                    }
                    case 6 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Summon Lightning");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Call upon the wrath of the");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "thundergod to summon lightning");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "and smite all enemies in an");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "area.");
                    }
                    case 7 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Illusory Orb");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Casts an arcane orb that hurts");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "any being it comes into contact");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "with. Casting the spell while an");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "orb is travelling teleports the");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "caster to its location.");
                    }
                    case 8 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Dragon's Breath");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Imbuing your essence with draconic");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "powers, you unleash a wide dragon");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "breath attack in front of you");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "that harms all beings it comes");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "into contact with.");
                    }
                    case 9 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Voices of the Damned");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Summons a portal to the realm of");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "the damned. Creatures will appear");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "from the portal and attack any");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "creatures that are in sight.");
                    }
                    case 10 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Nova");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "爆裂, 爆裂, la la la");
                    }
                }
            }
            case "swordsmanship" -> {
                switch (level){
                    case 1 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Precise Blade");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Causes attacks to occasionally");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "deal extra critical damage.");
                    }
                    case 2 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Crippling Blow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Every fifth attack bleeds and");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "cripples enemies.");
                    }
                    case 3 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Shields Up");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Reduces incoming damage and");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "reflects some back at the");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "enemy.");
                    }
                    case 4 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Reaver");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Increases cleave damage, critical");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "chance and critical damage.");
                    }
                    case 5 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Phoenix Dive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Leaps through the air and");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "deals burn damage over time");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "upon landing.");
                    }case 6 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Enrage");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Tremendously increase critical");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "damage and critical chance but");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "be unable to cast abilities");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "during the duration.");
                    }
                    case 7 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Onslaught");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Strike enemies around you");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "with a flurry of attacks");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "of astonishing speed.");
                    }
                    case 8 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Terrifying Cruelty");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Radiate an intimidating aura");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "causing enemies to be afraid,");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "taking more damage and missing");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "their attacks.");
                    }
                    case 9 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Superconducted");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Shocks all enemies around");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "you with chaotic discharges that");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "temporarily blinds them.");
                    }
                    case 10 -> {
                        lore.add(ChatColor.RESET + "" +ChatColor.GOLD + "Calamity");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Summon a raging chaotic storm");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "that strikes down enemies around");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "you. Your hits are guaranteed");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "critical and incoming damage is");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "reduced for its duration.");
                    }
                }
            }
            case "archery" -> {

            }
        }

        return lore;
    }

}
