package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.MagicListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Blink {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;

    private final List<String> playerCooldowns = new ArrayList<>();

    public Blink(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castBlink(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            magicListener.sendCooldownMessage(player, "Blink");
        } else if (magicListener.subtractMana(player, 10)) {
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
            playerCooldowns.add(player.getUniqueId().toString());
            location.setDirection(player.getLocation().getDirection());
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getEyeLocation(), 10, 0.5, 0.5, 0.5);
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, location.add(new Vector(0,2,0)), 10, 0.5, 0.5, 0.5);
            magicListener.teleportToAir(player, location);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    magicListener.sendNoLongerCooldownMessage(player, "Blink");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 300);
        }
    }

}
