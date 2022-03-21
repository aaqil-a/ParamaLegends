package me.cuna.paramalegends.game;

import me.cuna.paramalegends.ParamaLegends;

public class GameManager {

    public ParamaLegends plugin;
    public DamageModifyingListener damageModifier;
    public ExperienceListener experience;
    public MobSpawnListener mobSpawnModifier;
    public PlayerShopListener playerShop;
    public SetupListener setup;
    public WorldRuleListener worldRule;

    public GameManager(ParamaLegends plugin){
        this.plugin = plugin;
        damageModifier = new DamageModifyingListener(plugin);
        experience = new ExperienceListener(plugin);
        mobSpawnModifier = new MobSpawnListener(plugin);
        playerShop = new PlayerShopListener(plugin);
        setup = new SetupListener(plugin);
        worldRule = new WorldRuleListener(plugin);
        
        registerListeners();
    }
    
    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(mobSpawnModifier, plugin);
        plugin.getServer().getPluginManager().registerEvents(worldRule, plugin);
        plugin.getServer().getPluginManager().registerEvents(experience, plugin);
        plugin.getServer().getPluginManager().registerEvents(damageModifier, plugin);
        plugin.getServer().getPluginManager().registerEvents(setup, plugin);
        plugin.getServer().getPluginManager().registerEvents(playerShop, plugin);
    }


}
