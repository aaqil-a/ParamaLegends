package me.cuna.paramalegends.boss;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.boss.fight.BloodMoonListener;
import me.cuna.paramalegends.boss.fight.DragonFightListener;
import me.cuna.paramalegends.boss.fight.NatureFightListener;
import me.cuna.paramalegends.boss.fight.RaidFightListener;
import me.cuna.paramalegends.boss.summon.BloodMoonSummonListener;
import me.cuna.paramalegends.boss.summon.NatureSummonListener;
import me.cuna.paramalegends.boss.summon.RaidSummonListener;

public class BossManager {

    public ParamaLegends plugin;

    public RaidFightListener raidFight;
    public NatureFightListener natureFight;
    public DragonFightListener dragonFight;
    public BloodMoonListener bloodMoon;

    public RaidSummonListener raidSummon;
    public NatureSummonListener natureSummon;
    public BloodMoonSummonListener bloodMoonSummon;

    public BossManager(ParamaLegends plugin){
        this.plugin = plugin;

        // boss fights
        raidFight = new RaidFightListener(plugin);
        natureFight = new NatureFightListener(plugin);
        dragonFight = new DragonFightListener(plugin);
        bloodMoon = new BloodMoonListener(plugin);

        // boss summons
        raidSummon = new RaidSummonListener(plugin);
        natureSummon = new NatureSummonListener(plugin);
        bloodMoonSummon = new BloodMoonSummonListener(plugin);

        registerListeners();
    }

    public void registerListeners(){
        // boss fights
        plugin.getServer().getPluginManager().registerEvents(raidFight, plugin);
        plugin.getServer().getPluginManager().registerEvents(natureFight, plugin);
        plugin.getServer().getPluginManager().registerEvents(dragonFight, plugin);
        plugin.getServer().getPluginManager().registerEvents(bloodMoon, plugin);

        // boss summons
        plugin.getServer().getPluginManager().registerEvents(raidSummon, plugin);
        plugin.getServer().getPluginManager().registerEvents(natureSummon, plugin);
        plugin.getServer().getPluginManager().registerEvents(bloodMoonSummon, plugin);

    }
}
