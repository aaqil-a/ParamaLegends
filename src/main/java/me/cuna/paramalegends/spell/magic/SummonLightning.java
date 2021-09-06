package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Predicate;

public class SummonLightning implements Listener, SpellParama {

    private final ParamaLegends plugin;

    private final int manaCost = 100;
    private final int cooldown = 600;
    private final int damage = 45;
    private final int damageBonus = 4;
    private final int cooldownReduction = 40;

    public SummonLightning(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Summon Lightning");
        } else {
            int masteryLevel = playerParama.getMasteryLevel("summonlightning");
            Player player = playerParama.getPlayer();
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 100, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                plugin.sendOutOfRangeMessage(playerParama);
                return;
            }
            if (playerParama.subtractMana( manaCost)) {
                playerParama.addToCooldown(this);
                player.getWorld().strikeLightningEffect(location);
                player.getWorld().spawnParticle(Particle.FLASH, location.add(new Vector(0,1,0)), 5);
                player.getWorld().getHighestBlockAt(location.clone().add(0,1,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(0,0,1)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(-1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(0,0,-1)).getRelative(BlockFace.UP).setType(Material.FIRE);
                for(Entity ignited : player.getWorld().getNearbyEntities(location, 3,4,3)){
                    if(ignited instanceof Damageable && !(ignited instanceof ArmorStand) && !(ignited.equals(player))){
                        plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                        ((Damageable) ignited).damage(damage+masteryLevel*damageBonus+0.069, player);
                        plugin.magicListener.addMastery(playerParama, "summonlightning", 10);
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Summon Lightning");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown- (long) masteryLevel *cooldownReduction);
            }
        }
    }
    public int getManaCost() {
        return manaCost;
    }
    public int getCooldown() {
        return cooldown;
    }
}
