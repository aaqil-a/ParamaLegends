package me.cuna.ParamaLegends.Spells.Archery;

import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.SpellParama;
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

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class RoyalArtillery implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 150;


    public RoyalArtillery(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Royal Artillery");
        } else {
            Player player = playerParama.getPlayer();
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
                plugin.sendOutOfRangeMessage(playerParama);
                return;
            }
            if (playerParama.subtractMana(manaCost)) {
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

                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Royal Artillery");
                        playerParama.removeFromCooldown(this);
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

        PlayerParama playerParama = plugin.getPlayerParama(player);
        playerParama.addTask("ROYALARTILLERY1",Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(int i = 0; i < 5; i++){
                        Location arrowLocation = location.clone().add(arrowMapX[i], 6, arrowMapZ[i]);
                        Objects.requireNonNull(location.getWorld()).spawn(arrowLocation, Arrow.class, arrow -> {
                            arrow.setCustomName("barrage");
                            arrow.setVelocity(new Vector(0, -0.7, 0));
                        });
                    }
                }, 0, 30));
        playerParama.addTask("ROYALARTILLERY2", Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                    for(int i = 0; i < 5; i++){
                        Location arrowLocation = location.clone().add(arrowMapX2[i], 6, arrowMapZ2[i]);
                        location.getWorld().spawn(arrowLocation, Arrow.class, arrow -> {
                            arrow.setCustomName("barrage");
                            arrow.setVelocity(new Vector(0, -0.7, 0));
                            arrow.setSilent(true);
                        });
                    }
                }, 10, 30));
        playerParama.addTask("ROYALARTILLERY3", Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            for(int i = 0; i < 5; i++){
                Location arrowLocation = location.clone().add(arrowMapX3[i], 6, arrowMapZ3[i]);
                Objects.requireNonNull(location.getWorld()).spawn(arrowLocation, Arrow.class, arrow -> {
                    arrow.setCustomName("barrage");
                    arrow.setVelocity(new Vector(0, -0.7, 0));
                    arrow.setSilent(true);
                });
            }
        }, 10, 30));
        playerParama.addTask("ROYALARTILLERY4", Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.EXPLOSION_NORMAL, location, 4, 0,0,0,0);
            List<Entity> entities = location.getWorld().getNearbyEntities(location, 2.5, 5, 2.5).stream().toList();
            for(Entity hit : entities){
                if(hit instanceof LivingEntity && !(hit instanceof Player)){
                    ((LivingEntity) hit).damage(6.016, player);
                }
            }
        }, 5, 10));
        Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            playerParama.cancelTask("ROYALARTILLERY1");
            playerParama.cancelTask("ROYALARTILLERY2");
            playerParama.cancelTask("ROYALARTILLERY3");
            playerParama.cancelTask("ROYALARTILLERY4");
        }, 160);
    }

    //Deal when projectile hits block
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow && (projectile.getCustomName() != null)){
            Arrow arrow = (Arrow) projectile;
            if(arrow.getCustomName().equals("barrage")){
                arrow.remove();
                event.setCancelled(true);
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
