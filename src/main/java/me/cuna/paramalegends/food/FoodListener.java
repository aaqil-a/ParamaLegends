package me.cuna.paramalegends.food;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
                if(plugin.foodRecipes.getFoodNames().contains(item.getItemMeta().getDisplayName())){
                    if(player.hasMetadata("PARAMAFOOD")){
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY + "You are too satiated to eat any more."));
                    } else {
                        switch (item.getItemMeta().getDisplayName()) {
                            case ChatColor.COLOR_CHAR + "5Sushi" -> {
                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 6));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 601, 1));
                                item.setAmount(item.getAmount() - 1);
                                addFoodCooldown(player, 1200);
                            }
                            case ChatColor.COLOR_CHAR + "5Sandwich" -> {
                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 10));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1201, 0));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 61, 1));
                                item.setAmount(item.getAmount() - 1);
                                addFoodCooldown(player, 1200);
                            }
                            case ChatColor.COLOR_CHAR + "5Cilor" -> {
                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1f, 1f);
                                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 6));
                                item.setAmount(item.getAmount() - 1);
                                addFoodCooldown(player, 1200);
                            }
                            case ChatColor.COLOR_CHAR + "5Hot Chocolate" -> {
                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 1f);
                                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 61, 1));
                                item.setAmount(item.getAmount() - 1);
                                addFoodCooldown(player, 1200);
                            }
                            case ChatColor.COLOR_CHAR + "5Cold Brew" -> {
                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 1f);
                                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 0));
                                item.setAmount(item.getAmount() - 1);
                                addFoodCooldown(player, 1200);
                            }
                            case ChatColor.COLOR_CHAR + "5Hot Coffee" -> {
                                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 1f);
                                player.setFoodLevel(Math.min(20, player.getFoodLevel() + 4));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1200, 0));
                                item.setAmount(item.getAmount() - 1);
                                addFoodCooldown(player, 1200);
                            }
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
            switch (Objects.requireNonNull(placed.getItemMeta()).getDisplayName()) {
                case ChatColor.COLOR_CHAR + "5Bowl Of Rice", ChatColor.COLOR_CHAR + "5Sushi", ChatColor.COLOR_CHAR + "5Sandwich", ChatColor.COLOR_CHAR + "5Cilor", ChatColor.COLOR_CHAR + "5Hot Chocolate", ChatColor.COLOR_CHAR + "5Cold Chocolate", ChatColor.COLOR_CHAR + "5Coffee Ground", ChatColor.COLOR_CHAR + "5Hot Coffee", ChatColor.COLOR_CHAR + "5Cold Brew" -> event.setCancelled(true);
                case "Coffee Grinder" -> {
                    event.getBlock().setMetadata("COFFEEGRINDER", new FixedMetadataValue(plugin, 1));
                    event.getPlayer().sendMessage("Coffee Grinder placed!");
                    Location location = event.getBlock().getLocation();
                    data.getConfig().set("coffeegrinder", location);
                    data.saveConfig();
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
//        Bukkit.broadcastMessage(String.valueOf(Objects.requireNonNull(event.getClickedBlock()).hasMetadata("COFFEEGRINDER")));
        if ((event.getClickedBlock() != null) && (event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getHand() == EquipmentSlot.HAND)){
            if(event.getClickedBlock().getType() == Material.PLAYER_HEAD){
                Location datax = data.getConfig().getLocation("coffeegrinder");
                if(event.getClickedBlock().getLocation().equals(datax)){
                    event.getPlayer().getWorld().dropItem(event.getClickedBlock().getLocation(), plugin.foodRecipes.getCoffeeGround());
                }
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
