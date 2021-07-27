package id.cuna.ParamaLegends.NPCListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class WiseOldManListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public Inventory gui;
    public Inventory gui2;
    private final int[] xpNeeded = {0,460,740,960,1160,1200,1440,1500,1780,1860, Integer.MAX_VALUE};


    public WiseOldManListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
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
        if (event.getSlot() == 1){
            createGui2(player, "swordsmanship");
            player.closeInventory();
            player.openInventory(gui2);
        } else if (event.getSlot() == 5){
            createGui2(player, "magic");
            player.closeInventory();
            player.openInventory(gui2);
        } else if (event.getSlot() == 3){
            createGui2(player, "archery");
            player.closeInventory();
            player.openInventory(gui2);
        } else if (event.getSlot() == 7){
            createGui2(player, "reaper");
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
        gui.setItem(1, item);
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
        gui.setItem(3, item);
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
        gui.setItem(5, item);
        lore.clear();

        // Reaper
        item.setType(Material.IRON_HOE);
        meta.setDisplayName(ChatColor.RESET + "Reaper");
        level = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaper");
        exp = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaperexp");
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(7, item);
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
                switch(level) {
                    case 1 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Hunter's Eye");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Arrow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Tag an enemy, revealing them");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "for some time and increasing");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "its incoming damage.");
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Archer's Blessing");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "The further you are from the");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "enemy, the higher damage your");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "arrows deal.");
                    }
                    case 2 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Viper's Bite");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Arrow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Poisons your target for a short");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "duration.");
                    }
                    case 3 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Totsuka's Creation");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Throw a web to the targeted");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "location, rooting enemies caught");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "in its location.");
                    }
                    case 4 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Wind Boost");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Harness the power of wind to");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "boost your arrows, dealing more");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "damage and knockback.");
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Nimble");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Movement speed lightly increased.");
                    }
                    case 5 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Neurotoxin");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Arrow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Imbue your arrow with neurotoxin,");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "poisoning and slowing targets for");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "a medium duration.");
                    }
                    case 6 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Soulstring");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Summons a bow companion that");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "periodically shoots very sharp");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "arrows at nearby enemies.");
                    }
                    case 7 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Retreat");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Arrow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Shoots two arrows with the first");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "dealing less damage. After shooting,");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "you fall back a short distance and");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "your speed is temporarily increased.");
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Huayra's Fury");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "You reawaken the wrath of Huayra,");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "sacrificing some of your life force");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "to shoot 20 consecutive arrows at");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "great speeds without consuming mana.");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "You cannot move while firing.");
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Advanced Archery");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Your arrows have a slight chance of");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "dealing extra damage.");
                    }
                    case 8 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Blast");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Arrow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Imbue your arrow with gunpowder,");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "making it explode on impact.");
                    }
                    case 9->{
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Royal Artillery");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Calls in a continuous arrow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "barrage on the targeted location.");
                    }
                    case 10->{
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Whistling Wind");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Arrow");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "An arrow that directs itself towards");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "every enemy around its shooter and");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "returns after.");
                    }
                }
            }
        }

        return lore;
    }

}
