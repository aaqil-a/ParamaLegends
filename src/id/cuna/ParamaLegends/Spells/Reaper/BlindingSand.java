package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ReaperListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.SwordsmanListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;

public class BlindingSand {

    private final ParamaLegends plugin;
    private final ReaperListener reaperListener;

    private final List<String> playerCooldowns = new ArrayList<>();

    public BlindingSand(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
        this.reaperListener = reaperListener;
    }

    public void castBlindingSand(Player player){
        if (playerCooldowns.contains(player.getUniqueId().toString())) {
            reaperListener.sendCooldownMessage(player, "Blinding Sand");
        } else {

            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    reaperListener.sendNoLongerCooldownMessage(player, "Blinding Sand");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 320);
        }
    }

}
