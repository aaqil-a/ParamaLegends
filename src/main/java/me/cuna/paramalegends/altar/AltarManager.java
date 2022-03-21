package me.cuna.paramalegends.altar;

import me.cuna.paramalegends.ParamaLegends;

public class AltarManager {

    public EarthAltarListener earthAltar;
    public NatureAltarListener natureAltar;
    public StartAltarListener startAltar;
    public ParamaLegends plugin;

    public AltarManager(ParamaLegends plugin){
        this.plugin = plugin;
        earthAltar = new EarthAltarListener(plugin);
        natureAltar = new NatureAltarListener(plugin);
        startAltar = new StartAltarListener(plugin);

        registerListeners();
    }

    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(startAltar, plugin);
        plugin.getServer().getPluginManager().registerEvents(natureAltar, plugin);
        plugin.getServer().getPluginManager().registerEvents(earthAltar, plugin);
    }
}
