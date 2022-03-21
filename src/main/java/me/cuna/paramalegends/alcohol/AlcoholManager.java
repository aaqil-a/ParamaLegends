package me.cuna.paramalegends.alcohol;

import me.cuna.paramalegends.ParamaLegends;

public class AlcoholManager {

    public ParamaLegends plugin;
    public AlcoholRecipes alcoholRecipes;
    public AlcoholListener alcohol;
    public WineryListener winery;

    public AlcoholManager(ParamaLegends plugin){
        this.plugin = plugin;

        alcoholRecipes = new AlcoholRecipes(plugin);
        alcohol = new AlcoholListener(plugin);
        winery = new WineryListener(plugin);

        registerListeners();
    }

    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(winery, plugin);
        plugin.getServer().getPluginManager().registerEvents(alcohol, plugin);
    }
}
