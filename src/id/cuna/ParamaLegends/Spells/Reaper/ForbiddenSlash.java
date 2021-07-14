package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ForbiddenSlash implements Listener {

    private final ParamaLegends plugin;
    private ReaperListener reaperListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<Player> playersForbiddenSlash = new ArrayList<>();

    public ForbiddenSlash(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
        this.reaperListener = reaperListener;
    }

    public void castForbiddenSlash(Player player){
        if (playerCooldowns.contains(player.getUniqueId().toString())) {
            reaperListener.sendCooldownMessage(player, "Forbidden Slash");
        } else if(reaperListener.subtractMana(player, 200)){
            playersForbiddenSlash.add(player);
            player.sendMessage(ChatColor.GREEN+"You ready your scythe.");
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    reaperListener.sendNoLongerCooldownMessage(player, "Forbidden Slash");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 402);
        }
    }

    public List<Player> getPlayersForbiddenSlash(){
        return playersForbiddenSlash;
    }
}
