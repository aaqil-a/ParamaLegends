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

public class PlayerManagerListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    private final HashMap<Player, PlayerParama> players = new HashMap<>();

    public PlayerManagerListener(ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        //start blood moon task if not started
        if(plugin.bloodMoonSummonListener.bloodMoonTask == null){
            plugin.bloodMoonSummonListener.startBloodMoonTask();
        }
        players.put(event.getPlayer(), new PlayerParama(plugin, event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        plugin.savePlayerData(player);
        PlayerParama playerParama = getPlayerParama(event.getPlayer());
        if(playerParama != null){
            players.remove(player);
            playerParama.cancelAllTasks();
            playerParama.removeAllEntities();
            plugin.wiseOldManListener.gui.remove(player);
            plugin.wiseOldManListener.gui2.remove(player);
            plugin.seniorRanger.gui.remove(player);
            plugin.oddWares.gui.remove(player);
            plugin.suspiciousPeasant.gui.remove(player);
            plugin.banishedMagus.gui.remove(player);
            plugin.retiredWeaponsmith.gui.remove(player);
            plugin.earthAltarListener.gui.remove(player);
            plugin.natureAltarListener.gui.remove(player);
            plugin.alcoholListener.playersDrunk.remove(player);
            plugin.alcoholListener.playerBarrier.remove(player);
            plugin.alcoholListener.playerFallTasks.remove(player);
        }
    }

    public PlayerParama getPlayerParama(Player player){
        return players.get(player);
    }
}
