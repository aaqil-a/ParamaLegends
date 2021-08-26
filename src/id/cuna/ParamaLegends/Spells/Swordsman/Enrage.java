package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Enrage implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 150;
    private final List<Player> playersEnraging = new ArrayList<Player>();
    private final HashMap<Player, BukkitTask> playersEnragingTasks = new HashMap<>();


    public Enrage(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Enrage");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                player.sendMessage(ChatColor.GREEN+"Enrage activated.");
                playersEnragingTasks.put(player, Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation(), 4, 1, 0.5, 1, 0);
                }, 0, 20));
                playersEnraging.add(player);
                playerParama.setSilenced(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.GREEN+"Enrage wore off.");
                    playersEnraging.remove(player);
                    playerParama.setSilenced(false);
                    playersEnragingTasks.get(player).cancel();
                    playersEnragingTasks.remove(player);
                },  260);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Enrage");
                        playerParama.removeFromCooldown(this);
                    }
                }, 900);
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }

    public List<Player> getPlayersEnraging(){
        return playersEnraging;
    }

}
