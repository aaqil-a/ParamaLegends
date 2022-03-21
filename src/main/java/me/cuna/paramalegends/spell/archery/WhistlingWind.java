package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ArcheryListener;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;


import java.util.List;

import java.util.Objects;
import java.util.Random;

public class WhistlingWind implements Listener {

    private final ParamaLegends plugin;
    private final int manaCost = 120;
    private final Random rand;

    public WhistlingWind(ParamaLegends plugin){
        this.plugin = plugin;
        rand = new Random();
    }

    public void castWhistlingWind(PlayerParama playerParama, EntityShootBowEvent event){
        if(event.getProjectile() instanceof SpectralArrow){
            Player player = playerParama.getPlayer();
            SpectralArrow arrow = (SpectralArrow) event.getProjectile();
            if(playerParama.subtractMana(manaCost)){
                player.setMetadata("WHISTLINGWIND", new FixedMetadataValue(plugin, 10));
                arrow.setCustomName("whistlingwind");
                arrow.setGravity(false);
                arrow.remove();
                List <Entity> entities = player.getNearbyEntities(15,15,15);
                entities.removeIf(hit -> (!(hit instanceof Monster || hit instanceof Phantom || hit instanceof Slime) || hit.isDead()));
                if(entities.size()>0) shootArrowToEntity(player, player, entities.get(rand.nextInt(entities.size())));
                else player.removeMetadata("WHISTLINGWIND", plugin);
            } else {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                givePlayerWhistlingWind(player, event.getConsumable());
            }
        }
    }

