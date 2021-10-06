package me.cuna.paramalegends.spell.tinker;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Predicate;

public class OverwhelmingBlink implements SpellParama {

    private final ParamaLegends plugin;
    private final int cooldown = 300;

    public OverwhelmingBlink(ParamaLegends plugin){
        this.plugin = plugin;
    }

    @Override
    public void castSpell(PlayerParama playerParama) {
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Overwhelming Blink");
        } else {
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
                // Damage surrounding entities
                List<Entity> entities = player.getNearbyEntities(4,4,4);
                entities.removeIf(hit -> !(hit instanceof Monster || hit instanceof Phantom || hit instanceof Slime));
                for(Entity entity : entities){
                    if(entity instanceof Damageable){
                        ((Damageable) entity).damage(10, player);
                    }
                }
                //Spawn firework effect
                Firework firework = (Firework) player.getWorld().spawnEntity(finalLocation.add(0,-0.5,0), EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BURST)
                        .flicker(false).trail(false).withColor(Color.RED, Color.ORANGE).build());
                meta.setPower(0);
                firework.setFireworkMeta(meta);
                firework.setSilent(true);
                firework.detonate();
            }, 5);
            player.getWorld().spawnParticle(Particle.SMOKE_LARGE, location.add(new Vector(0,2,0)), 10, 0.5, 0.5, 0.5);
            plugin.magicListener.teleportToAir(player, location);
            playerParama.addToTinkerRefreshCooldown("Overwhelming Blink", Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Overwhelming Blink");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown));
        }
    }

    public int getManaCost(){
        return 0;
    }
    public int getCooldown() {return cooldown;}
}
