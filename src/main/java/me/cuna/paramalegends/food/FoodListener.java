package me.cuna.paramalegends.food;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class FoodListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;

    public FoodListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_AIR){
            Player player = e.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item.getItemMeta() != null){
                switch (item.getItemMeta().getDisplayName()){
                    case ChatColor.COLOR_CHAR+"5Sushi" -> {
                        if(!player.hasMetadata("PARAMAFOOD")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 6));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 601, 1));
                            item.setAmount(item.getAmount() - 1);
                            addFoodCooldown(player, 1200);
                        }
                    }
                    case ChatColor.COLOR_CHAR +"5Sandwich" -> {
                        if(!player.hasMetadata("PARAMAFOOD")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 10));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1201, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 61, 2));
                            item.setAmount(item.getAmount() - 1);
                            player.setMetadata("PARAMAFOOD", new FixedMetadataValue(plugin, true));
                            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                player.removeMetadata("PARAMAFOOD", plugin);
                            }, 100);
                        }
                    }
                    case ChatColor.COLOR_CHAR +"5Cilor" -> {
                        if(!player.hasMetadata("PARAMAFOOD")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 6));
                            item.setAmount(item.getAmount() - 1);
                            player.setMetadata("PARAMAFOOD", new FixedMetadataValue(plugin, true));
                            Bukkit.getScheduler().runTaskLater(plugin, ()->{
                                player.removeMetadata("PARAMAFOOD", plugin);
                            }, 100);
                        }
                    }
                    case ChatColor.COLOR_CHAR +"5Hot Chocolate" -> {
                        if(!player.hasMetadata("PARAMAFOOD")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 1f);
                            player.setFoodLevel(Math.min(20, player.getFoodLevel() + 8));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 61, 2));
                            item.setAmount(item.getAmount() - 1);
                            addFoodCooldown(player, 1200);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if (event.getItemInHand().hasItemMeta()){
            ItemStack placed = event.getItemInHand();
            switch(Objects.requireNonNull(placed.getItemMeta()).getDisplayName()){
                case ChatColor.COLOR_CHAR+"5Bowl Of Rice", ChatColor.COLOR_CHAR+"5Sushi", ChatColor.COLOR_CHAR+"5Sandwich", ChatColor.COLOR_CHAR+"5Cilor", ChatColor.COLOR_CHAR+"5Hot Chocolate", ChatColor.COLOR_CHAR+"5Cold Chocolate" -> event.setCancelled(true);
            }
        }
    }

    public void addFoodCooldown(Player player, int duration){
        player.setMetadata("PARAMAFOOD", new FixedMetadataValue(plugin, true));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            player.removeMetadata("PARAMAFOOD", plugin);
        }, duration);
    }
}
