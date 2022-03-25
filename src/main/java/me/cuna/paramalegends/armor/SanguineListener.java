package me.cuna.paramalegends.armor;


import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.lib.armorequip.ArmorEquipEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class SanguineListener implements Listener {

    private final ParamaLegends plugin;

    public SanguineListener(ParamaLegends plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEquipSanguine(ArmorEquipEvent event){
        if(event.getMethod().equals(ArmorEquipEvent.EquipMethod.BROKE) ||
            event.getMethod().equals(ArmorEquipEvent.EquipMethod.DEATH))
            return;

        if(event.getNewArmorPiece() != null && event.getNewArmorPiece().getItemMeta() != null){
            switch(event.getNewArmorPiece().getItemMeta().getDisplayName()){
                case ChatColor.COLOR_CHAR+"4Sanguine Chestplate"-> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()+2);
                    addSanguineEffect( event.getPlayer(), "CHESTPLATE");
                }
                case ChatColor.COLOR_CHAR+"4Sanguine Leggings" -> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()+2);
                    addSanguineEffect( event.getPlayer(), "LEGGINGS");
                }
                case ChatColor.COLOR_CHAR+"4Sanguine Helmet" -> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()+1);
                    addSanguineEffect( event.getPlayer(), "HELMET");

                }
                case ChatColor.COLOR_CHAR+"4Sanguine Boots" -> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()+1);
                    addSanguineEffect( event.getPlayer(), "BOOTS");

                }
            }
        }
    }

    @EventHandler
    public void onUnequipSanguine(ArmorEquipEvent event){
        if(event.getOldArmorPiece() != null && event.getOldArmorPiece().getItemMeta() != null){
            switch(event.getOldArmorPiece().getItemMeta().getDisplayName()){
                case ChatColor.COLOR_CHAR+"4Sanguine Chestplate" -> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()-2);
                    removeSanguineEffect( event.getPlayer(), "CHESTPLATE");
                }
                case ChatColor.COLOR_CHAR+"4Sanguine Leggings" -> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()-2);
                    removeSanguineEffect( event.getPlayer(), "LEGGINGS");
                }
                case ChatColor.COLOR_CHAR+"4Sanguine Helmet" -> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()-1);
                    removeSanguineEffect( event.getPlayer(), "HELMET");
                }
                case ChatColor.COLOR_CHAR+"4Sanguine Boots" -> {
                    event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                            .setBaseValue(event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()-1);
                    removeSanguineEffect( event.getPlayer(), "BOOTS");
                }
            }
        }
    }

    public void addSanguineEffect(Player player, String piece){
        PlayerParama playerParama = plugin.getPlayerParama(player);
        if(!playerParama.hasTask("SANGUINEEFFECT"+piece)){
            playerParama.addTask("SANGUINEEFFECT"+piece,
                    Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                        player.getWorld().spawnParticle(Particle.FALLING_LAVA, player.getEyeLocation(), 1, 0.35, 0.2, 0.35);
                    }, 0, 3));
        }
    }
    public void removeSanguineEffect(Player player, String piece){
        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.cancelTask("SANGUINEEFFECT"+piece);
    }

}
