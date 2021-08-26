package id.cuna.ParamaLegends.GameListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

    public PlayerParama getPlayerParama(Player player){
        return players.get(player);
    }
}
