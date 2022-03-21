package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Destiny implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public final HashMap<Player, Inventory> gui = new HashMap<>();
    public final HashMap<Player, Inventory> gui2 = new HashMap<>();

    private final int[] xpNeeded;
    private final int[] xpNeededSwordsman;

    public Destiny(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
        xpNeeded = plugin.gameManager.experience.xpNeeded;
        xpNeededSwordsman = plugin.gameManager.experience.xpNeededSwordsman;
    }

    //open gui when right clicking npc
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof ArmorStand) {
            if (event.getRightClicked().getName().equals(ChatColor.COLOR_CHAR+"6Your Destiny")){
                event.setCancelled(true);
                createGui(player);
                player.openInventory(gui.get(player));
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Your destiny unravels before you.");
            }
        }
    }

    //Event called when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();

        //Determine if item clicked is in gui
        if (event.getClickedInventory() == null)
            return;
        if(event.getInventory().equals(gui.get(player)) && !event.getClickedInventory().equals(gui.get(player))){
            event.setCancelled(true);
            return;
        }
        if(event.getInventory().equals(gui2.get(player))){
            event.setCancelled(true);
            return;
        }
        if (!event.getClickedInventory().equals(gui.get(player)) && !event.getClickedInventory().equals(gui2.get(player)))
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        event.setCancelled(true);

        //Open destiny gui depending on item pressed
        if (event.getSlot() == 1){
            createGui2(player, ClassGameType.SWORDSMAN);
            player.closeInventory();
            player.openInventory(gui2.get(player));
        } else if (event.getSlot() == 5){
            createGui2(player, ClassGameType.MAGIC);
            player.closeInventory();
            player.openInventory(gui2.get(player));
        } else if (event.getSlot() == 3){
            createGui2(player, ClassGameType.ARCHERY);
            player.closeInventory();
            player.openInventory(gui2.get(player));
        } else if (event.getSlot() == 7){
            createGui2(player, ClassGameType.REAPER);
            player.closeInventory();
            player.openInventory(gui2.get(player));
        }

    }


    //Create main gui
    public void createGui(Player player){
        Inventory openGui = Bukkit.createInventory(null,9, ChatColor.COLOR_CHAR+"5Your Destiny");
        gui.put(player, openGui);
        PlayerParama playerParama = plugin.playerManager.getPlayerParama(player);

        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        int level;
        int exp;


        // Swordsmanship
        meta.setDisplayName(ChatColor.RESET + "Swordsmanship");
        List<String> lore = new ArrayList<String>();
        level = playerParama.getClassLevel(ClassGameType.SWORDSMAN);
        exp = playerParama.getClassExp(ClassGameType.SWORDSMAN);
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeededSwordsman[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        openGui.setItem(1, item);
        lore.clear();

        // Archery
        item.setType(Material.BOW);
        meta.setDisplayName(ChatColor.RESET + "Archery");
        level = playerParama.getClassLevel(ClassGameType.ARCHERY);
        exp = playerParama.getClassExp(ClassGameType.ARCHERY);
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        openGui.setItem(3, item);
        lore.clear();

        // Magic
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Magic");
        level = playerParama.getClassLevel(ClassGameType.MAGIC);
        exp = playerParama.getClassExp(ClassGameType.MAGIC);
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        openGui.setItem(5, item);
        lore.clear();

        // Reaper
        item.setType(Material.IRON_HOE);
        meta.setDisplayName(ChatColor.RESET + "Reaper");
        level = playerParama.getClassLevel(ClassGameType.REAPER);
        exp = playerParama.getClassExp(ClassGameType.REAPER);
        lore.add(ChatColor.RESET + "Level " + ChatColor.GOLD + "" + level);
        if(level < 10)
            lore.add(ChatColor.RESET + "EXP to level up: " + ChatColor.GOLD + "" + (xpNeeded[level]-exp));
        meta.setLore(lore);
        item.setItemMeta(meta);
        openGui.setItem(7, item);
        lore.clear();

    }

    //Create gui of destiny
    public void createGui2(Player player, ClassGameType skill){
        Inventory openGui = Bukkit.createInventory(null,54, ChatColor.COLOR_CHAR+"5Your "+skill.name().substring(0,1).toUpperCase() + skill.name().substring(1).toLowerCase()+" Destiny");
        gui2.put(player, openGui);
        int playerLevel = plugin.playerManager.getPlayerParama(player).getClassLevel(skill);
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
            lore = getLores(skill.name().toLowerCase(), i);
            meta.setLore(lore);
            item.setItemMeta(meta);
            openGui.setItem(itemLocations[i], item);
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
            case "swordsman" -> {
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
            case "reaper" -> {
                switch(level) {
                    case 1 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Coated Blade");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Causes attacks to occasionally inflict");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "poison on the enemy.");
                    }
                    case 2 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Hidden Strike");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Conceal your weapon, stabbing the");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "enemy at a critical location while");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "also inflicting Coated Blade on");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "the next attack.");
                    }
                    case 3 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Blinding Sand");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Throw sand into the enemy");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "making them temporarily confused.");
                    }
                    case 4 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Why Are You Hitting Yourself?");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Occasionally reflect damage back");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "to an attacker.");
                    }
                    case 5 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Prowl");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Focus your energy, increasing");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "your movement speed and damage.");
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Too Slow!");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Occasionally dodge an enemy's attack,");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "making it deal no damage.");
                    }
                    case 6 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Dash Strike");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Dash forwards, striking any");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "enemy you slam into.");
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Second Wind");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "When taking damage, have a chance");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "to gain a second wind.");
                    }
                    case 7 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Rejuvenate");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Clear your state of mind,");
                        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "refreshing most reaper");
                        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "cooldowns.");
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Bloody Fervour");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Passive");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "When dealing damage, have a slight");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "chance to heal yourself.");
                    }
                    case 8 -> {
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Gut Punch");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Deals damage based on the");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "opponent's current HP and");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "inflicts high discomfort on");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "opponent.");
                    }
                    case 9->{
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Forbidden Slash");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Ready your weapon, dealing a");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "decisive slash to the enemy and");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "making them do less damage on");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "the next attack.");
                    }
                    case 10->{
                        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Memento Mori");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Active");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Strike with the power of the");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "grim reaper, dealing immense");
                        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "damage to a single target.");
                    }
                }
            }
        }

        return lore;
    }

}
