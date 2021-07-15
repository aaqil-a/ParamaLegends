package id.cuna.ParamaLegends.BossListener;

import id.cuna.ParamaLegends.BossType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AltarListener implements Listener {

    private final ParamaLegends plugin;
    private final DataManager data;
    private Inventory gui;
    private final BossType type;
    private final String typeName;

    public AltarListener(ParamaLegends plugin, BossType type){
        this.plugin = plugin;
        this.data = plugin.getData();
        this.type = type;
        if(type == null){
            this.typeName = "§6Occult Altar";
        } else {
            this.typeName = switch(type){
                case NATURE -> "§aMysterious Sludge";
                case EARTH -> null;
                case WATER -> null;
                case FIRE -> null;
                case VOID -> null;
            };
        }
    }

    public void createAltarLocation(World world){
        double startX = data.getConfig().getDouble("world.startX");
        double startZ = data.getConfig().getDouble("world.startZ");

        //Get random x offset and z offset for altar locationx.
        Random rand = new Random();
        int offsetX = 200 - rand.nextInt(400) ;
        int offsetZ = 200 - rand.nextInt(400) ;
        if(offsetX > 0) offsetX += 300;
        else offsetX -= 300;
        if(offsetZ > 0) offsetZ += 300;
        else offsetZ -= 300;
        double altarX = startX + offsetX;
        double altarZ = startZ + offsetZ;
        data.getConfig().set("world.altarX", altarX);
        data.getConfig().set("world.altarZ", altarZ);
        data.saveConfig();
        spawnAltar(world);
    }

    public void spawnAltar(World world){

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
                gui = createGui(player, data);
                player.openInventory(gui);
            }
        }
    }

    //Handle events when item clicked in gui
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        //Check if item clicked is in gui
        if (event.getClickedInventory() == null)
            return;
        if (event.getInventory().equals(gui) && !event.getClickedInventory().equals(gui)) {
            event.setCancelled(true);
            return;
        }
        if (!event.getClickedInventory().equals(gui))
            return;
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        event.setCancelled(true);

        //purchase summoning item
        if (event.getSlot() == 4){
            if(takeIngredient(event.getWhoClicked().getInventory())) giveItem(event.getWhoClicked().getInventory(), event.getCurrentItem());
            else event.getWhoClicked().sendMessage(ChatColor.RED+"You do not have the exact required components.");
        }

    }

    //Give summoning item
    public void giveItem(Inventory inventory, ItemStack item){
        ItemStack newItem = item.clone();
        inventory.addItem(newItem);
    }

    //Take summoning ingredients
    public boolean takeIngredient(Inventory inventory){
        return false;
    }

    //Create gui
    public Inventory createGui(Player player, DataManager data){
        player.sendMessage("This NPC has no gui!");
        return null;
    }

}
