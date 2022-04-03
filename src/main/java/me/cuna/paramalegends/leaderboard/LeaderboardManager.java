package me.cuna.paramalegends.leaderboard;

import me.cuna.paramalegends.ParamaLegends;

public class LeaderboardManager {

    private final ParamaLegends plugin;
    public final NetWorth netWorthCriteria;
    public final PlayTime playTimeCriteria;
    public final PlayerKills playerKillsCriteria;
    public final MobKills mobKillsCriteria;
    public final Deaths deathsCriteria;
    public LeaderboardCommand leaderboardCommand;

    public LeaderboardManager(ParamaLegends plugin){
        this.plugin = plugin;
        this.netWorthCriteria = new NetWorth(plugin);
        this.playTimeCriteria = new PlayTime(plugin);
        this.playerKillsCriteria = new PlayerKills(plugin);
        this.mobKillsCriteria = new MobKills(plugin);
        this.deathsCriteria = new Deaths(plugin);


        initCommand();
    }

    public void initCommand(){
        leaderboardCommand = new LeaderboardCommand(plugin, this);
        plugin.getCommand("leaderboard").setExecutor(leaderboardCommand);
    }


}
