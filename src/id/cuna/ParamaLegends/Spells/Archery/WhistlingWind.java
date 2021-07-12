package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class WhistlingWind implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;


    private final HashMap<Player, List<Entity>> entitiesWhistlingWind = new HashMap<>();
    private final HashMap<Player, Entity> targetWhistlingWind = new HashMap<>();

    public WhistlingWind(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }


    public void castWhistlingWind(Player player, EntityShootBowEvent event, boolean noMana){
        int manaCost = 200;
        if(noMana){
            manaCost = 0;
        }
        if(event.getProjectile() instanceof SpectralArrow){
            SpectralArrow arrow = (SpectralArrow) event.getProjectile();
            if(archeryListener.subtractMana(player, manaCost)){
                arrow.setCustomName("whistlingwind");
                arrow.setGravity(false);

                Location location = player.getEyeLocation();
                Vector offset = player.getEyeLocation().getDirection().multiply(2);
                location.add(offset);
                if(!(location.getBlock().isEmpty() || location.getBlock().isLiquid())){
                    archeryListener.findAir(location);
                }

                arrow.remove();
                List <Entity> entities = player.getNearbyEntities(10,10,10);
                entities.removeIf(hit -> !(hit instanceof LivingEntity) || hit instanceof Player || hit instanceof ArmorStand || hit instanceof Villager);
                entitiesWhistlingWind.put(player, entities);
                if(entities.size() == 0){
                    entitiesWhistlingWind.remove(player);
                }
                targetWhistlingWind.put(player, player);
                directArrowToEntity(player, player);
            } else {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                givePlayerWhistlingWind(player, event.getConsumable());
            }

        }
    }


    public void shootToEntity(Entity entity, Entity source, Player player, boolean last){
        Location hitLocation = entity.getLocation().add(0,-0.5,0);
        Entity hit;
        if(!last){
            hit = targetWhistlingWind.get(player);
        } else {
            hit = player;
        }
        Location arrowLocation = hit.getLocation().add(0,-1,0);
        Vector direction = new Vector(hitLocation.getX()-arrowLocation.getX(), hitLocation.getY()-arrowLocation.getY(),
                hitLocation.getZ()-arrowLocation.getZ());
        arrowLocation.setDirection(direction.normalize());
        ArmorStand dummy = source.getWorld().spawn(arrowLocation, ArmorStand.class, armorStand -> {
            armorStand.setSilent(true);
            armorStand.setCollidable(false);
            armorStand.setGravity(false);
            armorStand.setInvisible(true);
            armorStand.setInvulnerable(true);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName("whistlingwindshooter");
        });
        source.getWorld().playSound(source.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.2f);
        if(!last) targetWhistlingWind.put(player, entity);

        SpectralArrow newArrow = dummy.launchProjectile(SpectralArrow.class, direction);
        newArrow.setCustomName("whistlingwind");
        newArrow.setShooter(player);
        newArrow.setGravity(false);
        newArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        newArrow.setSilent(true);
        newArrow.setVelocity(newArrow.getVelocity().multiply(2));
        dummy.remove();
        if(arrowLocation.distance(hitLocation) < 1.5){
            newArrow.remove();
            if(!last){
                if(entitiesWhistlingWind.containsKey(player)){
                    Entity target = targetWhistlingWind.get(player);
                    if(target.equals(entity)){
                        if (target instanceof LivingEntity)((LivingEntity) target).damage(20.016, player);
                        target.getWorld().spawnParticle(Particle.FLASH, target.getLocation(), 1, 0, 0, 0 ,0);
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 2f);
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                            directArrowToEntity(target, player);
                        }, 10);
                    }
                }
            }
        }  else {
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                newArrow.remove();
                if(!last){
                    if(entitiesWhistlingWind.containsKey(player)){
                        Entity target = targetWhistlingWind.get(player);
                        if(target.equals(entity)){
                            if (target instanceof LivingEntity)((LivingEntity) target).damage(20.016, player);
                            target.getWorld().spawnParticle(Particle.FLASH, target.getLocation(), 1, 0, 0, 0 ,0);
                            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 2f);
                            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                directArrowToEntity(target, player);
                            }, 10);
                        }
                    }
                }
            }, 20);

        }


    }

    public void directArrowToEntity(Entity entity, Player player){
        List<Entity> entities = entitiesWhistlingWind.get(player);
        if(entities != null){
            if(entities.size()==0){
                return;
            }
            Entity hit = entities.get(0);
            entities.remove(hit);
            shootToEntity(hit, entity, player, false);
            if(entities.size() == 0){
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    //fire arrow back to player
                    Location hitLocation = player.getLocation().add(0,-0.5,0);
                    Location arrowLocation = hit.getLocation().add(0,-1,0);
                    Vector direction = new Vector(hitLocation.getX()-arrowLocation.getX(), hitLocation.getY()-arrowLocation.getY(),
                            hitLocation.getZ()-arrowLocation.getZ());
                    arrowLocation.setDirection(direction.normalize());
                    ArmorStand dummy = player.getWorld().spawn(arrowLocation, ArmorStand.class, armorStand -> {
                        armorStand.setSilent(true);
                        armorStand.setCollidable(false);
                        armorStand.setGravity(false);
                        armorStand.setInvisible(true);
                        armorStand.setInvulnerable(true);
                        armorStand.setCanPickupItems(false);
                        armorStand.setCustomName("whistlingwindshooter");
                    });
                    SpectralArrow newArrow = dummy.launchProjectile(SpectralArrow.class, direction);
                    newArrow.setCustomName("return");
                    newArrow.setShooter(player);
                    newArrow.setGravity(false);
                    newArrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    newArrow.setSilent(true);
                    newArrow.setVelocity(newArrow.getVelocity().multiply(2));
                    dummy.remove();
                    if(arrowLocation.distance(hitLocation) < 4){
                        newArrow.remove();
                    } else {
                        Bukkit.getScheduler().runTaskLater(plugin, newArrow::remove,20);
                    }
                },10);
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    entitiesWhistlingWind.remove(player);
                },8);
            } else {
                entitiesWhistlingWind.replace(player, entities);
            }
        }
    }

    //Deal when projectile hits block
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof SpectralArrow && projectile.getCustomName() != null){
            SpectralArrow arrow = (SpectralArrow) projectile;
            if ("whistlingwind".equals(arrow.getCustomName())) {
                event.setCancelled(true);
                Player player = (Player) arrow.getShooter();
                arrow.remove();
                if(entitiesWhistlingWind.containsKey(player)){
                    Entity target = targetWhistlingWind.get(player);
                    if (target instanceof LivingEntity)((LivingEntity) target).damage(30.016, player);
                    target.getWorld().spawnParticle(Particle.FLASH, target.getLocation(), 1, 0, 0, 0 ,0);
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 2f);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        directArrowToEntity(target, player);
                    }, 10);
                }
            } else if ("return".equals(arrow.getCustomName())) {
                event.setCancelled(true);
                Player player = (Player) arrow.getShooter();
                if(event.getHitEntity()!=null){
                    if (event.getHitEntity() instanceof LivingEntity){
                        if(event.getHitEntity().equals(player)){
                            arrow.remove();
                        } else {
                            ((LivingEntity) event.getHitEntity()).damage(30.016, player);
                        }
                    }

                }
                player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 1, 0, 0, 0 ,0);
            }
        }
    }

    public void givePlayerWhistlingWind(Player player, ItemStack item){
        if(player.getEquipment().getItemInOffHand().equals(item)){
            player.getEquipment().setItemInOffHand(item);
        } else {
            player.getInventory().setItem(player.getInventory().first(item), item);
        }
    }

    public HashMap<Player, List<Entity>> getEntitiesWhistlingWind(){
        return entitiesWhistlingWind;
    }
}
