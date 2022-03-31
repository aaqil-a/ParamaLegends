package me.cuna.paramalegends.game;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class PlayerManager implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;

    private final HashMap<Player, PlayerParama> players = new HashMap<>();
    private final int[] maxMana = {0,50,100,150,200,250,300,400,500,600,800};
    private final int[] manaRegen = {0,1,2,2,3,3,4,5,6,7,8};

    public PlayerManager(ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        //start blood moon task if not started
        if(plugin.bossManager.bloodMoonSummon.bloodMoonTask == null){
            plugin.bossManager.bloodMoonSummon.startBloodMoonTask();
        }
        players.put(event.getPlayer(), new PlayerParama(plugin, event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        PlayerParama playerParama = getPlayerParama(event.getPlayer());

        data.savePlayerData(playerParama);
        data.toSave.remove(playerParama);

        if(playerParama.hasParty()){
            playerParama.getParty().leave(playerParama);
        }

        players.remove(player);
        playerParama.cancelAllTasks();
        playerParama.removeAllEntities();

        // remove player from plugin memory
        plugin.altarManager.earthAltar.gui.remove(player);
        plugin.altarManager.natureAltar.gui.remove(player);
        plugin.alcoholManager.alcohol.playersDrunk.remove(player);
        plugin.alcoholManager.alcohol.playerBarrier.remove(player);
        plugin.alcoholManager.alcohol.playerFallTasks.remove(player);
        plugin.commandManager.voteKick.playerVotesMap.remove(player);
    }

    public PlayerParama getPlayerParama(Player player){
        return players.get(player);
    }

    public int[] getMaxMana(){
        return maxMana;
    }
    public int[] getManaRegen(){
        return manaRegen;
    }
}
