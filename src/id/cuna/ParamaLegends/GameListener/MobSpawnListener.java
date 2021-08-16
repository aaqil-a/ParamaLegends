package id.cuna.ParamaLegends.GameListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Enderman;


import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MobSpawnListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;

    public MobSpawnListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }


    //Listen for mob spawns and set health, damage and other stats accordingly
    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM){
            if(event.getEntityType().equals(EntityType.PHANTOM)){
                event.setCancelled(true);
                return;
            }
        }
        boolean hostile = true;
        switch (event.getEntityType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED, HOGLIN, WITHER_SKELETON, PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN, ZOGLIN -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.health"));
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage"));
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.zombie.health"));
            }
            case SKELETON, STRAY, PHANTOM, BLAZE, GHAST -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.skeleton.health"));
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.skeleton.health"));
            }
            case SPIDER, CAVE_SPIDER, MAGMA_CUBE -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(data.getConfig().getDouble("mobs.spider.health"));
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.spider.health"));
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                    .setBaseValue(data.getConfig().getDouble("mobs.spider.damage"));
            }
            case CREEPER -> {
                Creeper c = (Creeper) event.getEntity();
                c.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.creeper.health"));
                c.setHealth(data.getConfig().getDouble("mobs.creeper.health"));
            }
            case WITCH -> {
                Witch w = (Witch) event.getEntity();
                w.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.witch.health"));
                w.setHealth(data.getConfig().getDouble("mobs.witch.health"));
            }
            case ENDERMAN -> {
                Enderman e = (Enderman) event.getEntity();
                e.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.enderman.health"));
                e.setHealth(data.getConfig().getDouble("mobs.enderman.health"));
                e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.enderman.damage"));
            }
            default -> {
                hostile = false;
            }
        }
        if(hostile){
            event.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.7);
        }
    }

    //Increase damage of arrows shot by skeletons
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (damager instanceof Arrow && event.getEntityType() == EntityType.PLAYER) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Skeleton || arrow.getShooter() instanceof Stray) {
                event.setDamage(event.getDamage()
                        + data.getConfig().getDouble("mobs.skeleton.arrowbonusdamage"));
            }
        }
    }
}
