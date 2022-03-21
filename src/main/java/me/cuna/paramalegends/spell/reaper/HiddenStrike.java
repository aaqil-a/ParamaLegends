package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

public class HiddenStrike implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 20;
    private final int cooldown = 202;

    public HiddenStrike(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Hidden Strike");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            player.setMetadata("HIDDENSTRIKE", new FixedMetadataValue(plugin, "HIDDENSTRIKE"));
            player.sendMessage(ChatColor.GREEN+"You conceal your scythe.");
            playerParama.addToCooldown(this);
            playerParama.addToReaperRefreshCooldown("Hidden Strike", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Hidden Strike");
                    playerParama.removeFromCooldown(this);
                    playerParama.removeFromReaperRefreshCooldown("Hidden Strike");
                }
            }, cooldown));
        }
    }

    public int getManaCost(){
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
