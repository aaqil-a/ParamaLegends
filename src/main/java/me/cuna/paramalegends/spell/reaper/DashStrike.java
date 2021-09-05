package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DashStrike implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 75;
    private final int cooldown = 402;
    private final int damage = 10;

    public DashStrike(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Dash Strike");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(2));
            // how to damage?
            playerParama.cancelTask("DASHSTRIKE");
            playerParama.addTask("DASHSTRIKE",
                    Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                        for(Entity entity : player.getNearbyEntities(1, 3, 1).stream().filter(entity -> entity instanceof Mob).toList()){
                            Mob mob = (Mob) entity;
                            mob.damage(damage+0.034, player);
                        }
                    }, 0, 4));
            Bukkit.getScheduler().runTaskLater(plugin, ()->{playerParama.cancelTask("DASHSTRIKE");}, 13);
            playerParama.addToCooldown(this);
            playerParama.addToReaperRefreshCooldown("Dash Strike", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Dash Strike");
                    playerParama.removeFromCooldown(this);
                    playerParama.removeFromReaperRefreshCooldown("Dash Strike");
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
