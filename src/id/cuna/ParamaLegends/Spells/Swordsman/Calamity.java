package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Damageable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Calamity {

    private final ParamaLegends plugin;
    private final SwordsmanListener swordsmanListener;

    private final List<String> playerCooldowns = new ArrayList<>();
    private final List<Player> playersCalamity = new ArrayList<Player>();

    public Calamity(ParamaLegends plugin, SwordsmanListener swordsmanListener){
        this.plugin = plugin;
        this.swordsmanListener = swordsmanListener;
    }


    public void castCalamity(Player player){
        if(playerCooldowns.contains(player.getUniqueId().toString())){
            swordsmanListener.sendCooldownMessage(player, "Calamity");
        } else {
            if(swordsmanListener.subtractMana(player, 500)){
                playersCalamity.add(player);
                BukkitTask calamity = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    List<Entity> entities = player.getNearbyEntities(10,10,10);
                    List<Entity> toDamage = new ArrayList<Entity>();
                    for(Entity hit : entities){
                        if(hit instanceof LivingEntity && !(hit instanceof Player) && !(hit instanceof Villager) && !(hit instanceof ArmorStand)) {
                            toDamage.add(hit);
                        }
                    }
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getEyeLocation().add(0,1,0), 4, 0.5, 0.5, 0.5, 0);

                    Random rand = new Random();
                    if(toDamage.size() > 0){
                        Entity striked = toDamage.get(rand.nextInt(toDamage.size()));
                        striked.getWorld().strikeLightningEffect(striked.getLocation());
                        striked.getWorld().spawnParticle(Particle.FLASH, striked.getLocation().add(new Vector(0,1,0)), 5);
                        ((Damageable) striked).damage(30.072, player);
                        plugin.experienceListener.addExp(player, ClassType.SWORDSMAN, 1);
                    }
                }, 62, 20);
                BukkitTask calamityEffect = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation().add(0,1,0), 1, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getEyeLocation().add(0,1.5,0), 1, 0.5, 0.5, 0.5, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getEyeLocation().add(0,1.5,0), 1, 0.5, 0.5, 0.5, 0);
                }, 0, 5);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    calamity.cancel();
                    playersCalamity.remove(player);
                    calamityEffect.cancel();
                }, 302);
                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCooldowns.contains(player.getUniqueId().toString())){
                        swordsmanListener.sendNoLongerCooldownMessage(player, "Calamity");
                        playerCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 2400);
            }
        }
    }

    public List<Player> getPlayersCalamity(){
        return playersCalamity;
    }

}
