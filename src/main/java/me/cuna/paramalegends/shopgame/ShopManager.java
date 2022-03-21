package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.ParamaLegends;

public class ShopManager {

    public ParamaLegends plugin;    
    public Destiny destiny;
    public MagicShop magicShop;
    public ArcheryShop archeryShop;
    public SwordsmanShop swordsmanShop;
    public ReaperShop reaperShop;
    public GeneralShop generalShop;

    public ShopManager(ParamaLegends plugin){
        this.plugin = plugin;
        magicShop = new MagicShop(plugin);
        archeryShop = new ArcheryShop(plugin);
        swordsmanShop = new SwordsmanShop(plugin);
        reaperShop = new ReaperShop(plugin);
        generalShop = new GeneralShop(plugin);
        destiny = new Destiny(plugin);

        registerListeners();
    }
    
    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(magicShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(archeryShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(swordsmanShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(reaperShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(generalShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(destiny, plugin);
    }
}