    public void shootArrowToEntity(Player shooter, Entity source, Entity target){
        //check if player haasdass more whistling winds to be shot
        if(shooter.hasMetadata("WHISTLINGWIND")) {
            int count = shooter.getMetadata("WHISTLINGWIND").get(0).asInt();
            if(count > 0) {
                shooter.setMetadata("WHISTLINGWIND", new FixedMetadataValue(plugin, --count));
            } else {
                returnArrow(shooter, source);
                return;
            }

            //fire whistling wind
            source.getWorld().playSound(source.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.2f);

            Location targetLocation = target.getLocation();
            Location sourceLocation = source.getLocation();
            //if distance is tiny, no need to shoot
            if(targetLocation.distance(sourceLocation) < 1.5){
                hitEntity(shooter, target);
                //fire next whistling wind
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    List <Entity> entities = shooter.getNearbyEntities(15,15,15);
                    entities.removeIf(entity -> (!(entity instanceof Monster || entity instanceof Phantom || entity instanceof Slime) || entity.equals(target) || entity.isDead()));
                    if(entities.size()>0){
                        shootArrowToEntity(shooter, target, entities.get(rand.nextInt(entities.size())));
                    } else {
                        returnArrow(shooter, target);
                    }
                }, 8);
            } else {
                //adjust target and source locations
                targetLocation.add(0,-0.5,0);
                sourceLocation.add(0,-1,0);
                //adjust direction to face target
                sourceLocation.setDirection(targetLocation.subtract(sourceLocation).toVector().normalize());

                //create dummy armor stand to shoot arrow
                ArmorStand dummyShooter = source.getWorld().spawn(sourceLocation, ArmorStand.class, armorStand -> {
                    armorStand.setSilent(true);
                    armorStand.setCollidable(false);
                    armorStand.setGravity(false);
                    armorStand.setInvisible(true);
                    armorStand.setInvulnerable(true);
                    armorStand.setCanPickupItems(false);
                });

                //launch arrow from dummy armor stand
                SpectralArrow arrow = dummyShooter.launchProjectile(SpectralArrow.class);
                arrow.setCustomName("whistlingwind");
                arrow.setShooter(shooter);
                arrow.setGravity(false);
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                arrow.setVelocity(sourceLocation.getDirection().multiply(2.5));
                arrow.setMetadata("TARGETWHISTLINGWIND", new FixedMetadataValue(plugin, target));
                dummyShooter.remove();

                //set timer for whistling wind in case of miss
                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    if(arrow.hasMetadata("TARGETWHISTLINGWIND") && Objects.equals(arrow.getMetadata("TARGETWHISTLINGWIND").get(0).value(), target)){
                        hitEntity(shooter, target);
                        arrow.remove();
                        //fire next whistling wind
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                            List <Entity> entities = shooter.getNearbyEntities(15,15,15);
                            entities.removeIf(entity -> (!(entity instanceof Monster || entity instanceof Phantom || entity instanceof Slime) || entity.equals(target) || entity.isDead()));
                            if(entities.size()>0){
                                shootArrowToEntity(shooter, target, entities.get(rand.nextInt(entities.size())));
                            } else {
                                returnArrow(shooter, target);
                            }
                        }, 8);
                    }
                }, 20);
            }
        }
    }

    public void hitEntity(Player shooter, Entity hit) {
        if(hit instanceof LivingEntity){
            ((LivingEntity) hit).damage(40.016, shooter);
            hit.getWorld().spawnParticle(Particle.FLASH, hit.getLocation(), 1, 0, 0, 0 ,0);
            hit.getWorld().playSound(hit.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.7f, 2f);
        }
    }

    public void returnArrow(Player shooter, Entity source){
        //fire return arrow
        source.getWorld().playSound(source.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.2f);

        Location targetLocation = shooter.getLocation();
        Location sourceLocation = source.getLocation();
        //if distance is tiny, no need to return
        if(targetLocation.distance(sourceLocation) < 1.5){
            shooter.removeMetadata("WHISTLINGWIND", plugin);
            shooter.getWorld().spawnParticle(Particle.FLASH, shooter.getLocation(), 1, 0, 0, 0 ,0);
        } else {
            //adjust target and source locations
            targetLocation.add(0, -0.5, 0);
            sourceLocation.add(0, -1, 0);
            //adjust direction to face target
            sourceLocation.setDirection(targetLocation.subtract(sourceLocation).toVector().normalize());

            //create dummy armor stand to shoot arrow
            ArmorStand dummyShooter = source.getWorld().spawn(sourceLocation, ArmorStand.class, armorStand -> {
                armorStand.setSilent(true);
                armorStand.setCollidable(false);
                armorStand.setGravity(false);
                armorStand.setInvisible(true);
                armorStand.setInvulnerable(true);
                armorStand.setCanPickupItems(false);
            });

            //launch arrow from dummy armor stand
            SpectralArrow arrow = dummyShooter.launchProjectile(SpectralArrow.class);
            arrow.setCustomName("return");
            arrow.setShooter(shooter);
            arrow.setGravity(false);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            arrow.setVelocity(sourceLocation.getDirection().multiply(2.5));
            dummyShooter.remove();
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                shooter.removeMetadata("WHISTLINGWIND", plugin);
                shooter.getWorld().spawnParticle(Particle.FLASH, shooter.getLocation(), 1, 0, 0, 0 ,0);
            }, 30);
        }
    }

    //make whistling wind not hurt player
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof SpectralArrow && event.getDamager().getCustomName() != null){
            SpectralArrow arrow = (SpectralArrow) event.getDamager();
            if(arrow.getCustomName().equals("whistlingwind") || arrow.getCustomName().equals("return")){
                if(!(event.getEntity() instanceof Monster || event.getEntity() instanceof Phantom || event.getEntity() instanceof Slime)){
                    event.setCancelled(true);
                }
            }
        }
    }

    //Deal when whistling wind hits something
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event){
        Projectile projectile = event.getEntity();
        if(projectile instanceof SpectralArrow && projectile.getCustomName() != null){
            SpectralArrow arrow = (SpectralArrow) projectile;
            if ("whistlingwind".equals(arrow.getCustomName()) && arrow.hasMetadata("TARGETWHISTLINGWIND")) {
                if(event.getHitEntity() != null && event.getHitEntity() instanceof Player){
                    event.setCancelled(true);
                } else if((event.getHitEntity() != null && event.getHitEntity().equals(arrow.getMetadata("TARGETWHISTLINGWIND").get(0).value())) || event.getHitBlock() != null){
                    Player shooter = (Player) arrow.getShooter();
                    Entity hit = (Entity) arrow.getMetadata("TARGETWHISTLINGWIND").get(0).value();
                    arrow.remove();
                    hitEntity(shooter, hit);
                    arrow.removeMetadata("TARGETWHISTLINGWIND", plugin);
                    //fire next whistling wind
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        List <Entity> entities = shooter.getNearbyEntities(15,15,15);
                        entities.removeIf(entity -> (!(entity instanceof Monster || entity instanceof Phantom || entity instanceof Slime) || entity.equals(hit) || entity.isDead()));
                        if(entities.size()>0){
                            shootArrowToEntity(shooter, hit, entities.get(rand.nextInt(entities.size())));
                        } else {
                            returnArrow(shooter, hit);
                        }
                    }, 8);
                } else if(event.getHitEntity() != null){
                    Player shooter = (Player) arrow.getShooter();
                    hitEntity(shooter, event.getHitEntity());
                    event.setCancelled(true);
                }
            } else if(arrow.getCustomName().equals("return")){
                if(event.getHitEntity() != null){
                    if(event.getHitEntity().equals(arrow.getShooter())){
                        arrow.remove();
                        event.setCancelled(true);
                    } else if(event.getHitEntity() instanceof Player) {
                        event.setCancelled(true);
                    } else {
                        Player shooter = (Player) arrow.getShooter();
                        hitEntity(shooter, event.getHitEntity());
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public void givePlayerWhistlingWind(Player player, ItemStack item){
        if(Objects.requireNonNull(player.getEquipment()).getItemInOffHand().equals(item)){
            player.getEquipment().setItemInOffHand(item);
        } else {
            player.getInventory().setItem(player.getInventory().first(item), item);
        }
    }
    public int getManaCost(){
        return manaCost;
    }
}
