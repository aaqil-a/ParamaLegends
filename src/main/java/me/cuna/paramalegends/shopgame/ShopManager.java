package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopManager implements Listener {

    public ParamaLegends plugin;    
    public Destiny destiny;
    public MagicShop magicShop;
    public ArcheryShop archeryShop;
    public SwordsmanShop swordsmanShop;
    public ReaperShop reaperShop;
    public GeneralShop generalShop;

    public ShopManager(ParamaLegends plugin){
        this.plugin = plugin;
        magicShop = new MagicShop(plugin);
        archeryShop = new ArcheryShop(plugin);
        swordsmanShop = new SwordsmanShop(plugin);
        reaperShop = new ReaperShop(plugin);
        generalShop = new GeneralShop(plugin);
        destiny = new Destiny(plugin);

        registerListeners();
    }
    
    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getPluginManager().registerEvents(magicShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(archeryShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(swordsmanShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(reaperShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(generalShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(destiny, plugin);
    }


    //Handle events when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerParama playerParama = plugin.getPlayerParama(player);
        ShopGUI gui = playerParama.getOpenShopGui();


        if (gui == null){
            return;
        }

        //player has gui open but clicks in own inventory
        if(event.getInventory().equals(gui.getInventory()) && !event.getClickedInventory().equals(gui.getInventory())){
            event.setCancelled(true);
            return;
        }

        if( event.getClickedInventory() == null
            || !event.getClickedInventory().equals(gui.getInventory())
            || event.getCurrentItem() == null
            || event.getCurrentItem().getItemMeta() == null)
            return;
        event.setCancelled(true);

        // Get lectrum of player
        int lectrum = playerParama.getLectrum();
        GameShop shop = gui.getGameShop();

        //Purchase specified item
        if (event.getSlot() != 0){
            // Gets price of clicked item
            int price = shop.getPrices().get(event.getSlot());
            if (lectrum < price) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Not enough lectrum!");
            } else {
                plugin.getPlayerParama(player).removeLectrum(price);
                if(shop.giveItem(event)) shop.updateLectrum(event);
            }
        }

    }
}
