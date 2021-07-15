package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HuayraFury implements Listener {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;
    private final HashMap<Player, BukkitTask> playerHuayraEffectTask = new HashMap<Player, BukkitTask>();
    private final HashMap<Player, BukkitTask> playerHuayraShootTask = new HashMap<Player, BukkitTask>();
    private final List<String> playersHuayra = new ArrayList<>();

    private final List<String> playerCooldowns = new ArrayList<>();

    public HuayraFury(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castHuayraFury(Player player){
        if (playersHuayra.contains(player.getUniqueId().toString())){
            player.sendMessage(ChatColor.GREEN+"Huayra's Fury deactivated.");
            playerHuayraShootTask.get(player).cancel();
            playerHuayraEffectTask.get(player).cancel();
            playerHuayraShootTask.remove(player);
            playerHuayraEffectTask.remove(player);
            playersHuayra.remove(player.getUniqueId().toString());
        } else if(playerCooldowns.contains(player.getUniqueId().toString())){
            archeryListener.sendCooldownMessage(player, "Huayra's Fury");
        } else if(archeryListener.subtractMana(player, 200)){
            player.sendMessage(ChatColor.GREEN+"Huayra's Fury activated.");
            playerHuayraShootTask.put(player, Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                ItemStack[] inventory = player.getInventory().getStorageContents();
                ItemStack shotArrow = null;
                int shotArrowSlot = -1;

                if(player.getInventory().getItemInOffHand().getType().equals(Material.ARROW)
                        || player.getInventory().getItemInOffHand().getType().equals(Material.TIPPED_ARROW)) {
                    shotArrow = player.getInventory().getItemInOffHand();
                    shotArrowSlot = -2;
                } else {
                    for(ItemStack item : inventory){
                        if(item == null){
                            continue;
                        }
                        if(item.getType().equals(Material.ARROW) || item.getType().equals(Material.TIPPED_ARROW)){
                            shotArrow = item;
                            break;
                        }
                    }
                    if(shotArrow!=null){
                        shotArrowSlot = player.getInventory().first(shotArrow);
                    }
                }
                if(shotArrow != null){
                    Arrow arrow = player.launchProjectile(Arrow.class, player.getLocation().getDirection().multiply(2));
                    if(shotArrowSlot == -2){
                        player.getInventory().getItemInOffHand().setAmount(shotArrow.getAmount()-1);
                    } else {
                        player.getInventory().getItem(shotArrowSlot).setAmount(shotArrow.getAmount()-1);
                    }
                    if(shotArrow.hasItemMeta()){
                        ItemMeta meta = shotArrow.getItemMeta();
                        if(meta.hasDisplayName()){
                            switch(meta.getDisplayName()){
                                case "§aHunter's Eye" -> {
                                    if(!plugin.isSilenced(player)){
                                        archeryListener.getHunterEye().castHuntersEye(player, arrow, true);
                                    }
                                }
                                case "§aViper's Bite" -> {
                                    if(!plugin.isSilenced(player)){
                                        if(archeryListener.checkLevel(player, 2)){
                                            archeryListener.getViperBite().castViperBite(player, arrow, true);
                                        }
                                    }
                                }
                                case "§aNeurotoxin" -> {
                                    if(!plugin.isSilenced(player)){
                                        if(archeryListener.checkLevel(player, 5)){
                                            archeryListener.getNeurotoxin().castNeurotoxin(player, arrow, true);
                                        }
                                    }
                                }
                                case "§aRetreat" -> {
                                    if(!plugin.isSilenced(player)){
                                        if(archeryListener.checkLevel(player, 7)){
                                            archeryListener.getRetreat().castRetreat(player, arrow, true);
                                        }
                                    }
                                }
                                case "§aBlast" -> {
                                    if(!plugin.isSilenced(player)){
                                        if(archeryListener.checkLevel(player, 8)){
                                            archeryListener.getBlast().castBlast(player, arrow, true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }, 5, 5));
            playersHuayra.add(player.getUniqueId().toString());
            playerHuayraEffectTask.put(player, Bukkit.getScheduler().runTaskTimer(plugin, ()->{
                player.getWorld().spawnParticle(Particle.VILLAGER_ANGRY, player.getEyeLocation(), 4, 1, 0.5, 1, 0);
                player.damage(1);
            }, 0, 20));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerHuayraShootTask.containsKey(player)){
                    playerHuayraShootTask.get(player).cancel();
                    playerHuayraEffectTask.get(player).cancel();
                    playerHuayraShootTask.remove(player);
                    playerHuayraEffectTask.remove(player);
                    player.sendMessage(ChatColor.GREEN+"Huayra's Fury wore off.");
                    playersHuayra.remove(player.getUniqueId().toString());
                }
            }, 104);
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCooldowns.contains(player.getUniqueId().toString())){
                    archeryListener.sendNoLongerCooldownMessage(player, "Huayra's Fury");
                    playerCooldowns.remove(player.getUniqueId().toString());
                }
            }, 1800);
        }
    }

}
