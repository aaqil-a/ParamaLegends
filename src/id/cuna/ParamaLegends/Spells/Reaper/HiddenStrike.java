package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import java.util.ArrayList;
import java.util.List;

public class HiddenStrike implements Listener {

    private final ParamaLegends plugin;
    private ReaperListener reaperListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<Player> playersHiddenStrike = new ArrayList<>();

    public HiddenStrike(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
        this.reaperListener = reaperListener;
    }

    public void castHiddenStrike(Player player){
        if (playerCooldowns.contains(player.getUniqueId().toString())) {
            reaperListener.sendCooldownMessage(player, "Hidden Strike");
        } else if(reaperListener.subtractMana(player, 20)){
            playersHiddenStrike.add(player);
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    reaperListener.sendNoLongerCooldownMessage(player, "Hidden Strike");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 202);
        }
    }

    public List<Player> getPlayersHiddenStrike(){
        return playersHiddenStrike;
    }
}
