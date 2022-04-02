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
        int l1 = o1.getSecond();
        int l2 = o2.getSecond();
        if(l1 == l2) return String.CASE_INSENSITIVE_ORDER.compare(o1.getFirst(), o2.getFirst());
        return Integer.compare(l2, l1);
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
        if(findInLeaderboard(player) != null){
            leaderboard.remove(findInLeaderboard(player));
        }
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
