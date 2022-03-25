package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopGUI {

    private Inventory gui;
    private ParamaLegends plugin;

    public ShopGUI(ParamaLegends plugin, int size, String name){
        this.plugin = plugin;
        gui = Bukkit.createInventory(null,size, name);
    }

    public void setItem(int slot, ItemStack item){
        gui.setItem(slot, item);
    }

    public Inventory getGui(){
        return gui;
    }
}
