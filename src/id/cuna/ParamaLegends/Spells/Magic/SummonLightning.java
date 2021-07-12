package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SummonLightning implements Listener {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;

    private final List<String> playerCooldowns = new ArrayList<>();


    public SummonLightning(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
    }

    public void castSummonLightning(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            magicListener.sendCooldownMessage(player, "Summon Lightning");
        } else {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 50, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                magicListener.sendOutOfRangeMessage(player);
                return;
            }
            if ( magicListener.subtractMana(player, 150)) {

                playerCooldowns.add(player.getUniqueId().toString());
                player.getWorld().strikeLightningEffect(location);
                player.getWorld().spawnParticle(Particle.FLASH, location.add(new Vector(0,1,0)), 5);
                player.getWorld().getHighestBlockAt(location.clone().add(0,1,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(0,0,1)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(-1,0,0)).getRelative(BlockFace.UP).setType(Material.FIRE);
                player.getWorld().getHighestBlockAt(location.clone().add(0,0,-1)).getRelative(BlockFace.UP).setType(Material.FIRE);
                List<Entity> entities = player.getWorld().getNearbyEntities(location, 3,4,3).stream().toList();
                for(Entity ignited : entities){
                    if(ignited instanceof Damageable && !(ignited instanceof Player)){
                        plugin.experienceListener.addExp(player, ClassType.MAGIC, 1);
                        ((Damageable) ignited).damage(20.069, player);
                    }
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        magicListener.sendNoLongerCooldownMessage(player, "Summon Lightning");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 600);
            }
        }
    }
}
