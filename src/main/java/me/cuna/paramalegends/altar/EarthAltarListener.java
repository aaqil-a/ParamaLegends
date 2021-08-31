package me.cuna.paramalegends.altar;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.boss.BossType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EarthAltarListener extends AltarListener implements Listener {


    public EarthAltarListener(ParamaLegends plugin){
        super(plugin, BossType.EARTH);
    }

    public void spawnAltar(World world, int altarX, int altarY, int altarZ){
        Location location = new Location(world, altarX, altarY, altarZ);
        Location placeLocation;
        location.add(0,1,0);
        //Build first layer
        String[] blockMap = {"DFD", "FDF", "DFD"};
        for(int x = 0; x < blockMap.length; x++){
            placeLocation = location.clone().add(x,0,0);
            for(int z = 0; z < blockMap[x].length(); z++) {
                switch(blockMap[x].charAt(z)){
                    case 'D' ->{
                        placeLocation.getBlock().setType(Material.DEEPSLATE_TILES);
                    }
                    case 'F' -> {
                        placeLocation.getBlock().setType(Material.DEEPSLATE_TILE_WALL);
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
                        placeLocation.getBlock().setType(Material.DEEPSLATE_TILES);
                    }
                    case 'P' -> {
                        Block pumpkinBlock = placeLocation.getBlock();
                        pumpkinBlock.setType(Material.DEEPSLATE_TILE_SLAB);
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
            armorStand.getEquipment().setHelmet(new ItemStack(Material.OBSIDIAN));
            armorStand.setInvisible(true);
        });
    }


    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        Inventory gui;
        gui = Bukkit.createInventory(null,9, super.getTypeName());

        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        // Raid boss summoning item
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName("§6Esoteric Pearl");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A mysterious pearl with");
        lore.add(ChatColor.GRAY+"a threatening aura.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        item.setType(Material.PAPER);
        meta.setDisplayName("§7Evoking Components");
        lore.add(ChatColor.DARK_GRAY+"A pearl of end");
        lore.add(ChatColor.DARK_GRAY+"Four exploding powder");
        lore.add(ChatColor.DARK_GRAY+"Four osseous matter");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();

        return gui;
    }

}
