package me.cuna.paramalegends.leaderboard;

import com.mojang.datafixers.util.Pair;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class LeaderboardCriteria {

    protected final ParamaLegends plugin;
    protected final DataManager data;
    protected final Set<String> toUpdate = new HashSet<>();
    protected final SortedSet<Pair<String, Integer>> leaderboard = new TreeSet<>((o1, o2) -> {
        int lectrum1 = o1.getSecond();
        int lectrum2 = o2.getSecond();
        return Integer.compare(lectrum2, lectrum1);
    });
    protected String name = "";
    protected String criterion = "";

    public LeaderboardCriteria(ParamaLegends plugin) {
        this.plugin = plugin;
        this.data = plugin.dataManager;
    }

    protected void init(){
        if(data.getConfig().getConfigurationSection("players") != null) {
            for (String uuid : data.getConfig().getConfigurationSection("players").getKeys(false)) {
                if (uuid.length() == 36) toUpdate.add(uuid);
            }
        }
        startUpdateTask();
    }

    protected Pair<String, Integer> findInLeaderboard(Pair<String, Integer> player){
        for(Pair<String, Integer> top : leaderboard){
            if(top.getFirst().equals(player.getFirst())) return top;
        }
        return null;
    }

    protected void addToLeaderboard(String name, int value){
        Pair<String, Integer> player = new Pair<>(name, value);
        leaderboard.remove(findInLeaderboard(player));
        leaderboard.add(player);
    }

    protected SortedSet<Pair<String, Integer>> getLeaderboard(){
        return leaderboard;
    }

    public void update(String uuid){
        toUpdate.add(uuid);
    }

    protected String getName(){
        return this.name;
    }

    protected String getCriterion(){
        return this.criterion;
    }

    protected abstract void startUpdateTask();
}
