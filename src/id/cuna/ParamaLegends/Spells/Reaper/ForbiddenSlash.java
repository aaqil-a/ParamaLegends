package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ForbiddenSlash implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 200;
    private final List<Player> playersForbiddenSlash = new ArrayList<>();

    public ForbiddenSlash(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Forbidden Slash");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            playersForbiddenSlash.add(player);
            player.sendMessage(ChatColor.GREEN+"You ready your scythe.");
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Forbidden Slash");
                    playerParama.removeFromCooldown(this);
                }
            }, 402);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public List<Player> getPlayersForbiddenSlash(){
        return playersForbiddenSlash;
    }
}
