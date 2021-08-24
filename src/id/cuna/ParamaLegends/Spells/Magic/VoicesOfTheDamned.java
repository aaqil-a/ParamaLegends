package id.cuna.ParamaLegends.Spells.Magic;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.MagicListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.List;

public class VoicesOfTheDamned implements Listener {

    private final ParamaLegends plugin;
    private final MagicListener magicListener;
    private final DataManager data;

    private final List<String> playerCooldowns = new ArrayList<>();

    public VoicesOfTheDamned(ParamaLegends plugin, MagicListener magicListener){
        this.plugin = plugin;
        this.magicListener = magicListener;
        this.data = plugin.getData();
    }

    public void castVoicesOfTheDamned(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            magicListener.sendCooldownMessage(player, "Voices of the Damned");
        } else if (magicListener.subtractMana(player, 400)) {
            Location location = player.getEyeLocation();
            Vector offset = player.getEyeLocation().getDirection().setY(0).normalize().multiply(5);
            location.add(offset);
            ArmorStand dummy = (ArmorStand) player.getWorld().spawnEntity(new Location(player.getWorld(), 0,0,0), EntityType.ARMOR_STAND);
            dummy.setVisible(false);
            dummy.setCustomName(ChatColor.DARK_PURPLE + player.getName() +"'s Portal");
            dummy.setCustomNameVisible(true);
            dummy.setGravity(false);
            dummy.setCanPickupItems(false);
            dummy.setInvulnerable(true);
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                dummy.teleport(location);
                dummy.getLocation().add(new Vector(0,1,0));
            }, 2);
            BukkitTask portalEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                dummy.getWorld().spawnParticle(Particle.CLOUD, dummy.getLocation(), 1, 0.25, 1, 0.25, 0);
                dummy.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, dummy.getLocation(), 1, 0.25, 1, 0.25, 0);
                dummy.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, dummy.getLocation(), 1, 0.25, 1, 0.25,0);
            },3, 3);
            List<Phantom> damnedListPhantom = new ArrayList<Phantom>();
            BukkitTask portalSpawnPhantom = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Phantom damned = (Phantom) dummy.getWorld().spawnEntity(dummy.getLocation(), EntityType.PHANTOM);
                damned.setMetadata(player.getName(), new FixedMetadataValue(plugin, "summoner"));
                damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                damned.setCustomNameVisible(true);
                damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(20.069);
                damned.setTarget(null);
                damnedListPhantom.add(damned);
            },3, 100);
            List<Silverfish> damnedListSilverfish = new ArrayList<Silverfish>();
            BukkitTask portalSpawnSilverfish = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Silverfish damned = (Silverfish) dummy.getWorld().spawnEntity(dummy.getLocation(), EntityType.SILVERFISH);
                damned.setMetadata(player.getName(), new FixedMetadataValue(plugin, "summoner"));
                damned.setCustomName(ChatColor.DARK_PURPLE+"Damned Soul");
                damned.setCustomNameVisible(true);
                damned.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10.069);
                damned.setTarget(null);
                damnedListSilverfish.add(damned);
            },53, 100);
            BukkitTask setDamnedTarget = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
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
                    for(Phantom damned : damnedListPhantom)
                        if(damned.getTarget() == null)
                            damned.setTarget((LivingEntity) closestEntity);
                    for(Silverfish damned : damnedListSilverfish)
                        if(damned.getTarget() == null)
                            damned.setTarget((LivingEntity) closestEntity);
                }
            },3, 20);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                portalEffect.cancel();
                portalSpawnPhantom.cancel();
                portalSpawnSilverfish.cancel();
                dummy.remove();
            }, 600);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                setDamnedTarget.cancel();
                for(Phantom damned : damnedListPhantom)
                    damned.remove();
                for(Silverfish damned : damnedListSilverfish)
                    damned.remove();
            }, 800);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    magicListener.sendNoLongerCooldownMessage(player, "Voices of the Damned");
                    playerCooldowns.remove(player.getUniqueId().toString());
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
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(event.getDamager() instanceof Phantom){
                        event.getDamager().getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, event.getDamager().getLocation(), 8, 0.5, 0.5, 0.5, 0);
                    } else {
                        event.getDamager().getWorld().spawnParticle(Particle.SMOKE_NORMAL, event.getDamager().getLocation(), 8, 0.5, 0.5, 0.5, 0);
                    }
                    event.getDamager().remove();
                }, 60);

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



}
