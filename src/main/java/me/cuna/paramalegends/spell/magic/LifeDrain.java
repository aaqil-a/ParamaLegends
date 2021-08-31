package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public class LifeDrain implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 10;
    private final int range = 10;
    private final int cooldown = 120;
    private final int damage = 3;
    private final int healing = 1;

    public LifeDrain(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        Player player = playerParama.getPlayer();
        if(playerParama.hasTask("LIFEDRAIN")){
            player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
            playerParama.cancelTask("LIFEDRAIN");
            playerParama.addToCooldown(this);
            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                if(playerParama.checkCooldown(this)){
                    plugin.sendNoLongerCooldownMessage(playerParama, "Life Drain");
                    playerParama.removeFromCooldown(this);
                }
            }, cooldown);
        } else if(playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Life Drain");
        } else {
            Predicate<Entity> notPlayer = entity -> !(entity.equals(player));
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), range, FluidCollisionMode.NEVER,
                    true, 1.5, notPlayer);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    Entity drained = rayTrace.getHitEntity();
                    if(drained instanceof Damageable && !(drained instanceof ArmorStand)){
                        player.sendMessage(ChatColor.GREEN + "Life Drain activated.");
                        playerParama.addTask("LIFEDRAIN",
                                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                    if(drained.isDead() || player.isDead() || player.getLocation().distance(drained.getLocation())>range){
                                        player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
                                        playerParama.addToCooldown(this);
                                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                            if(playerParama.checkCooldown(this)){
                                                plugin.sendNoLongerCooldownMessage(playerParama, "Life Drain");
                                                playerParama.removeFromCooldown(this);
                                            }
                                        }, cooldown);
                                        playerParama.cancelTask("LIFEDRAIN");
                                    } else if(playerParama.subtractMana( manaCost)){
                                        if(drained instanceof Player){
                                            drained.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(new Vector(0,1,0)), 4, 0.5, 0.5, 0.5, 0);
                                            plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                                            Player healed = (Player) drained;
                                            if(healed.getHealth() <= (20-healing) && player.getHealth() > healing){
                                                healed.setHealth(healed.getHealth()+healing);
                                                player.setHealth(player.getHealth()-healing);
                                            }
                                        } else {
                                            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(new Vector(0,1,0)), 4, 0.5, 0.5, 0.5, 0);
                                            plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                                            ((Damageable) drained).damage(damage+0.069, player);
                                            if(player.getHealth() <= (20-healing)){
                                                player.setHealth(player.getHealth()+healing);
                                            }
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
                                        playerParama.addToCooldown(this);
                                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                            if(playerParama.checkCooldown(this)){
                                                plugin.sendNoLongerCooldownMessage(playerParama, "Life Drain");
                                                playerParama.removeFromCooldown(this);
                                            }
                                        }, cooldown);
                                        playerParama.cancelTask("LIFEDRAIN");
                                    }
                        }, 0, 20));
                    }
                }
            }
        }
    }

    public int getManaCost(){
        return manaCost;
    }
    public int getCooldown() { return cooldown;}

}