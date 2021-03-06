package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SwordsmanShop extends GameShop {

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<Integer, Integer>(){{
        put(2,30);
        put(4,100);
        put(6,200);
        put(8,250);
        put(10,300);
        put(12,400);
        put(14,500);
    }};

    public SwordsmanShop(ParamaLegends plugin) {
        super(plugin, ChatColor.COLOR_CHAR+"2Dull Anvil");
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
        ShopGUI shopGUI = new ShopGUI(plugin,  this,27,ChatColor.COLOR_CHAR+"2Swordsman Buffs");
        playerParama.setOpenShopGui(shopGUI);
        int playerLevel = plugin.getPlayerParama(player).getClassLevel(ClassGameType.SWORDSMAN);

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + plugin.getPlayerParama(player).getLectrum());
        meta.setLore(lore);
        item.setItemMeta(meta);
        shopGUI.setItem(0, item);
        lore.clear();

        if(playerLevel >= 3){
            // Shields Up
            item.setType(Material.SHIELD);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"2Shields Up");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Reduces incoming damage and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "reflects some back at the");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "enemy.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.swordsman.shieldsUp.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.swordsman.shieldsUp.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 3");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(2, item);
            lore.clear();
        }
        if(playerLevel >= 5){
            // Phoenix Dive
            item.setType(Material.FIRE_CHARGE);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"2Phoenix Dive");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Leaps through the air and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "deals burn damage over time");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "upon landing.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.swordsman.phoenixDive.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.swordsman.phoenixDive.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 5");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "100 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(4, item);
            lore.clear();
        }
        if(playerLevel >= 6){
            // Enrage
            item.setType(Material.BLAZE_POWDER);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"2Enrage");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Tremendously increase critical");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "damage and critical chance but");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "be unable to cast abilities");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "during the duration.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.swordsman.enrage.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.swordsman.enrage.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 6");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(6, item);
            lore.clear();
        }
        if(playerLevel >= 7){
            // Onslaught
            item.setType(Material.GUNPOWDER);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"2Onslaught");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Strike enemies around you");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "with a flurry of attacks");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "of astonishing speed.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.swordsman.onslaught.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.swordsman.onslaught.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 7");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "250 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(8, item);
            lore.clear();
        }
        if(playerLevel >= 8){
            // Terrifying Cruelty
            item.setType(Material.MAGMA_CREAM);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"2Terrifying Cruelty");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Radiate an intimidating aura");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "causing enemies to be afraid,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "taking more damage and missing");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "their attacks.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.swordsman.terrifyingCruelty.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.swordsman.terrifyingCruelty.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 8");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(10, item);
            lore.clear();
        }
        if(playerLevel >= 9){
            // Superconducted
            item.setType(Material.IRON_SWORD);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"2Superconducted");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Shocks all enemies around");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "you with chaotic discharges that");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "temporarily blinds them.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.swordsman.superconducted.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.swordsman.superconducted.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 9");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(12, item);
            lore.clear();
        }
        if(playerLevel >= 10){
            // Calamity
            item.setType(Material.NETHER_STAR);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"2Calamity");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summon a raging chaotic storm");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "that strikes down enemies around");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "you. Your hits are guaranteed");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "critical and incoming damage is");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "reduced for its duration.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.swordsman.calamity.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.swordsman.calamity.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Swordsmanship 10");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "500 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(14, item);
            lore.clear();
        }
    }
}
