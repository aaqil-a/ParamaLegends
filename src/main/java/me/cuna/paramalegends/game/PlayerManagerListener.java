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
        PlayerParama player = plugin.getPlayerParama(event.getPlayer());
        if(player != null){
            player.cancelAllTasks();
            player.removeAllEntities();
        }
    }

    public PlayerParama getPlayerParama(Player player){
        return players.get(player);
    }
}
