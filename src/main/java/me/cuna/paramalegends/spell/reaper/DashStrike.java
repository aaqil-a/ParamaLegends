package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
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
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Dash Strike");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            player.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(2));



            playerParama.cancelTask("DASHSTRIKE");
            playerParama.cancelTask("DASHSTRIKEEND");
            player.setInvulnerable(true);
            playerParama.addTask("DASHSTRIKEEND", Bukkit.getScheduler().runTaskLater(plugin, ()->{
                player.setInvulnerable(false);
            }, 20));
            playerParama.addTask("DASHSTRIKE",
                    Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                        for(Entity entity : player.getNearbyEntities(1, 2, 1).stream()
                                .filter(entity -> (entity instanceof Mob || entity instanceof Player)).toList()){
                            if(entity.equals(player)) continue;
                            Damageable mob = (Damageable) entity;
                            mob.damage(damage+0.034, player);
                        }
                    }, 0, 1));
            Bukkit.getScheduler().runTaskLater(plugin, ()->{playerParama.cancelTask("DASHSTRIKE");}, 13);
            playerParama.addToCooldown(this);
            playerParama.addToReaperRefreshCooldown("Dash Strike", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Dash Strike");
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
