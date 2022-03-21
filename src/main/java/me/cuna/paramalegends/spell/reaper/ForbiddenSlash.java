package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;

public class ForbiddenSlash implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 120;
    private final int cooldown = 202;

    public ForbiddenSlash(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Forbidden Slash");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            player.setMetadata("FORBIDDENSLASH", new FixedMetadataValue(plugin, "FORBIDDENSLASH"));
            player.sendMessage(ChatColor.GREEN+"You ready your scythe.");
            playerParama.addToCooldown(this);
            playerParama.addToReaperRefreshCooldown("Forbidden Slash", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Forbidden Slash");
                    playerParama.removeFromCooldown(this);
                    playerParama.removeFromReaperRefreshCooldown("Forbidden Slash");
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
