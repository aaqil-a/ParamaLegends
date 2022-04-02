package me.cuna.paramalegends.leaderboard;

import me.cuna.paramalegends.ParamaLegends;

public class LeaderboardManager {

    private final ParamaLegends plugin;
    public final NetWorth netWorthCriteria;
    public LeaderboardCommand leaderboardCommand;

    public LeaderboardManager(ParamaLegends plugin){
        this.plugin = plugin;
        this.netWorthCriteria = new NetWorth(plugin);

        initCommand();
    }

    public void initCommand(){
        leaderboardCommand = new LeaderboardCommand(plugin, this);
        plugin.getCommand("leaderboard").setExecutor(leaderboardCommand);
    }


}
