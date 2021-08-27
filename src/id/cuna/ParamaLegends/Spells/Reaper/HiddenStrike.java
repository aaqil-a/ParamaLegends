package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class HiddenStrike implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 20;

    public HiddenStrike(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Hidden Strike");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            player.setMetadata("HIDDENSTRIKE", new FixedMetadataValue(plugin, "HIDDENSTRIKE"));
            player.sendMessage(ChatColor.GREEN+"You conceal your scythe.");
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Hidden Strike");
                    playerParama.removeFromCooldown(this);
                }
            }, 202);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
