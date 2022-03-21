package me.cuna.paramalegends.food;

import me.cuna.paramalegends.ParamaLegends;

public class FoodManager {

    public ParamaLegends plugin;
    public FoodListener food;
    public FoodRecipes foodRecipes;

    public FoodManager(ParamaLegends plugin){
        this.plugin = plugin;
        food = new FoodListener(plugin);
        foodRecipes = new FoodRecipes(plugin);

        registerListeners();
    }

    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(food, plugin);
    }
}
