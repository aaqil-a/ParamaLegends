package me.cuna.paramalegends.spell.magic;

import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import java.util.function.Predicate;

public class LifeDrain implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 10;

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
            }, 120);
        } else if(playerParama.checkCooldown(this)) {
            plugin.sendCooldownMessage(playerParama, "Life Drain");
        } else {
            Predicate<Entity> notPlayer = entity -> !(entity.equals(player));
            RayTraceResult rayTrace = player.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20, FluidCollisionMode.NEVER,
                    true, 1.5, notPlayer);
            if(rayTrace != null) {
                if (rayTrace.getHitEntity() != null) {
                    Entity drained = rayTrace.getHitEntity();
                    if(drained instanceof Damageable && !(drained instanceof ArmorStand)){
                        player.sendMessage(ChatColor.GREEN + "Life Drain activated.");
                        playerParama.addTask("LIFEDRAIN",
                                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                    if(drained.isDead() || player.isDead() || player.getLocation().distance(drained.getLocation())>8){
                                        player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
                                        playerParama.cancelTask("LIFEDRAIN");
                                        playerParama.addToCooldown(this);
                                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                            if(playerParama.checkCooldown(this)){
                                                plugin.sendNoLongerCooldownMessage(playerParama, "Life Drain");
                                                playerParama.removeFromCooldown(this);
                                            }
                                        }, 120);
                            } else if(playerParama.subtractMana( manaCost)){
                                if(drained instanceof Player){
                                    drained.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(new Vector(0,1,0)), 4, 0.5, 0.5, 0.5, 0);
                                    plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                                    Player healed = (Player) drained;
                                    if(healed.getHealth() <= 19 && player.getHealth() > 1){
                                        healed.setHealth(healed.getHealth()+1);
                                        player.setHealth(player.getHealth()-1);
                                    }
                                } else {
                                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(new Vector(0,1,0)), 4, 0.5, 0.5, 0.5, 0);
                                    plugin.experienceListener.addExp(player, ClassGameType.MAGIC, 1);
                                    ((Damageable) drained).damage(3.069, player);
                                    if(player.getHealth() <= 19){
                                        player.setHealth(player.getHealth()+1);
                                    }
                                }
                            } else {
                                player.sendMessage(ChatColor.GREEN + "Life Drain deactivated.");
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

}