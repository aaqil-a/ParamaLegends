package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ReaperListener;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class Rejuvenate implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 50;
    private final int cooldown = 1200;

    public Rejuvenate(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Rejuvenate");
        } else if(playerParama.subtractMana(manaCost)){
            playerParama.removeFromCooldown(plugin.reaperListener.blindingSand);
            playerParama.removeFromCooldown(plugin.reaperListener.dashStrike);
            playerParama.removeFromCooldown(plugin.reaperListener.forbiddenSlash);
            playerParama.removeFromCooldown(plugin.reaperListener.gutPunch);
            playerParama.removeFromCooldown(plugin.reaperListener.hiddenStrike);
            playerParama.removeFromCooldown(plugin.reaperListener.prowl);
            playerParama.addToCooldown(this);
            playerParama.refreshReaperCooldown.keySet().forEach(key -> {
                playerParama.refreshReaperCooldown.get(key).cancel();
            });
            Player player = playerParama.getPlayer();
            player.playSound(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 1f, 1.5f);
            playerParama.refreshReaperCooldown.clear();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Rejuvenate");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown);
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
