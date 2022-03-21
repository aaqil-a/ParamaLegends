package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class WindBoost implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 60;
    private final int duration = 280;
    private final int cooldown = 600;

    public WindBoost(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Wind Boost");
        } else if (playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            player.sendMessage(ChatColor.GREEN+"Wind Boost activated.");
            player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, player.getLocation().add(0,1,0),8, 0.5, 0.5, 0.5, 0);
            player.setMetadata("WINDBOOSTPARAMA", new FixedMetadataValue(plugin, "WINDBOOSTPARAMA"));

            //add player to cooldown
            playerParama.addToCooldown(this);

            //wind boost expire
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(ChatColor.GREEN+"Wind Boost wore off.");
                player.removeMetadata("WINDBOOSTPARAMA", plugin);
            }, duration);

            //remove fro mcooldown
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Wind Boost");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getDuration() {return duration;}
    public int getCooldown() {return cooldown;}

}
