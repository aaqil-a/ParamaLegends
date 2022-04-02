package me.cuna.paramalegends.leaderboard;

import com.mojang.datafixers.util.Pair;

import java.util.SortedSet;

public interface LeaderboardCriteria {

    public void init();
    public SortedSet<Pair<String, Integer>> getLeaderboard();
    public String getName();
    public String getCriterion();

}
