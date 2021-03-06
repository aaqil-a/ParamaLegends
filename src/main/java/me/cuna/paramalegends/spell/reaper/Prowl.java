package me.cuna.paramalegends.spell.reaper;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class Prowl implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 50;
    private final int cooldown = 402;

    public Prowl(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)) {
            plugin.gameClassManager.sendCooldownMessage(playerParama, "Prowl");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            player.setMetadata("PROWL", new FixedMetadataValue(plugin, "PROWL"));
            player.playSound(player.getLocation(), Sound.ENTITY_WOLF_SHAKE, 1f, 1.5f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
            playerParama.addToCooldown(this);
            //increase attack speed
            playerParama.cancelTask("PROWLEND");
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED))
                    .setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).getDefaultValue()*2);
            playerParama.addTask("PROWLEND", Bukkit.getScheduler().runTaskLater(plugin, ()->{
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED))
                        .setBaseValue(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).getDefaultValue());
            }, 200));
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                player.removeMetadata("PROWL", plugin);
            },200);
            playerParama.addToReaperRefreshCooldown("Prowl", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.gameClassManager.sendNoLongerCooldownMessage(playerParama, "Prowl");
                    playerParama.removeFromCooldown(this);
                    playerParama.removeFromReaperRefreshCooldown("Prowl");
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
