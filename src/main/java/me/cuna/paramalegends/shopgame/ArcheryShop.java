package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArcheryShop extends GameShop {

    private final ParamaLegends plugin;

    //Prices array
    public HashMap<Integer, Integer> prices = new HashMap<>(){{
        put(2,1); //hunter's eye
        put(4,3); //viper's bite
        put(6,40); //totsuka's creation
        put(8,80); //windboost
        put(10,8); //neurotoxin
        put(12,200); //soulstring
        put(14,10); //retreat
        put(16,300); //huayra's fury
        put(20,15); //blast
        put(22,400); //royal artillery
        put(24,400); //whistling wind
    }};

    public ArcheryShop(ParamaLegends plugin) {
        super(plugin, ChatColor.GREEN+"Fletcher's Table");
        this.plugin = plugin;
    }

    //Get prices
    @Override
    public HashMap<Integer, Integer> getPrices(){
        return prices;
    }

    //Create shop gui
    @Override
    public void createGui(Player player){
        PlayerParama playerParama = plugin.getPlayerParama(player);
        ShopGUI shopGUI = new ShopGUI(plugin,this, 27,ChatColor.COLOR_CHAR+"aRanger Gear");
        playerParama.setOpenShopGui(shopGUI);
        int playerLevel = playerParama.getClassLevel(ClassGameType.ARCHERY);

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + plugin.getPlayerParama(player).getLectrum());
        meta.setLore(lore);
        item.setItemMeta(meta);
        shopGUI.setItem(0, item);
        lore.clear();

        //Hunter's Eye
        ItemStack tippedArrowItem = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta tippedArrowMeta = (PotionMeta) tippedArrowItem.getItemMeta();
        tippedArrowMeta.setColor(Color.YELLOW);
        tippedArrowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        tippedArrowMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aHunter's Eye");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Tag an enemy, revealing them");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "for some time and increasing");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "its incoming damage.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.hunterEye.getManaCost());
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 1");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(2)+" Lectrum");
        tippedArrowMeta.setLore(lore);
        tippedArrowItem.setAmount(8);
        tippedArrowItem.setItemMeta(tippedArrowMeta);
        shopGUI.setItem(2, tippedArrowItem);
        lore.clear();

        if(playerLevel >= 2){
            // Viper's Bite;
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aViper's Bite");
            tippedArrowMeta.setColor(Color.GREEN);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Poisons your target for a short");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "duration.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.viperBite.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 2");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(4)+" Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            shopGUI.setItem(4, tippedArrowItem);
            lore.clear();
        }
        if(playerLevel >= 3){
            // Totsuka's Creation
            item.setType(Material.COBWEB);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aTotsuka's Creation");
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Throw a web to the targeted");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "location, rooting enemies caught");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "in its location.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.totsukaCreation.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.archery.totsukaCreation.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 3");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(6)+" Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(6, item);
            lore.clear();
        }
        if(playerLevel >= 4){
            // Wind Boost
            item.setType(Material.FEATHER);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aWind Boost");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Harness the power of wind to");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "boost your arrows, dealing more");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "damage and knockback.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.windBoost.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.archery.windBoost.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 4");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(8)+" Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(8, item);
            lore.clear();
        }
        if(playerLevel >= 5){
            // Neurotoxin
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aNeurotoxin");
            tippedArrowMeta.setColor(Color.LIME);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbue your arrow with neurotoxin,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "poisoning and slowing targets for");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "a medium duration.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.neurotoxin.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 5");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(10)+" Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            shopGUI.setItem(10, tippedArrowItem);
            lore.clear();
        }
        if(playerLevel >= 6){
            // Soulstring
            item.setType(Material.TRIPWIRE_HOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aSoulstring");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summons a bow companion that");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "periodically shoots very sharp");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "arrows at nearby enemies.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.soulstring.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.archery.soulstring.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 6");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(12)+" Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(12, item);
            lore.clear();
        }
        if(playerLevel >= 7){
            // Retreat
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aRetreat");
            tippedArrowMeta.setColor(Color.WHITE);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Shoots two arrows with the first");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "dealing less damage. After shooting,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "you fall back a short distance and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "your speed is temporarily increased.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.retreat.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 7");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(14)+" Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            shopGUI.setItem(14, tippedArrowItem);
            lore.clear();
            // Huayra's Fury
            item.setType(Material.SKELETON_SKULL);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aHuayra's Fury");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "You reawaken the wrath of Huayra,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "cursing critical arrows you shoot");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "to strike at incredible speeds and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "have the ability to pierce through");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "your enemies.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.huayraFury.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.archery.huayraFury.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 7");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(16)+" Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(16, item);
            lore.clear();
        }
        if(playerLevel >= 8){
            // Blast
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aBlast");
            tippedArrowMeta.setColor(Color.RED);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbue your arrow with gunpowder,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making it explode on impact.");;
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.blast.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 8");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(20)+" Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            shopGUI.setItem(20, tippedArrowItem);
            lore.clear();
        }
        if(playerLevel >= 9){
            // Royal Artillery
            item.setType(Material.NAUTILUS_SHELL);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aRoyal Artillery");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Calls in a continuous arrow");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "barrage on the targeted location.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.royalArtillery.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.archery.royalArtillery.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 9");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(22)+" Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(22, item);
            lore.clear();
        }
        if(playerLevel >= 10){
            // Whistling Wind
            item.setType(Material.SPECTRAL_ARROW);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"aWhistling Wind");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "An arrow that directs itself towards");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "every enemy around its shooter and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "returns after.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.archery.whistlingWind.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 10");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + prices.get(24)+" Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(24, item);
            lore.clear();
        }
    }
}
