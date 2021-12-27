package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;

public class VoicesOfTheDamned implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final int manaCost = 400;
    private final int cooldown = 1200;
    private final int spawnDuration = 600;
    private final int livingDuration = 800;
    private final int damage = 10;
    private final int damageBonus = 4;
    private final int cooldownReduction = 40;

    public VoicesOfTheDamned(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.getData();
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Voices of the Damned");
        } else if (playerParama.subtractMana(manaCost)) {
            int masteryLevel = playerParama.getMasteryLevel("voicesofthedamned");
            Player player = playerParama.getPlayer();
            Location location = player.getEyeLocation();
            Vector offset = player.getEyeLocation().getDirection().setY(0).normalize().multiply(5);
            location.add(offset);
            playerParama.addEntity("DAMNEDPORTAL",
                    player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND));
            ArmorStand dummy = (ArmorStand) playerParama.getEntity("DAMNEDPORTAL");
            dummy.setVisible(false);
            dummy.setCustomName(ChatColor.DARK_PURPLE + player.getName() +"'s Portal");
            dummy.setCustomNameVisible(true);
            dummy.setGravity(false);
            dummy.setCanPickupItems(false);
            dummy.setInvulnerable(true);
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(location);
            }, 2);
            playerParama.addTask("DAMNEDEFFECT",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        dummy.getWorld().spawnParticle(Particle.CLOUD, dummy.getLocation(), 1, 0.25, 1, 0.25, 0);
                        dummy.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, dummy.getLocation(), 1, 0.25, 1, 0.25, 0);
                        dummy.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, dummy.getLocation(), 1, 0.25, 1, 0.25,0);
                    },3, 3));
            playerParama.addTask("DAMNEDSPAWNPHANTOM",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Phantom damned = (Phantom) dummy.getWorld().spawnEntity(dummy.getLocation(), EntityType.PHANTOM);
                        damned.setMetadata("caster", new FixedMetadataValue(plugin, player.getName()));
                        damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                        damned.setCustomNameVisible(true);
                        Objects.requireNonNull(damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(damage+damageBonus*masteryLevel+0.069);
                        damned.setTarget(null);
                        playerParama.addEntity("DAMNED"+damned.getUniqueId().toString(), damned);
                    },3, 100));
            playerParama.addTask("DAMNEDSPAWNSILVERFISH",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Silverfish damned = (Silverfish) dummy.getWorld().spawnEntity(dummy.getLocation(), EntityType.SILVERFISH);
                        damned.setMetadata("caster", new FixedMetadataValue(plugin, player.getName()));
                        damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                        damned.setCustomNameVisible(true);
                        Objects.requireNonNull(damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(damage+damageBonus*masteryLevel+0.069);
                        damned.setTarget(null);
                        playerParama.addEntity("DAMNED"+damned.getUniqueId().toString(), damned);
                    },53, 100));
            playerParama.addTask("DAMNEDTARGET",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        double closestDistance = Double.MAX_VALUE;
                        Entity closestEntity = null;
                        for(Entity entity : player.getNearbyEntities(10,10,10)){
                            if(entity instanceof Monster || entity instanceof Slime){
                                double distance = entity.getLocation().distance(player.getLocation());
                                if(distance < closestDistance){
                                    closestDistance = distance;
                                    closestEntity = entity;
                                }
                            }
                        }
                        if(closestEntity != null){
                            for(Entity entity : playerParama.getEntities().values())
                                if(entity instanceof Phantom || entity instanceof Silverfish){
                                    Mob damned = (Mob) entity;
                                    if(damned.getTarget() == null || damned.getTarget() instanceof Player)
                                        damned.setTarget((LivingEntity) closestEntity);
                                }
                        }
                    },3, 20));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerParama.cancelTask("DAMNEDEFFECT");
                playerParama.cancelTask("DAMNEDSPAWNPHANTOM");
                playerParama.cancelTask("DAMNEDSPAWNSILVERFISH");
                dummy.remove();
            }, spawnDuration);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerParama.cancelTask("DAMNEDTARGET");
                playerParama.getEntities().forEach((k, v)-> {
                    if(v instanceof Phantom || v instanceof Silverfish) {
                        v.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, v.getLocation(), 8, 0.5, 0.5, 0.5, 0);
                        v.remove();
                    }
                });
            }, livingDuration);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Voices of the Damned");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown- (long) cooldownReduction *masteryLevel);
        }
    }

    //Make damned not burn
    @EventHandler
    public void onEntityBurn(EntityCombustEvent event){
        Entity entity = event.getEntity();
        if(entity.getCustomName() != null){
            if(entity.getCustomName().contains("Damned Soul") || entity.getCustomName().contains("Soulstring"))
                event.setCancelled(true);
        }
    }

    //make damned souls not target players
    @EventHandler
    public void onChangeTarget(EntityTargetLivingEntityEvent event){
        if(event.getEntity().getCustomName() != null){
            if(event.getEntity().getCustomName().contains("Damned Soul")){
                if(event.getTarget() instanceof Player){
                    event.setCancelled(true);
                }
            }
        }
    }

    //Check if damned soul hit entity
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Phantom || event.getDamager() instanceof Silverfish){
            if(event.getDamager().getCustomName() != null && event.getDamager().getCustomName().equals(ChatColor.DARK_PURPLE+"Damned Soul")){
                Player summoner = Bukkit.getPlayer(event.getDamager().getMetadata("caster").get(0).asString());
                plugin.experienceListener.addExp(summoner, ClassGameType.MAGIC, 1);
                if(event.getDamager() instanceof Monster) plugin.getPlayerParama(summoner).addMastery( "voicesofthedamned", 5);
                //Check if damned killed enemy and give exp if so
                Damageable victim = (Damageable) event.getEntity();
                if(event.getFinalDamage() > victim.getHealth()){
                    String mob = "";
                    switch (event.getEntityType()) {
                        case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED -> mob = "zombie";
                        case WITCH -> mob = "witch";
                        case SKELETON, STRAY -> mob = "skeleton";
                        case CREEPER -> mob = "creeper";
                        case SPIDER, CAVE_SPIDER -> mob = "spider";
                    }
                    //Grant exp and lectrum to player according to mob killed
                    if(!mob.equals("")){
                        plugin.experienceListener.addExp(summoner, ClassGameType.MAGIC, data.getConfig().getInt("mobs."+mob+".exp"));
                        plugin.experienceListener.addLectrum(summoner, data.getConfig().getInt("mobs."+mob+".lectrum"));
                    }
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }
}
