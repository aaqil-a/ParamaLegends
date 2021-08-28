package me.cuna.ParamaLegends.FunListener;

import me.cuna.ParamaLegends.DataManager;
import me.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AlcoholListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public final List<Player> playersDrunk = new ArrayList<>();
    public final HashMap<Player, Block> playerBarrier = new HashMap<>();
    public final HashMap<Player, BukkitTask> playerFallTasks = new HashMap<>();


    public AlcoholListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }


    //Handle events when item clicked in gui
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if(item.getItemMeta() != null) {
            if(item.getItemMeta().getDisplayName().startsWith("§dAged")){
                List<String> lore = item.getItemMeta().getLore();
                long expire = Integer.parseInt(lore.get(0).substring(6));
                if(expire < player.getWorld().getGameTime()){
                    player.sendMessage(ChatColor.RED+"Bland and bitter, if only you have something to cleanse your palate.");
                    player.removePotionEffect(PotionEffectType.SATURATION);
                }
                if(!playerFallTasks.containsKey(player)){
                    playerFallTasks.put(player, Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        playersDrunk.add(player);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99, 999, false, false, false));
                        if(player.getEyeLocation().getBlock().isPassable()){
                            player.getEyeLocation().getBlock().setType(Material.BARRIER);
                            playerBarrier.put(player, player.getEyeLocation().getBlock());
                        }
                        player.setSwimming(true);
                        Bukkit.getScheduler().runTaskLater(plugin, ()->{
                            playersDrunk.remove(player);
                            player.removePotionEffect(PotionEffectType.SATURATION);
                            if(playerBarrier.containsKey(player)){
                                playerBarrier.get(player).setType(Material.AIR);
                                playerBarrier.remove(player);
                            }
                            playerFallTasks.remove(player);
                        }, 100);
                    }, 100));
                }
            }
        }
    }

    // keep swimming until duration expires
    @EventHandler
    public void onToggleSwim(EntityToggleSwimEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if(playersDrunk.contains(player)){
                event.setCancelled(true);
            }
        }
    }
}
