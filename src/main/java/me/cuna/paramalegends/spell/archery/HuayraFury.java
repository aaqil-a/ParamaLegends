package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class HuayraFury implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 150;
    private final int duration = 400;
    private final int cooldown = 1200;

    public HuayraFury(ParamaLegends plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Huayra's Fury");
        } else if(playerParama.subtractMana(manaCost)){
            Player player = playerParama.getPlayer();
            player.sendMessage(ChatColor.GREEN+"Huayra's Fury activated.");
            playerParama.addToCooldown(this);
            player.setMetadata("HUAYRAFURY", new FixedMetadataValue(plugin, true));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1f, 1.5f);
            //huayra fury expire
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(ChatColor.GREEN+"Huayra's Fury wore off.");
                player.removeMetadata("HUAYRAFURY", plugin);
            }, duration);
            //remove fro mcooldown
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Huayra's Fury");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown);//1200
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        if(event.getEntity().hasMetadata("HUAYRAARROW") && event.getHitBlock() != null){
            event.getEntity().setGlowing(false);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getDuration(){ return duration;}
    public int getCooldown(){return cooldown;}
}

