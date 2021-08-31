package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;
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

    public SummonLightning(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Summon Lightning");
        } else {
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
                List<Entity> entities = player.getWorld().getNearbyEntities(location, 3,4,3).stream().toList();
                for(Entity ignited : entities){
                    if(ignited instanceof Damageable && !(ignited instanceof ArmorStand) && !(ignited.equals(player))){
                        plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                        ((Damageable) ignited).damage(damage+0.069, player);
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Summon Lightning");
                        playerParama.removeFromCooldown(this);
                    }
                }, cooldown);
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
