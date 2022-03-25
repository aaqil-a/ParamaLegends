package me.cuna.paramalegends.leaderboard;

import com.mojang.datafixers.util.Pair;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Leaderboard {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final ArrayList<String> toUpdateNetWorth = new ArrayList<>();
    private final SortedSet<Pair<String, Integer>> netWorth = new TreeSet<>((o1, o2) -> {
        int lectrum1 = o1.getSecond();
        int lectrum2 = o2.getSecond();
        if(lectrum1 == lectrum2) return 0;
        else if(lectrum1 > lectrum2) return 1;
        else return -1;
    });

    public Leaderboard(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.dataManager;

        initNetWorth();
    }

    public void initNetWorth(){
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

    public void addToNetWorth(String name, int value){
        Pair<String, Integer> player = new Pair<>(name, value);

        if(isInTopNetWorth(player) != null){
            netWorth.remove(isInTopNetWorth(player));
            netWorth.add(player);
        } else if(netWorth.size() < 10){
            netWorth.add(player);
        } else if((netWorth.first().getSecond() < value)){
            netWorth.remove(netWorth.first());
            netWorth.add(player);
        }
    }

    public Pair<String, Integer> isInTopNetWorth(Pair<String, Integer> player){
        for(Pair<String, Integer> top : netWorth){
            if(top.getFirst().equals(player.getFirst())) return top;
        }
        return null;
    }

    public SortedSet<Pair<String, Integer>> getNetWorth(){
        return netWorth;
    }

    public void updateNetWorth(String uuid){
        if(!toUpdateNetWorth.contains(uuid))toUpdateNetWorth.add(uuid);
    }
}
