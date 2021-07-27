package id.cuna.ParamaLegends.BossListener.AltarTypeListener;

import id.cuna.ParamaLegends.BossListener.AltarListener;
import id.cuna.ParamaLegends.BossType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NatureAltarListener extends AltarListener implements Listener {

    private final ParamaLegends plugin;
    private final DataManager data;

    public NatureAltarListener(ParamaLegends plugin){
        super(plugin, BossType.NATURE);
        this.plugin = plugin;
        this.data = plugin.getData();
    }

    public void spawnAltar(World world, int altarX, int altarZ){

        Location location = new Location(world, altarX, world.getHighestBlockYAt(altarX, altarZ), altarZ);
        Location placeLocation;
        location.add(0,1,0);
        //Build first layer
        String[] blockMap = {"DFD", "FDF", "DFD"};
        for(int x = 0; x < blockMap.length; x++){
            placeLocation = location.clone().add(x,0,0);
            for(int z = 0; z < blockMap[x].length(); z++) {
                switch(blockMap[x].charAt(z)){
                    case 'D' ->{
                        placeLocation.getBlock().setType(Material.ROOTED_DIRT);
                    }
                    case 'F' -> {
                        placeLocation.getBlock().setType(Material.OAK_FENCE);
                    }
                }
                placeLocation.add(0,0,1);
            }
        }
        //Build second layer
        blockMap = new String[]{"APA", "PDP", "APA"};
        for(int x = 0; x < blockMap.length; x++){
            placeLocation = location.clone().add(x,1,0);
            for(int z = 0; z < blockMap[x].length(); z++) {
                switch(blockMap[x].charAt(z)){
                    case 'D' ->{
                        placeLocation.getBlock().setType(Material.ROOTED_DIRT);
                    }
                    case 'P' -> {
                        Block pumpkinBlock = placeLocation.getBlock();
                        pumpkinBlock.setType(Material.JACK_O_LANTERN);
                        BlockFace direction;
                        if(x == 1 && z == 0) direction = BlockFace.NORTH;
                        else if(x == 1 && z == 2) direction = BlockFace.SOUTH;
                        else if(x == 2 && z == 1) direction = BlockFace.EAST;
                        else direction = BlockFace.WEST;
                        Directional pumpkin = ((Directional) placeLocation.getBlock().getBlockData());
                        pumpkin.setFacing(direction);
                        pumpkinBlock.setBlockData(pumpkin);
                    }
                    case 'A' -> {
                        placeLocation.getBlock().setType(Material.AIR);
                    }
                }
                placeLocation.add(0,0,1);
            }
        }
        //Fill three layers with air
        for(int y = 0; y < 3; y++){
            placeLocation = location.clone().add(0,2+y,0);
            for(int x = 0; x < 3; x++){
                placeLocation.add(1,0,0);
                for(int z = 0; z < 3; z++) {
                    placeLocation.getBlock().setType(Material.AIR);
                    placeLocation.add(0,0,1);
                }
            }
        }

        //Spawn altar interact
        placeLocation = location.clone().add(1.5,2,1.5);
        placeLocation.getWorld().spawn(placeLocation, ArmorStand.class, armorStand -> {
            armorStand.setCustomName(super.getTypeName());
            armorStand.setCustomNameVisible(true);
            armorStand.setSilent(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setCollidable(false);
            armorStand.setCanPickupItems(false);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.SLIME_BLOCK));
            armorStand.setInvisible(true);
        });
    }


    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        Inventory gui;
        gui = Bukkit.createInventory(null,9, super.getTypeName());

        ItemStack item = new ItemStack(Material.SLIME_BLOCK);
        ItemMeta meta = item.getItemMeta();

        // Slime boss summoning item
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName("§aEssence of Nature");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        item.setType(Material.PAPER);
        meta.setDisplayName("§7Evoking Components");
        lore.add(ChatColor.DARK_GRAY+"An eye of the void    ///");
        lore.add(ChatColor.DARK_GRAY+"Four exploding powder");
        lore.add(ChatColor.DARK_GRAY+"Four osseous matter");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();

        return gui;
    }


}
