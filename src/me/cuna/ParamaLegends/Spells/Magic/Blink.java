package me.cuna.ParamaLegends.Spells.Magic;

import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import java.util.function.Predicate;

public class Blink implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 50;

    public Blink(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Blink");
        } else if (playerParama.subtractMana(manaCost)) {
            Player player = playerParama.getPlayer();
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                location = player.getEyeLocation().add(player.getLocation().getDirection().multiply(20));
            }
            playerParama.addToCooldown(this);
            location.setDirection(player.getLocation().getDirection());
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getEyeLocation(), 10, 0.5, 0.5, 0.5);
            Location finalLocation = location;
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                player.getWorld().playSound(player.getEyeLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                player.getWorld().playSound(finalLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            }, 5);
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, location.add(new Vector(0,2,0)), 10, 0.5, 0.5, 0.5);
            plugin.magicListener.teleportToAir(player, location);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Blink");
                    playerParama.removeFromCooldown(this);
                }
            }, 300);
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
