package me.cuna.paramalegends.armor;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.lib.armorequip.ArmorListener;
import me.cuna.paramalegends.lib.armorequip.DispenserArmorListener;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class ArmorManager {

    public ParamaLegends plugin;
    public SanguineListener sanguine;

    public ArmorManager(ParamaLegends plugin){
        this.plugin = plugin;
        sanguine = new SanguineListener(plugin);

        registerListeners();
    }

    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(sanguine, plugin);


        //initialize armor events
        Bukkit.getPluginManager().registerEvents(new ArmorListener(new ArrayList<>()), plugin);
        Bukkit.getPluginManager().registerEvents(new DispenserArmorListener(), plugin);
    }
}
