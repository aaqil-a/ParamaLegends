package me.cuna.paramalegends.leaderboard;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayTime extends LeaderboardCriteria {

    public PlayTime(ParamaLegends plugin){
        super(plugin);
        this.name = "Total Playtime";
        this.criterion = "Hours";
        init();
    }

    //Task to update net worth leaderboard
    public void startUpdateTask(){
        Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for (Player player: plugin.getServer().getOnlinePlayers()){
                toUpdate.add(player.getUniqueId().toString());
            }
            for(String uuid : toUpdate) {
                Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                if(player != null) {
                    addToLeaderboard(player.getName(), player.getStatistic(Statistic.PLAY_ONE_MINUTE)/72_000);
                } else {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
                    if(offlinePlayer.getName() != null){
                        addToLeaderboard(offlinePlayer.getName(), offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE)/72_000);
                    }
                }
            }
            toUpdate.clear();
        }, 0, 1200);
    }
}
