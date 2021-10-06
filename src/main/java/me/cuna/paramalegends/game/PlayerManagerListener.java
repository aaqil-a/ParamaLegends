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
        players.put(event.getPlayer(), new PlayerParama(plugin, event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        PlayerParama playerParama = getPlayerParama(event.getPlayer());
        if(playerParama != null){
            Player player = playerParama.getPlayer();
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
            plugin.archeryListener.whistlingWind.entitiesWhistlingWind.remove(player);
            plugin.archeryListener.whistlingWind.targetWhistlingWind.remove(player);
        }
    }

    public PlayerParama getPlayerParama(Player player){
        return players.get(player);
    }
}
