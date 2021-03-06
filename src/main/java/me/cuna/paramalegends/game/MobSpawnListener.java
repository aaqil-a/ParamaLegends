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
        data = plugin.dataManager;
    }


    //Listen for mob spawns and set health, damage and other stats accordingly
    @EventHandler
    public void creatureSpawn(CreatureSpawnEvent event) {
        int worldLevel = plugin.gameManager.experience.getWorldLevel();
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
        //temporary workaround for boss fights
        if(event.getEntityType().equals(EntityType.PHANTOM) || event.getEntityType().equals(EntityType.WITCH)){
            if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)){
                event.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.7);
                return;
            }
        }
        if(event.getEntityType().equals(EntityType.GHAST)){
            if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)){
                event.getEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
                return;
            }
        }
        if(event.getEntityType().equals(EntityType.ENDERMAN)){
            if(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)){
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15);
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                event.getEntity().setHealth(40);
                return;
            }
        }

        //set new attributes
        switch (event.getEntityType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED, HOGLIN, WITHER_SKELETON, PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN,
                    EVOKER, PILLAGER, VINDICATOR -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(50+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(10+bonusDamage);
                event.getEntity().setHealth(50+bonusHealth);
            }
            case SKELETON, STRAY, PHANTOM, BLAZE, GHAST -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(30+bonusHealth);
                event.getEntity().setHealth(30+bonusHealth);
            }
            case SPIDER, CAVE_SPIDER, MAGMA_CUBE -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                    .setBaseValue(30+bonusHealth);
                event.getEntity().setHealth(30+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                    .setBaseValue(6+bonusDamage);
            }
            case CREEPER -> {
                Creeper c = (Creeper) event.getEntity();
                c.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(40+bonusHealth);
                c.setHealth(40+bonusHealth);
            }
            case WITCH -> {
                Witch w = (Witch) event.getEntity();
                w.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(40+bonusHealth);
                w.setHealth(40+bonusHealth);
            }
            case ENDERMAN -> {
                Enderman e = (Enderman) event.getEntity();
                e.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(80+bonusHealth);
                e.setHealth(80+bonusHealth);
                e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(15+bonusDamage);
            }
            case RAVAGER -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(50*5+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(10*2.5+bonusDamage);
                event.getEntity().setHealth(50*5+bonusHealth);
            }
            case VEX -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(30+bonusHealth);
                event.getEntity().setHealth(30+bonusHealth);
                event.getEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(10+bonusDamage);
            }
            case GUARDIAN -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(60+bonusHealth);
                event.getEntity().setHealth(60+bonusHealth);
            }
            case ELDER_GUARDIAN -> {
                event.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(160+bonusHealth);
                event.getEntity().setHealth(160+bonusHealth);
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
                int worldLevel = plugin.gameManager.experience.getWorldLevel();
                int bonusDamage = worldLevel>=2 ? worldLevel*2 : 0;
                event.setDamage(event.getDamage()
                        + 5
                        + bonusDamage);
            }
        }
    }
}
