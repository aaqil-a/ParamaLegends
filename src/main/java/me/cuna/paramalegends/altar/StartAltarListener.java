package me.cuna.paramalegends.altar;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.boss.BossType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StartAltarListener extends AltarListener implements Listener {

    private final ParamaLegends plugin;
    private final DataManager data;

    public StartAltarListener(ParamaLegends plugin){
        super(plugin, BossType.START);
        this.plugin = plugin;
        this.data = plugin.dataManager;
    }

    public void spawnAltar(World world, int altarX, int altarY, int altarZ){

        Location location = new Location(world, altarX, altarY, altarZ);
        Location placeLocation;
        //Spawn altar interact
        placeLocation = location.clone().add(-2,0,-2);
        placeLocation.add(0,1,0);
        placeLocation.getWorld().spawn(placeLocation, ArmorStand.class, armorStand -> {
            armorStand.setCustomName(super.getTypeName());
            armorStand.setCustomNameVisible(true);
            armorStand.setSilent(true);
            armorStand.setGravity(false);
            armorStand.setInvulnerable(true);
            armorStand.setCollidable(false);
            armorStand.setCanPickupItems(false);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.CRYING_OBSIDIAN));
            armorStand.setInvisible(true);
        });
    }


    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        Inventory gui;
        gui = Bukkit.createInventory(null,27, super.getTypeName());

        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        gui.setItem(13, item);

        item.setType(Material.BONE);
        gui.setItem(3, item);
        gui.setItem(5, item);
        gui.setItem(21, item);
        gui.setItem(23, item);

        item.setType(Material.GUNPOWDER);
        gui.setItem(4, item);
        gui.setItem(12, item);
        gui.setItem(14, item);
        gui.setItem(22, item);

        return gui;
    }
}
