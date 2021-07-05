package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DestinyListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;

    public DestinyListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Set default data of player when joining for the first time
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = (Player) event.getPlayer();

        if (!data.getConfig().contains("players." + player.getUniqueId().toString() + ".joined")){
            data.getConfig().set("players." + player.getUniqueId().toString() + ".joined", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", 50);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".swordsmanship", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".swordsmanshipexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".archery", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".archeryexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".magic", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".magicexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".mining", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".miningexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".reaper", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".reaperexp", 0);
            data.saveConfig();
        }

    }
}
