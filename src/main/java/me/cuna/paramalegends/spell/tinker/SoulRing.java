package me.cuna.paramalegends.spell.tinker;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SoulRing implements SpellParama {

    private final ParamaLegends plugin;
    private final int healthCost = 2;
    private final int manaGain = 150;
    private final int cooldown = 500;

    public SoulRing(ParamaLegends plugin){
        this.plugin = plugin;
    }

    @Override
    public void castSpell(PlayerParama playerParama) {
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Soul Ring");
        } else {
            Player player = playerParama.getPlayer();
            if(player.getHealth()>healthCost){
                player.setHealth(player.getHealth()-healthCost);
            } else {
                player.setHealth(1);
            }
            playerParama.addMana(manaGain);
            playerParama.addToCooldown(this);
            playerParama.addToTinkerRefreshCooldown("SoulRing", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Soul Ring");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown));
        }
    }

    public int getManaCost(){
        return 0;
    }
    public int getCooldown() {return cooldown;}
}
