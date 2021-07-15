package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WindBoost {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<String> playersWindBoosted = new ArrayList<>();

    public WindBoost(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castWindBoost(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            archeryListener.sendCooldownMessage(player, "Wind Boost");
        } else if (archeryListener.subtractMana(player, 60)) {
            player.sendMessage(ChatColor.GREEN+"Wind Boost activated.");
            playersWindBoosted.add(player.getUniqueId().toString());
            playerCooldowns.add(player.getUniqueId().toString());
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0,1,0),8, 0.5, 0.5, 0.5, 0);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(ChatColor.GREEN+"Wind Boost wore off.");
                playersWindBoosted.remove(player.getUniqueId().toString());
            }, 240);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    archeryListener.sendNoLongerCooldownMessage(player, "Wind Boost");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 600);
        }
    }

    public List<String> getPlayersWindBoosted(){
        return playersWindBoosted;
    }

}
