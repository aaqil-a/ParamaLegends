package me.cuna.paramalegends.game;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        int worldLevel = plugin.experienceListener.getWorldLevel();
        int bonusHealth;
        int bonusDamage;
        if(worldLevel >= 2) {
            bonusHealth = 10*(worldLevel-1);
            bonusDamage = 2*(worldLevel-1);
        } else {
            bonusHealth=0;
            bonusDamage=0;
        }
        boolean hostile = true;
        //temporary workaround for nature fight
        if(event.getEntityType().equals(EntityType.PHANTOM) || event.getEntityType().equals(EntityType.WITCH)){
            if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)){
                event.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.7);
                return;
            }
        }

        switch (event.getEntityType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED, HOGLIN, WITHER_SKELETON, PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN, ZOGLIN,
                    EVOKER, PILLAGER, VINDICATOR -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.health")+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage")+bonusDamage);
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.zombie.health")+bonusHealth);
            }
            case SKELETON, STRAY, PHANTOM, BLAZE, GHAST -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.skeleton.health")+bonusHealth);
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.skeleton.health")+bonusHealth);
            }
            case SPIDER, CAVE_SPIDER, MAGMA_CUBE -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(data.getConfig().getDouble("mobs.spider.health")+bonusHealth);
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.spider.health")+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                    .setBaseValue(data.getConfig().getDouble("mobs.spider.damage")+bonusDamage);
            }
            case CREEPER -> {
                Creeper c = (Creeper) event.getEntity();
                c.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.creeper.health")+bonusHealth);
                c.setHealth(data.getConfig().getDouble("mobs.creeper.health")+bonusHealth);
            }
            case WITCH -> {
                Witch w = (Witch) event.getEntity();
                w.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.witch.health")+bonusHealth);
                w.setHealth(data.getConfig().getDouble("mobs.witch.health")+bonusHealth);
            }
            case ENDERMAN -> {
                Enderman e = (Enderman) event.getEntity();
                e.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.enderman.health")+bonusHealth);
                e.setHealth(data.getConfig().getDouble("mobs.enderman.health")+bonusHealth);
                e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.enderman.damage")+bonusDamage);
            }
            case RAVAGER -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.health")*5+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage")*2.5);
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.zombie.health")*5+bonusHealth);
            }
            case VEX -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(data.getConfig().getDouble("mobs.skeleton.health")+bonusHealth);
                event.getEntity().setHealth(data.getConfig().getDouble("mobs.skeleton.health")+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(data.getConfig().getDouble("mobs.zombie.damage")+bonusDamage);
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
            if (arrow.getShooter() instanceof Skeleton || arrow.getShooter() instanceof Stray
                || arrow.getShooter() instanceof Pillager) {
                int worldLevel = plugin.experienceListener.getWorldLevel();
                int bonusDamage = worldLevel>=2 ? worldLevel*2 : 0;
                event.setDamage(event.getDamage()
                        + data.getConfig().getDouble("mobs.skeleton.arrowbonusdamage")
                        + bonusDamage);
            }
        }
    }
}
