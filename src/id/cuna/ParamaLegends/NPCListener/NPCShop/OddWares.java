package id.cuna.ParamaLegends.NPCListener.NPCShop;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.NPCListener.NPCShopListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OddWares extends NPCShopListener {

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<>(){{
        put(2,1);
        put(4,3);
        put(6,4);
        put(8,5);
    }};

    public OddWares(ParamaLegends plugin) {
        super(plugin, "§6Odd Wares");
    }

    //Get prices
    @Override
    public HashMap<Integer, Integer> getPrices(){
        return prices;
    }

    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        Inventory gui;
        gui = Bukkit.createInventory(null,9, "§6Odd Wares");

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

        // Classic Arrows
        item.setType(Material.ARROW);
        meta.setDisplayName("");
        item.setAmount(8);
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "1 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        // Healing Potion
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setDisplayName(ChatColor.RESET + "§6Healing Potion");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "3 Lectrum");
        potionMeta.setLore(lore);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1), true);
        potionMeta.setColor(Color.FUCHSIA);
        potion.setItemMeta(potionMeta);
        gui.setItem(4, potion);
        lore.clear();

        // Healing Potion
        potionMeta.setDisplayName(ChatColor.RESET + "§6Regeneration Potion");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "4 Lectrum");
        potionMeta.setLore(lore);
        potionMeta.removeCustomEffect(PotionEffectType.HEAL);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 260, 1), true);
        potionMeta.setColor(Color.PURPLE);
        potion.setItemMeta(potionMeta);
        gui.setItem(6, potion);
        lore.clear();

        // Expanse Fund
        item.setType(Material.DIAMOND);
        item.setAmount(1);
        meta.setDisplayName(ChatColor.GOLD + "Expanse Fund");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Expand safe zone region.");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Every player can contribute");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "to the fund.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Remaining lectrum required: " + data.getConfig().getInt("world.expanseFund"));
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "5 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

        return gui;
    }


    //Purchase item from gui
    @Override
    public boolean giveItem(Inventory inventory, ItemStack item){
        if(item.getType().equals(Material.ARROW)){
            ItemStack arrow = new ItemStack(Material.ARROW, 8);
            inventory.addItem(arrow);
        } else {
            ItemStack newItem = item.clone();
            ItemMeta meta = newItem.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(lore.size()-1);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            newItem.setItemMeta(meta);
            inventory.addItem(newItem);
        }
        return true;
    }
}
