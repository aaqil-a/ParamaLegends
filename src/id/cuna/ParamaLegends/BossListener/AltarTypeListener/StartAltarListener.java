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

public class StartAltarListener extends AltarListener implements Listener {

    private final ParamaLegends plugin;
    private final DataManager data;

    public StartAltarListener(ParamaLegends plugin){
        super(plugin, null);
        this.plugin = plugin;
        this.data = plugin.getData();
    }

    @Override
    public void spawnAltar(World world){
        int altarX = (int) data.getConfig().getDouble("world.startX");
        int altarZ = (int) data.getConfig().getDouble("world.startZ");

        Location location = new Location(world, altarX, world.getHighestBlockYAt(altarX, altarZ), altarZ);
        Location placeLocation;
        //Spawn altar interact
        placeLocation = location.clone().add(-2,0,-2);
        placeLocation = placeLocation.getWorld().getHighestBlockAt(placeLocation).getLocation();
        placeLocation.getWorld().spawn(placeLocation, ArmorStand.class, armorStand -> {
            armorStand.setCustomName("§6Occult Altar");
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
        gui = Bukkit.createInventory(null,9, "§6Occult Altar");

        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();

        // Raid boss summoning item
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName("§6Esoteric Pearl");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY+"A mysterious pearl with");
        lore.add(ChatColor.GRAY+"a threatening aura.");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        // Raid boss summoning item
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

    //Take summoning ingredients
    @Override
    public boolean takeIngredient(Inventory inventory){
        ItemStack gunpowder = new ItemStack(Material.GUNPOWDER);
        gunpowder.setAmount(4);
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL);
        pearl.setAmount(1);
        ItemStack bone = new ItemStack(Material.BONE);
        bone.setAmount(4);
        if(inventory.contains(gunpowder)
        && inventory.contains(pearl)
        && inventory.contains(bone)){
            inventory.remove(gunpowder);
            inventory.remove(pearl);
            inventory.remove(bone);
            return true;
        }
        return false;
    }


}
