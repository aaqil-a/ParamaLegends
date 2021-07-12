package id.cuna.ParamaLegends.NPCListener.NPCShop;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.NPCListener.NPCShopListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OddReseller extends NPCShopListener {

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<Integer, Integer>(){{
        put(2,10);
    }};

    public OddReseller(ParamaLegends plugin) {
        super(plugin, "§eOdd Reseller");
    }

    //Get prices
    @Override
    public HashMap<Integer, Integer> getPrices(){
        return prices;
    }

    //Send player message when opening gui
    @Override
    public String getNPCMessage(){
        return ChatColor.YELLOW + "" + ChatColor.ITALIC + "Wares from all over the world are for sale.";
    }

    //Attack player when npc attacked according to npc type
    @Override
    public void NPCAttack(Player player, Entity npc){
        player.sendMessage(ChatColor.GOLD + "He appears to be safeguarded by a sturdy shield.");
    }

    //Create gui of shop
    @Override
    public Inventory createGui(Player player, DataManager data){
        Inventory gui;
        gui = Bukkit.createInventory(null,9, "§eOdd Reseller's Wares");


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

        // Empty Tome
        item.setType(Material.BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Empty Tome");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "An empty book surging");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "with magical potential.");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        return gui;
    }
}
