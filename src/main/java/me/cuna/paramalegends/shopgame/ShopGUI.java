package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopGUI {

    private Inventory gui;
    private ParamaLegends plugin;
    private GameShop shop;

    public ShopGUI(ParamaLegends plugin, GameShop shop, int size, String name){
        this.plugin = plugin;
        this.shop = shop;
        gui = Bukkit.createInventory(null,size, name);
    }

    public GameShop getGameShop(){
        return shop;
    }

    public void setItem(int slot, ItemStack item){
        gui.setItem(slot, item);
    }

    public Inventory getInventory(){
        return gui;
    }
}
