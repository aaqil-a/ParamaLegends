package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Superconducted implements Listener {

    private final ParamaLegends plugin;
    private final SwordsmanListener swordsmanListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<Entity> entitiesBlinded = new ArrayList<Entity>();

    public Superconducted(ParamaLegends plugin, SwordsmanListener swordsmanListener){
        this.plugin = plugin;
        this.swordsmanListener = swordsmanListener;
    }

    public void castSuperconducted(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            swordsmanListener.sendCooldownMessage(player, "Superconducted");
        } else {
            if(swordsmanListener.subtractMana(player, 300)){
                List<Entity> entities = player.getNearbyEntities(5,4,5);
                createFireworkEffect(player.getLocation(), player);
                List<Entity> toDamage = new ArrayList<Entity>();
                for(Entity hit : entities){
                    if(hit instanceof LivingEntity && !(hit instanceof Player) && !(hit instanceof ArmorStand)){
                        entitiesBlinded.add(hit);
                        ((LivingEntity) hit).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8, 5, false, false, false));
                        toDamage.add(hit);
                    }
                }
                int delay = 10;
                for(Entity damaged : toDamage){
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if(damaged instanceof Damageable){
                            plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                            ((Damageable) damaged).damage(20.072, player);
                            createFireworkEffect(damaged.getLocation(), player);
                        }
                    }, delay);
                    delay += 10;
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for(Entity hit: entities){
                        if(hit instanceof LivingEntity && !(hit instanceof Player)){
                            entitiesBlinded.remove(hit);
                        }
                    }
                }, 160);
                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        swordsmanListener.sendNoLongerCooldownMessage(player, "Superconducted");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 1200);
            }
        }
    }

    public void createFireworkEffect(Location location, Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(location.add(new Vector(0,1,0)), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.STAR)
                .flicker(false).trail(false).withColor(Color.WHITE, Color.NAVY, Color.SILVER).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.setSilent(true);
        firework.detonate();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            if(entitiesBlinded.contains(event.getDamager())){
                event.setCancelled(true);
            }
        }
    }

    public List<Entity> getEntitiesBlinded() {
        return entitiesBlinded;
    }
}
