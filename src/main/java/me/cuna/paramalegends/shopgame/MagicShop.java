package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class MagicShop extends GameShop {

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<>(){{
        put(2,10);
        put(4,20);
        put(6,30);
        put(8,60);
        put(10,80);
        put(12,200);
        put(14,200);
        put(16,300);
        put(20,400);
        put(24,600);
    }};

    public MagicShop(ParamaLegends plugin) {
        super(plugin, ChatColor.COLOR_CHAR+"5Ancient Tomes");
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
        ShopGUI shopGUI = new ShopGUI(plugin,  this,27,ChatColor.COLOR_CHAR+"5Ancient Tomes");
        playerParama.setOpenShopGui(shopGUI);
        int playerLevel = playerParama.getClassLevel(ClassGameType.MAGIC);

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

        // Fling Earth
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Fling Earth");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Gathers nearby earth into");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "a dense cube, then flinging");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "it towards whatever may be");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "in its path.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.flingEarth.getManaCost());
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1 second");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 1");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        shopGUI.setItem(2, item);
        lore.clear();

        if(playerLevel >= 2){
            // Ignite
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Ignite");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Sets targets ablaze within a");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "small area, dealing damage");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "over time.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.ignite.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.ignite.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 2");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "20 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(4, item);
            lore.clear();

        }
        if(playerLevel >= 3){
            // Gust
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Gust");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Pushes away targets within an");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "area of where you are facing.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.gust.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.gust.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 3");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(6, item);
            lore.clear();
        }
        if(playerLevel >= 4){
            // Life Drain
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Life Drain");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Periodically drains the life of");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "nearby beings when activated.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.lifeDrain.getManaCost()+" per second");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.lifeDrain.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 4");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "60 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(8, item);
            lore.clear();
        }
        if(playerLevel >= 5){
            // Blink
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Blink");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Instantaneously teleport to");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the targeted location.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.blink.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.blink.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 5");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "80 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(10, item);
            lore.clear();
        }
        if(playerLevel >= 6){
            // Summon Lightning
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Summon Lightning");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Call upon the wrath of the");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "thundergod to summon lightning");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "and smite all enemies in an");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "area.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.summonLightning.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.summonLightning.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 6");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(12, item);
            lore.clear();
        }
        if(playerLevel >= 7){
            // Illusory orb
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Illusory Orb");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Casts an arcane orb that hurts");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "any being it comes into contact");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "with. Casting the spell while an");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "orb is travelling teleports the");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "caster to its location.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.illusoryOrb.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.illusoryOrb.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 7");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(14, item);
            lore.clear();
        }
        if(playerLevel >= 8){
            // Dragon breath
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Dragon's Breath");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbuing your essence with draconic");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "powers, you unleash a wide dragon");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "breath attack in front of you");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "that harms all beings it comes");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "into contact with.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.dragonBreath.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.dragonBreath.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 8");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(16, item);
            lore.clear();
        }
        if(playerLevel >= 9){
            // Voices of the Damned
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Voices of the Damned");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summons a portal to the realm of");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the damned. Creatures will appear");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "from the portal and attack any");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "creatures that are in sight.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.voicesOfTheDamned.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.voicesOfTheDamned.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 9");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(20, item);
            lore.clear();
        }
        if(playerLevel >= 10){
            // Nova
            item.setType(Material.ENCHANTED_BOOK);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"5Nova");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "爆裂, 爆裂, la la la");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.gameClassManager.magic.nova.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.gameClassManager.magic.nova.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 10");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "600 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            shopGUI.setItem(24, item);
            lore.clear();
        }
    }


}
