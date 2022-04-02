package me.cuna.paramalegends.leaderboard;

import com.mojang.datafixers.util.Pair;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class NetWorth implements LeaderboardCriteria {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final Set<String> toUpdateNetWorth = new HashSet<>();
    private final SortedSet<Pair<String, Integer>> netWorth = new TreeSet<>((o1, o2) -> {
        int lectrum1 = o1.getSecond();
        int lectrum2 = o2.getSecond();
        return Integer.compare(lectrum2, lectrum1);
    });

    public NetWorth(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.dataManager;

        init();
    }

    public String getName(){
        return "Net Worth";
    }

    public String getCriterion(){
        return "Lectrum";
    }

    public void init(){
        if(data.getConfig().getConfigurationSection("players") != null) {
            for (String uuid : data.getConfig().getConfigurationSection("players").getKeys(false)) {
                if (uuid.length() == 36) updateNetWorth(uuid);
            }
        }
        startNetWorthUpdateTask();
    }

    //Task to update net worth leaderboard
    public void startNetWorthUpdateTask(){
        Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(String uuid : toUpdateNetWorth) {
                Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
                if(player != null)
                    addToNetWorth(player.getName(), plugin.getPlayerParama(player).getLectrum());
                else {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
                    if(offlinePlayer.getName() != null){
                        addToNetWorth(offlinePlayer.getName(), data.getConfig().getInt("players."+uuid+".lectrum"));
                    }
                }
            }
            toUpdateNetWorth.clear();
        }, 0, 1200);
    }

    public void updateNetWorth(String uuid){
        toUpdateNetWorth.add(uuid);
    }

    public void addToNetWorth(String name, int value){
        Pair<String, Integer> player = new Pair<>(name, value);
        netWorth.add(player);
    }

    public SortedSet<Pair<String, Integer>> getLeaderboard(){
        return netWorth;
    }

}
