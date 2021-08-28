package me.cuna.paramalegends.fun;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;


public class WineryListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;


    public WineryListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }


    //Handle events when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        //Check if item clicked is in gui
        if (!event.getView().getTitle().equals("§6Winery Barrel")) {
            return;
        }
        event.setCancelled(true);
        long expiration = data.getConfig().getLong("players."+player.getUniqueId().toString()+".alcoholLicenseExpiration");
        if(expiration > player.getWorld().getGameTime()){
            ItemStack itemClicked = event.getCurrentItem();
            if(itemClicked != null && itemClicked.getItemMeta() != null) {
                if(itemClicked.getItemMeta().getDisplayName().startsWith("§dUnaged")){
                    beginAging(event);
                } else if(itemClicked.getItemMeta().getDisplayName().startsWith("§dAged")){
                    event.getClickedInventory().clear(event.getSlot());
                    player.getInventory().addItem(itemClicked);
                }
            }
        } else {
            player.sendMessage(ChatColor.RED+"You do not have a valid alcohol license.");
        }
    }

    public void beginAging(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack drink = event.getCurrentItem();
        if(event.getInventory().firstEmpty() == -1){
            player.sendMessage(ChatColor.RED+"This barrel is full.");
            return;
        }
        PotionMeta meta = (PotionMeta) drink.getItemMeta();
        List<String> lore = new ArrayList<>();
        switch(meta.getDisplayName().substring(9)) {
            case "Apple Wine" -> {
                lore.add(ChatColor.GRAY+"Aged in 3 days");
                lore.add(ChatColor.DARK_GRAY+"ID: "+ (player.getWorld().getGameTime()+72000));
            }
            case "Pale Ale" -> {
                lore.add(ChatColor.GRAY+"Aged in 1 day");
                lore.add(ChatColor.DARK_GRAY+"ID: "+ (player.getWorld().getGameTime()+24000));
            }
            case "Vodka" -> {
                lore.add(ChatColor.GRAY+"Aged in 2 days");
                lore.add(ChatColor.DARK_GRAY+"ID: "+ (player.getWorld().getGameTime()+48000));
            }
        }
        meta.setLore(lore);
        meta.setDisplayName("§dAging"+meta.getDisplayName().substring(8));
        drink.setItemMeta(meta);
        event.getInventory().addItem(drink);
        player.getInventory().clear(event.getSlot());
    }
    //update age when opening winery
    @EventHandler
    public void onOpenInventory(InventoryOpenEvent event){
        if(event.getView().getTitle().equals("§6Winery Barrel")){
            Player player = (Player) event.getPlayer();
            for(ItemStack potion : event.getInventory().getStorageContents()){
                if(potion == null || potion.getItemMeta().getDisplayName().contains("Aged")) continue;
                PotionMeta meta = (PotionMeta) potion.getItemMeta();
                List<String> lore = meta.getLore();
                long finish = Integer.parseInt(lore.get(1).substring(6));
                long time = finish-player.getWorld().getGameTime();
                if(time <= 0){
                    potion.setItemMeta(getAgedBeverageMeta(potion, player.getWorld().getGameTime()));
                } else {
                    int timeInDays = (int) Math.ceil(time/24000d);
                    lore.clear();
                    if(timeInDays == 1){
                        lore.add(ChatColor.GRAY+"Aged in "+timeInDays+" day");
                    } else {
                        lore.add(ChatColor.GRAY+"Aged in "+timeInDays+" days");
                    }
                    lore.add(ChatColor.DARK_GRAY+"ID: "+finish);
                    meta.setLore(lore);
                    potion.setItemMeta(meta);
                }
            }
        }
    }

    public PotionMeta getAgedBeverageMeta(ItemStack drink, long time){
        PotionMeta meta = (PotionMeta) drink.getItemMeta();
        List<String> lore = new ArrayList<>();
        switch(drink.getItemMeta().getDisplayName().substring(8)){
            case "Apple Wine" -> {
                meta.removeCustomEffect(PotionEffectType.POISON);
                meta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 1200, 0), true);
                meta.addCustomEffect(new PotionEffect(PotionEffectType.SATURATION, 160, 0), true);
                lore.add(ChatColor.DARK_GRAY+"ID: "+(time+168000));
                meta.setLore(lore);
            }
            case "Pale Ale" -> {
                meta.removeCustomEffect(PotionEffectType.POISON);
                meta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 1200, 0), true);
                lore.add(ChatColor.DARK_GRAY+"ID: "+(time+168000));
                meta.setLore(lore);
            }
            case "Vodka" -> {
                meta.removeCustomEffect(PotionEffectType.POISON);
                meta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 1800, 0), true);
                meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, 1800, 0), true);
                meta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 1800, 0), true);
                lore.add(ChatColor.DARK_GRAY+"ID: "+(time+168000));
                meta.setLore(lore);
            }
        }
        meta.setDisplayName("§dAged "+meta.getDisplayName().substring(8));
        return meta;
    }
}
