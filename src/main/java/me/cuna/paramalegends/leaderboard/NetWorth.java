package me.cuna.paramalegends.leaderboard;

import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class NetWorth extends LeaderboardCriteria {

    public NetWorth(ParamaLegends plugin){
        super(plugin);
        this.name = "Net Worth";
        this.criterion = "Lectrum";
        init();
    }

    //Task to update net worth leaderboard
    public void startUpdateTask(){
        Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(String uuid : toUpdate) {
                Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                if(player != null)
                    addToLeaderboard(player.getName(), plugin.getPlayerParama(player).getLectrum());
                else {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
                    if(offlinePlayer.getName() != null){
                        addToLeaderboard(offlinePlayer.getName(), data.getConfig().getInt("players."+uuid+".lectrum"));
                    }
                }
            }
            toUpdate.clear();
        }, 0, 1200);
    }
}
