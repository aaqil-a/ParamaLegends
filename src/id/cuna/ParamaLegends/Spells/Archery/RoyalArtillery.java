package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class RoyalArtillery implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;
    private final List<String> playerCooldowns = new ArrayList<>();
    private final HashMap<Player, BukkitTask> playerArrowTask = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerArrowTask2 = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerArrowTask3 = new HashMap<>();
    private final HashMap<Player, BukkitTask> playerDamageTask = new HashMap<>();


    public RoyalArtillery(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castRoyalArtillery(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            archeryListener.sendCooldownMessage(player, "Royal Artillery");
        } else {
            Predicate<Entity> notPlayer = entity -> !(entity instanceof Player);
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 30, FluidCollisionMode.NEVER, true, 0,
                    notPlayer);
            Location location = new Location(player.getWorld(),0,0,0);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    location = rayTrace.getHitEntity().getLocation();
                } else if (rayTrace.getHitBlock() != null) {
                    location = rayTrace.getHitBlock().getLocation();
                }
            } else{
                plugin.magicListener.sendOutOfRangeMessage(player);
                return;
            }
            if (archeryListener.subtractMana(player, 150)) {
                player.getWorld().spawn(location.clone().add(0,1,0), Firework.class, firework -> {
                    FireworkMeta meta = firework.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BURST)
                            .flicker(false).trail(false).withColor(Color.AQUA, Color.RED).build());
                    meta.setPower(0);
                    firework.setFireworkMeta(meta);
                    firework.setSilent(true);
                    firework.detonate();
                });

                Location finalLocation = location.add(0,1.5,0);
                Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                    barrageArrows(finalLocation, player);
                }, 40);

                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        archeryListener.sendNoLongerCooldownMessage(player, "Royal Artillery");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 1200);
            }
        }
    }

    public void barrageArrows(Location location, Player player){

        double[] arrowMapX = {-1,1,-2,2,0};
        double[] arrowMapZ = {-1,-2,0,0,2};
        double[] arrowMapX2 = {0,-2,2,-1,1};
        double[] arrowMapZ2 = {-2,-1,-1,0,2};
        double[] arrowMapX3 = {0,-1,2,-2,2};
        double[] arrowMapZ3 = {0,-2,-2,2,2};

        playerArrowTask.put(player, Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(int i = 0; i < 5; i++){
                Location arrowLocation = location.clone().add(arrowMapX[i], 6, arrowMapZ[i]);
                location.getWorld().spawn(arrowLocation, Arrow.class, arrow -> {
                    arrow.setCustomName("barrage");
                    arrow.setVelocity(new Vector(0, -0.7, 0));
                });
            }
        }, 0, 30));
        playerArrowTask2.put(player,  Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(int i = 0; i < 5; i++){
                Location arrowLocation = location.clone().add(arrowMapX2[i], 6, arrowMapZ2[i]);
                location.getWorld().spawn(arrowLocation, Arrow.class, arrow -> {
                    arrow.setCustomName("barrage");
                    arrow.setVelocity(new Vector(0, -0.7, 0));
                    arrow.setSilent(true);
                });
            }
        }, 10, 30));
        playerArrowTask3.put(player, Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(int i = 0; i < 5; i++){
                Location arrowLocation = location.clone().add(arrowMapX3[i], 6, arrowMapZ3[i]);
                location.getWorld().spawn(arrowLocation, Arrow.class, arrow -> {
                    arrow.setCustomName("barrage");
                    arrow.setVelocity(new Vector(0, -0.7, 0));
                    arrow.setSilent(true);
                });
            }
        }, 20, 30));
        playerDamageTask.put(player, Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            location.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, location, 4, 0,0,0,0);
            List<Entity> entities = location.getWorld().getNearbyEntities(location, 2.5, 5, 2.5).stream().toList();
            for(Entity hit : entities){
                if(hit instanceof LivingEntity && !(hit instanceof Player)){
                    ((LivingEntity) hit).damage(6.016, player);
                }
            }
        }, 5, 10));
        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            if(playerArrowTask.containsKey(player)){
                playerArrowTask.get(player).cancel();
                playerArrowTask.remove(player);
            }
            if(playerArrowTask2.containsKey(player)){
                playerArrowTask2.get(player).cancel();
                playerArrowTask2.remove(player);
            }
            if(playerArrowTask3.containsKey(player)){
                playerArrowTask3.get(player).cancel();
                playerArrowTask3.remove(player);
            }
            if(playerDamageTask.containsKey(player)){
                playerDamageTask.get(player).cancel();
                playerDamageTask.remove(player);
            }
        }, 160);
    }

    //Deal when projectile hits block
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow && (projectile.getCustomName() != null)){
            Arrow arrow = (Arrow) projectile;
            if(arrow.getCustomName().equals("barrage")){
                event.setCancelled(true);
            }
        }
    }
}
