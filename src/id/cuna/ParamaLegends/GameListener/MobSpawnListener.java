package id.cuna.ParamaLegends.GameListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
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
            case ZOMBIE -> {
                Zombie z = (Zombie) event.getEntity();
                z.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.health"));
                z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage"));
                z.setHealth(data.getConfig().getDouble("mobs.zombie.health"));
            }
            case ZOMBIE_VILLAGER -> {
                ZombieVillager z = (ZombieVillager) event.getEntity();
                z.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.health"));
                z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage"));
                z.setHealth(data.getConfig().getDouble("mobs.zombie.health"));
            }
            case HUSK -> {
                Husk z = (Husk) event.getEntity();
                z.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.health"));
                z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage"));
                z.setHealth(data.getConfig().getDouble("mobs.zombie.health"));
            }
            case SKELETON -> {
                Skeleton s = (Skeleton) event.getEntity();
                s.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.skeleton.health"));
                s.setHealth(data.getConfig().getDouble("mobs.skeleton.health"));
            }
            case STRAY -> {
                Stray s = (Stray) event.getEntity();
                s.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.skeleton.health"));
                s.setHealth(data.getConfig().getDouble("mobs.skeleton.health"));
            }
            case SPIDER -> {
                Spider s = (Spider) event.getEntity();
                s.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.spider.health"));
                s.setHealth(data.getConfig().getDouble("mobs.spider.health"));
                s.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.spider.damage"));
            }
            case CAVE_SPIDER -> {
                CaveSpider s = (CaveSpider) event.getEntity();
                s.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.spider.health"));
                s.setHealth(data.getConfig().getDouble("mobs.spider.health"));
                s.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.spider.damage"));
            }
            case CREEPER -> {
                Creeper c = (Creeper) event.getEntity();
                c.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.creeper.health"));
                c.setHealth(data.getConfig().getDouble("mobs.creeper.health"));
                c.setExplosionRadius(
                        data.getConfig().getInt("mobs.creeper.explosionradius"));
                c.setMaxFuseTicks(
                        data.getConfig().getInt("mobs.creeper.maxfuseticks"));
            }
            case DROWNED -> {
                Drowned z = (Drowned) event.getEntity();
                z.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.health"));
                z.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage"));
                z.setHealth(data.getConfig().getDouble("mobs.zombie.health"));
            }
            case WITCH -> {
                Witch w = (Witch) event.getEntity();
                w.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.witch.health"));
                w.setHealth(data.getConfig().getDouble("mobs.witch.health"));
            }
            case VINDICATOR, EVOKER, PILLAGER, ENDERMAN -> {
                event.setCancelled(true);
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
