package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReaperShop extends GameShop {

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<Integer, Integer>(){{
        put(4,5);
        put(9,10);
        put(11,40);
        put(13,300);
        put(15,400);
        put(17,600);
    }};

    public ReaperShop(final ParamaLegends plugin){
        super(plugin, "§4Reaper Grindstone");
    }

    //Attack player when npc attacked according to npc type
    @Override
    public void NPCAttack(Player player, Entity npc){
        Entity cloudEntity = player.getLocation().getWorld().spawnEntity(player.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
        AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
        cloud.setParticle(Particle.CRIT);
        cloud.setDuration(1);
        player.damage(10, npc);
    }

    //Get prices
    @Override
    public HashMap<Integer, Integer> getPrices(){
        return prices;
    }

    @Override
    //Purchase item from gui
    public boolean giveItem(InventoryClickEvent event){
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getWhoClicked().getInventory();
        ItemStack newItem;
        if(item.getType().equals(Material.ANVIL)){
            ItemStack hoe = checkHoe(inventory);
            if(hoe == null) return false;
            inventory.removeItem(hoe);
            newItem = hoeToScythe(hoe);
        } else {
            newItem = item.clone();
            ItemMeta meta = newItem.getItemMeta();
            List<String> lore = meta.getLore();
            lore.remove(lore.size()-1);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            newItem.setItemMeta(meta);
        }
        if(newItem != null) inventory.addItem(newItem);
        return true;
    }

    public ItemStack checkHoe(Inventory inv){
        ItemStack[] playerInv = inv.getStorageContents();
        for (ItemStack item : playerInv) {
            if(item != null){
                if(item.getType().equals(Material.IRON_HOE) || item.getType().equals(Material.WOODEN_HOE) || item.getType().equals(Material.STONE_HOE)
                        || item.getType().equals(Material.DIAMOND_HOE) || item.getType().equals(Material.GOLDEN_HOE) || item.getType().equals(Material.NETHERITE_HOE)){
                    if (item.hasItemMeta() && (item.getItemMeta().getDisplayName().contains("Scythe") || item.getItemMeta().getDisplayName().contains("Memento Mori"))) continue;
                    return item;
                }
            }
        }
        return null;
    }

    public ItemStack hoeToScythe (ItemStack item){
        ItemStack newItem = item.clone();
        int newDamage = 0;
        String name = "";
        switch (newItem.getType()) {
            case WOODEN_HOE -> {
                newDamage = 3;
                name = "§4Wooden Scythe";
            }
            case GOLDEN_HOE -> {
                newDamage = 3;
                name = "§4Golden Scythe";
            }
            case IRON_HOE -> {
                newDamage = 5;
                name = "§4Iron Scythe";
            }
            case STONE_HOE -> {
                newDamage = 4;
                name = "§4Stone Scythe";
            }
            case DIAMOND_HOE -> {
                newDamage = 6;
                name = "§4Diamond Scythe";
            }
            case NETHERITE_HOE -> {
                newDamage = 7;
                name = "§4Netherite Scythe";
            }
        }
        if (newDamage > 0){
            ItemMeta meta = newItem.getItemMeta();
            List<String> lore = meta.getLore();
            if(lore == null) {
                lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.GRAY+ "When in Main Hand:");
                lore.add(" " + ChatColor.DARK_GREEN + "" + newDamage + " Attack Damage");
            }
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setDisplayName(name);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("scythebonus", newDamage, AttributeModifier.Operation.ADD_NUMBER));
            newItem.setItemMeta(meta);
        }
        return newItem;
    }

    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        int playerLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+".reaper");
        Inventory gui;
        gui = Bukkit.createInventory(null,18, "§4Reaper's Weaponry");

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(0, item);
        lore.clear();

        item.setType(Material.ANVIL);
        meta.setDisplayName(ChatColor.RESET + "§4Enchant to Scythe");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Upgrade hoe to scythe.");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "5 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        if(playerLevel >= 2){
            // Hidden Strike
            item.setType(Material.LEATHER);
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            meta.setDisplayName(ChatColor.RESET + "§4Hidden Strike");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Conceal your weapon, stabbing the");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "enemy at a critical location while");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "also inflicting Coated Blade");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "on the next attack.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.reaperListener.hiddenStrike.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Reaper 2");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(9, item);
            lore.clear();
        }
        if(playerLevel >= 3){
            // Blinding Sand
            item.setType(Material.SAND);
            meta.setDisplayName(ChatColor.RESET + "§4Blinding Sand");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Throw sand into the enemy");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making them temporarily confused.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.reaperListener.blindingSand.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Reaper 3");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "40 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(11, item);
            lore.clear();
        }
        if(playerLevel >= 8){
            // Gut Punch
            item.setType(Material.LIGHTNING_ROD);
            meta.setDisplayName(ChatColor.RESET + "§4Gut Punch");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Deals damage based on the");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "opponent's current HP and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "inflicts high discomfort on");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "opponent.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.reaperListener.gutPunch.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 9 seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Reaper 8");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(13, item);
            lore.clear();
        }
        if(playerLevel >= 9){
            // Forbidden Slash
            item.setType(Material.END_ROD);
            meta.setDisplayName(ChatColor.RESET + "§4Forbidden Slash");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Ready your weapon, dealing a");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "decisive slash to the enemy and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making them do less damage on");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the next attack.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.reaperListener.forbiddenSlash.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 20 seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Reaper 9");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(15, item);
            lore.clear();
        }
        if(playerLevel >= 10){
            // Memento Mori
            item.setType(Material.NETHERITE_HOE);
            meta.setDisplayName(ChatColor.RESET + "§4Memento Mori");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Strike with the power of the");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "grim reaper, dealing immense");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "damage to a single target.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.reaperListener.mementoMori.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1 minute");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Reaper 10");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "600 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(17, item);
            lore.clear();
        }
        return gui;
    }
}
