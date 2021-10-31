package me.cuna.paramalegends.boss.summon;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BloodMoonSummonListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public boolean isBloodMoonOccuring = false;
    public int bloodMoonCooldown = 0;
    public BukkitTask bloodMoonTask = null;
    public Random rand = new Random();

    public BloodMoonSummonListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //Listen for summoning item usage
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().hasItemMeta()){
            ItemMeta meta = event.getItem().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.COLOR_CHAR+"4Crimson Root")){
                if(!isBloodMoonOccuring){
                    //check if blood moon on cooldown
                    if(bloodMoonCooldown > 0){
                        event.getPlayer().sendMessage(ChatColor.COLOR_CHAR+"4Crimson Root"+ChatColor.GRAY+" cannot be used too close to a previous Blood Moon.");
                    } else {
                        //Check if time is night
                        if(event.getPlayer().getWorld().getTime() > 13000){
                            bloodMoonCooldown = 2;
                            //reduce item (does it work?)
                            event.getItem().setAmount(event.getItem().getAmount()-1);
                            plugin.bloodMoonListener.bossFight(event.getPlayer().getWorld());
                        } else {
                            event.getPlayer().sendMessage(ChatColor.COLOR_CHAR+"4Crimson Root"+ChatColor.GRAY+" can only be used at night.");
                        }
                    }
                } else event.getPlayer().sendMessage(ChatColor.COLOR_CHAR+"4Crimson Root"+ChatColor.GRAY+" cannot be used during a Blood Moon.");
            }
        }
    }

    public void startBloodMoonTask(){
        if(bloodMoonTask != null) bloodMoonTask.cancel();
        World world = Objects.requireNonNull(Bukkit.getWorld("world"));
        long offset = 13000-world.getTime();
        if(offset < 0) offset += 24000;
        bloodMoonTask = Bukkit.getScheduler().runTaskTimer(plugin, ()->{
            if(Bukkit.getOnlinePlayers().isEmpty()) stopBloodMoonTask();
            if(bloodMoonCooldown > 0) bloodMoonCooldown--;
            else if(Bukkit.getOnlinePlayers().size() >= 4){
                if(rand.nextInt(9)==0 && !isBloodMoonOccuring()){
                    //start blood moon
                    bloodMoonCooldown = 2;
                    Bukkit.broadcastMessage(ChatColor.DARK_RED+"Cries of the dark resound sinisterly.");
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        plugin.bloodMoonListener.bossFight(world);
                    }, 1000);
                }
            }
        }, offset, 24000);
    }

    public void stopBloodMoonTask(){
        if(bloodMoonTask != null) bloodMoonTask.cancel();
        bloodMoonCooldown = 0;
        bloodMoonTask = null;
    }

    public void setBloodMoonOccuring(boolean isBloodMoonOccuring){
        this.isBloodMoonOccuring = isBloodMoonOccuring;
    }

    public boolean isBloodMoonOccuring() {
        return this.isBloodMoonOccuring;
    }
}
