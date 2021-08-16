package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Enrage {

    private final ParamaLegends plugin;
    private final SwordsmanListener swordsmanListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<Player> playersEnraging = new ArrayList<Player>();
    private final HashMap<Player, BukkitTask> playersEnragingTasks = new HashMap<>();


    public Enrage(ParamaLegends plugin, SwordsmanListener swordsmanListener){
        this.plugin = plugin;
        this.swordsmanListener = swordsmanListener;
    }

    public void castEnrage(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            swordsmanListener.sendCooldownMessage(player, "Enrage");
        } else {
            if(swordsmanListener.subtractMana(player, 150)){
                player.sendMessage(ChatColor.GREEN+"Enrage activated.");
                playersEnragingTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation(), 4, 1, 0.5, 1, 0);
                }, 0, 20));
                playersEnraging.add(player);
                plugin.getPlayersSilenced().add(player);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.GREEN+"Enrage wore off.");
                    playersEnraging.remove(player);
                    plugin.getPlayersSilenced().remove(player);
                    playersEnragingTasks.get(player).cancel();
                    playersEnragingTasks.remove(player);
                },  260);
                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        swordsmanListener.sendNoLongerCooldownMessage(player, "Enrage");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 900);
            }
        }
    }

    public List<Player> getPlayersEnraging(){
        return playersEnraging;
    }

}
