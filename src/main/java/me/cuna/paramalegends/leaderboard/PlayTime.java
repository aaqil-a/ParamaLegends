package me.cuna.paramalegends.leaderboard;

import com.mojang.datafixers.util.Pair;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayTime implements LeaderboardCriteria {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final Set<String> toUpdatePlayTime = new HashSet<>();
    private final SortedSet<Pair<String, Integer>> playTime = new TreeSet<>((o1, o2) -> {
        int lectrum1 = o1.getSecond();
        int lectrum2 = o2.getSecond();
        return Integer.compare(lectrum2, lectrum1);
    });

    public PlayTime(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.dataManager;

        init();
    }

    public String getName(){
        return "Total Playtime";
    }

    public String getCriterion(){
        return "Hours";
    }

    public void init(){
        if(data.getConfig().getConfigurationSection("players") != null) {
            for (String uuid : data.getConfig().getConfigurationSection("players").getKeys(false)) {
                if (uuid.length() == 36) updatePlayTime(uuid);
            }
        }
        startPlayTimeUpdateTask();
    }

    //Task to update net worth leaderboard
    public void startPlayTimeUpdateTask(){
        Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for (Player player: plugin.getServer().getOnlinePlayers()){
                toUpdatePlayTime.add(player.getUniqueId().toString());
            }
            for(String uuid : toUpdatePlayTime) {
                Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                if(player != null)
                    addToPlayTime(player.getName(), player.getStatistic(Statistic.PLAY_ONE_MINUTE));
                else {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
                    if(offlinePlayer.getName() != null){
                        addToPlayTime(offlinePlayer.getName(), offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE));
                    }
                }
            }
            toUpdatePlayTime.clear();
        }, 0, 1200);
    }

    public void updatePlayTime(String uuid){
        toUpdatePlayTime.add(uuid);
    }

    public void addToPlayTime(String name, int value){
        value = value/72_000;
        Pair<String, Integer> player = new Pair<>(name, value);
        playTime.add(player);
    }

    public SortedSet<Pair<String, Integer>> getLeaderboard(){
        return playTime;
    }

}
