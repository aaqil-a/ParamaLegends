package me.cuna.paramalegends.spell.swordsman;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Enrage implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 150;
    private final int cooldown = 900;
    private final int duration = 260;

    public Enrage(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Enrage");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
                player.sendMessage(ChatColor.GREEN+"Enrage activated.");
                playerParama.addTask("ENRAGE",
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation(), 4, 1, 0.5, 1, 0);
                        }, 0, 20));
                player.setMetadata("ENRAGING", new FixedMetadataValue(plugin, "ENRAGING"));
                playerParama.setSilenced(true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.sendMessage(ChatColor.GREEN+"Enrage wore off.");
                    player.removeMetadata("ENRAGING", plugin);
                    playerParama.setSilenced(false);
                    playerParama.cancelTask("ENRAGE");
                },  duration);
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Enrage");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
            }
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
