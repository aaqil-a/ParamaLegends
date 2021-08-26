package id.cuna.ParamaLegends.Spells.Swordsman;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
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

public class Calamity implements SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 500;
    private final List<Player> playersCalamity = new ArrayList<Player>();

    public Calamity(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if(playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Calamity");
        } else {
            if(playerParama.subtractMana(manaCost)){
                Player player = playerParama.getPlayer();
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
                playerParama.addToCooldown(this);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerParama.checkCooldown(this)){
                        plugin.sendNoLongerCooldownMessage(playerParama, "Calamity");
                        playerParama.removeFromCooldown(this);
                    }
                }, 2400);
            }
        }
    }

    public int getManaCost() {
        return manaCost;
    }

    public List<Player> getPlayersCalamity(){
        return playersCalamity;
    }

}
