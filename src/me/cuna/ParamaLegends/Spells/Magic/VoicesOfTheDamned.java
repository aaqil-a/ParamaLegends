package me.cuna.ParamaLegends.Spells.Magic;

import me.cuna.ParamaLegends.ClassType;
import me.cuna.ParamaLegends.DataManager;
import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;
import me.cuna.ParamaLegends.Spells.SpellParama;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Objects;

public class VoicesOfTheDamned implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final int manaCost = 400;

    public VoicesOfTheDamned(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.getData();
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Voices of the Damned");
        } else if (playerParama.subtractMana(manaCost)) {
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
                        damned.setMetadata(player.getName(), new FixedMetadataValue(plugin, "summoner"));
                        damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                        damned.setCustomNameVisible(true);
                        Objects.requireNonNull(damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(10.069);
                        damned.setTarget(null);
                        playerParama.addEntity("DAMNED"+damned.getUniqueId().toString(), damned);
                    },3, 100));
            playerParama.addTask("DAMNEDSPAWNSILVERFISH",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        Silverfish damned = (Silverfish) dummy.getWorld().spawnEntity(dummy.getLocation(), EntityType.SILVERFISH);
                        damned.setMetadata(player.getName(), new FixedMetadataValue(plugin, "summoner"));
                        damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                        damned.setCustomNameVisible(true);
                        Objects.requireNonNull(damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(10.069);
                        damned.setTarget(null);
                        playerParama.addEntity("DAMNED"+damned.getUniqueId().toString(), damned);
                    },53, 100));
            playerParama.addTask("DAMNEDTARGET",
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        List<Entity> entities = player.getNearbyEntities(10,10,10).stream().toList();
                        double closestDistance = Double.MAX_VALUE;
                        Entity closestEntity = null;
                        for(Entity entity : entities){
                            if(entity instanceof Villager || entity instanceof Player || entity instanceof Silverfish
                                    || entity instanceof ArmorStand || entity instanceof Phantom )
                                continue;
                            if(entity instanceof LivingEntity){
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
            }, 600);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerParama.cancelTask("DAMNEDTARGET");
                playerParama.getEntities().forEach((k, v)-> {
                    if(v instanceof Phantom || v instanceof Silverfish) {
                        v.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, v.getLocation(), 8, 0.5, 0.5, 0.5, 0);
                        v.remove();
                    }
                });
            }, 800);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Voices of the Damned");
                    playerParama.removeFromCooldown(this);
                }
            }, 1200);
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

    //Check if damned soul hit entity
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Phantom || event.getDamager() instanceof Silverfish){
            if(event.getDamager().getCustomName() != null && event.getDamager().getCustomName().equals(ChatColor.DARK_PURPLE+"Damned Soul")){
                Player summoner = null;
                for (Player player: Bukkit.getOnlinePlayers())
                    if(event.getDamager().hasMetadata(player.getName()))
                        summoner = player;
                plugin.experienceListener.addExp(summoner, ClassType.MAGIC, 1);

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
                        plugin.experienceListener.addExp(summoner, ClassType.MAGIC, data.getConfig().getInt("mobs."+mob+".exp"));
                        plugin.experienceListener.addLectrum(summoner, data.getConfig().getInt("mobs."+mob+".lectrum"));
                    }
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
}
