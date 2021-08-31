package me.cuna.paramalegends.altar;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.boss.BossType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class AltarListener implements Listener {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final HashMap<Player, Inventory> gui = new HashMap<>();
    private final String typeName;

    public AltarListener(ParamaLegends plugin, BossType type){
        this.plugin = plugin;
        this.data = plugin.getData();
        this.typeName = switch(type){
            case START -> "§6Occult Altar";
            case NATURE -> "§aMysterious Sludge";
            case EARTH -> "§0Indestructible Stone";
            case WATER, FIRE, VOID-> null;
        };
    }

    //Cancel damage of altar event
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntityType() == EntityType.ARMOR_STAND && event.getEntity().getName().equals(typeName))
            event.setCancelled(true);
    }

    //Open gui when right click altar
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof ArmorStand) {
            if (event.getRightClicked().getName().equals(typeName)){
                event.setCancelled(true);
                gui.put(player, createGui(player, data));
                player.openInventory(gui.get(player));
            }
        }
    }

    //Handle events when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        //Check if item clicked is in gui
        if (event.getClickedInventory() == null)
            return;
        if (event.getInventory().equals(gui.get(player)) && !event.getClickedInventory().equals(gui.get(player))) {
            event.setCancelled(true);
            return;
        }
        if (!event.getClickedInventory().equals(gui.get(player)))
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        event.setCancelled(true);
    }

    //Create gui
    public Inventory createGui(Player player, DataManager data){
        player.sendMessage("This NPC has no gui!");
        return null;
    }

    //altar name getter
    public String getTypeName(){
        return typeName;
    }

}
