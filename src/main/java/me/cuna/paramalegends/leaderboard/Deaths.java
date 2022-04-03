package me.cuna.paramalegends.leaderboard;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Deaths extends LeaderboardCriteria {

    public Deaths(ParamaLegends plugin){
        super(plugin);
        this.name = "Deaths";
        this.criterion = "Deaths";
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
                    addToLeaderboard(player.getName(), player.getStatistic(Statistic.DEATHS));
                } else {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
                    if(offlinePlayer.getName() != null){
                        addToLeaderboard(offlinePlayer.getName(), offlinePlayer.getStatistic(Statistic.DEATHS));
                    }
                }
            }
            toUpdate.clear();
        }, 0, 1200);
    }
}
