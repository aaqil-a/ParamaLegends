package me.cuna.paramalegends.shopgame;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeneralShop extends GameShop {

    private final int[] expansePrice = {400, 750, 1500, 2500, 4000, 8000, 10000};

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<>(){{
        put(2,1);
        put(4,3);
        put(6,4);
        put(8,5);
        put(10,50);
        put(12,10);
        put(14,20);
    }};

    public GeneralShop(ParamaLegends plugin) {
        super(plugin, ChatColor.COLOR_CHAR+"6Odd Wares");
    }

    //Get prices
    @Override
    public HashMap<Integer, Integer> getPrices(){
        return prices;
    }

    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        Inventory gui;
        gui = Bukkit.createInventory(null,18, ChatColor.COLOR_CHAR+"6Odd Wares");

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(0, item);
        lore.clear();

        // Classic Arrows
        item.setType(Material.ARROW);
        meta.setDisplayName("");
        item.setAmount(8);
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "1 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        // Healing Potion
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"dHealing Potion");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "3 Lectrum");
        potionMeta.setLore(lore);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1), true);
        potionMeta.setColor(Color.FUCHSIA);
        potion.setItemMeta(potionMeta);
        gui.setItem(4, potion);
        lore.clear();

        // Healing Potion
        potionMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"dRegeneration Potion");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "4 Lectrum");
        potionMeta.setLore(lore);
        potionMeta.removeCustomEffect(PotionEffectType.HEAL);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 260, 1), true);
        potionMeta.setColor(Color.PURPLE);
        potion.setItemMeta(potionMeta);
        gui.setItem(6, potion);
        lore.clear();

        // Expanse Fund
        item.setType(Material.DIAMOND);
        item.setAmount(1);
        meta.setDisplayName(ChatColor.GOLD + "Expanse Fund");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Expand safe zone region.");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Every player can contribute");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "to the fund.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Remaining lectrum required: " + data.getConfig().getInt("world.expanseFund"));
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "5 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

        // Alcohol License
        item.setType(Material.PAPER);
        item.setAmount(1);
        meta.setDisplayName(ChatColor.GOLD + "Alcohol License");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Only those who possess this");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "license can produce alcoholic");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "beverages.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Expires one week after purchase.");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "50 Lectrum");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        gui.setItem(10, item);
        lore.clear();

        // Mana Potion
        potionMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"9Mana Potion");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Replenishes 100 mana points.");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        potionMeta.setLore(lore);
        potionMeta.removeCustomEffect(PotionEffectType.REGENERATION);
        potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 0, 0), true);
        potionMeta.setColor(Color.BLUE);
        potion.setItemMeta(potionMeta);
        gui.setItem(12, potion);
        lore.clear();

        // Greater Mana Potion
        potionMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"9Greater Mana Potion");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Replenishes 200 mana points.");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "20 Lectrum");
        potionMeta.setLore(lore);
        potion.setItemMeta(potionMeta);
        gui.setItem(14, potion);
        lore.clear();

        return gui;
    }


    //Purchase item from gui
    @Override
    public boolean giveItem(InventoryClickEvent event){
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getWhoClicked().getInventory();
        if(item.getType().equals(Material.ARROW)){
            ItemStack arrow = new ItemStack(Material.ARROW, 8);
            inventory.addItem(arrow);
        } else if(item.getType().equals(Material.DIAMOND)) {
            //expanse fund
            int expanseFund = data.getConfig().getInt("world.expanseFund");
            int expanseLevel = data.getConfig().getInt("world.expanseLevel");
            expanseFund -= 5;
            if(expanseFund <= 0){
                //expand land
                expandLand((Player) event.getWhoClicked());
                if(expanseLevel > 6){
                    expanseFund = expansePrice[6]*(expanseLevel-5);
                } else {
                    expanseFund = expansePrice[expanseLevel+1];
                }
                data.getConfig().set("world.expanseLevel", expanseLevel+1);
            }
            data.getConfig().set("world.expanseFund", expanseFund);
            data.saveConfig();
            updateExpanseFund(event);
            updateLectrum(event);
        } else if(item.getType().equals(Material.PAPER)) {
            Player player = (Player) event.getWhoClicked();
            long oldLicense = data.getConfig().getLong("players."+player.getUniqueId().toString()+".alcoholLicenseExpiration");
            if(oldLicense > player.getWorld().getGameTime()){
                player.sendMessage(ChatColor.RED+"You already have a valid license.");
            } else {
                player.sendMessage(ChatColor.GOLD+"You now possess a valid alcohol license.");
                player.sendMessage(ChatColor.GOLD+"Will expire in seven (minecraft) days.");
                data.getConfig().set("players."+player.getUniqueId().toString()+".alcoholLicenseExpiration", player.getWorld().getGameTime()+168000);
                data.saveConfig();
            }
        } else {
            ItemStack newItem = item.clone();
            ItemMeta meta = newItem.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(lore.size()-1);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            newItem.setItemMeta(meta);
            inventory.addItem(newItem);
        }
        return true;
    }

    //update expanse fund
    public void updateExpanseFund(InventoryClickEvent event){
        Inventory inv = event.getInventory();
        ItemStack item = new ItemStack(Material.DIAMOND, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Expanse Fund");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Expand safe zone region.");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Every player can contribute");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "to the fund.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Remaining lectrum required: " + data.getConfig().getInt("world.expanseFund"));
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "5 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(8, item);
        lore.clear();
    }

    public void expandLand(Player player){
        Bukkit.broadcastMessage(ChatColor.GREEN+"The world around you feels safer.");
        int maxDepth = data.getConfig().getInt("world.maxdepth");
        if(maxDepth >= 10){
            maxDepth -= 10;
            data.getConfig().set("world.maxdepth", maxDepth);
            plugin.worldRuleListener.setMaxDepth(maxDepth);
        }

        //expand worldguard region
        // Get worldguard regions
        World worldGuard = BukkitAdapter.adapt(player.getWorld());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(worldGuard);
        ProtectedRegion safezone = regions.getRegion("safezone");
        //        regions.removeRegion("safezone");
        //create safe zone
        BlockVector3 min = safezone.getMinimumPoint();
        BlockVector3 max = safezone.getMaximumPoint();
        ProtectedRegion region = new ProtectedCuboidRegion("safezone", min.add(-16, 0, -16), max.add(16,0,16));
        region.setPriority(1);
        region.setFlag(Flags.GREET_MESSAGE, ChatColor.GREEN + "You feel slightly more protected...");
        region.setFlag(Flags.FAREWELL_MESSAGE, ChatColor.RED + "The wilderness exudes a threatening aura...");
        region.setFlag(Flags.HEALTH_REGEN, StateFlag.State.ALLOW);
        region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);

        regions.addRegion(region);
    }
}
