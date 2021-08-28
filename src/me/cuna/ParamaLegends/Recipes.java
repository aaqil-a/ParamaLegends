package me.cuna.ParamaLegends;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Recipes {

    private final ParamaLegends plugin;
    private final DataManager data;

    public Recipes(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.getData();

        //add esoteric pearl
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Esoteric Pearl");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY+"A mysterious pearl with");
        lore.add(ChatColor.GRAY+"a threatening aura.");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(plugin, "esoteric_pearl");

        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("BGB", "GEG", "BGB");
        recipe.setIngredient('B', Material.BONE);
        recipe.setIngredient('G', Material.GUNPOWDER);
        recipe.setIngredient('E', Material.ENDER_PEARL);

        Bukkit.addRecipe(recipe);
        lore.clear();

        //essence of nature
        item = new ItemStack(Material.SLIME_BALL);
        meta = item.getItemMeta();
        meta.setDisplayName("§aEssence of Nature");
        lore.add(ChatColor.GRAY+"A sticky substance that");
        lore.add(ChatColor.GRAY+"radiates life essence.");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        key = new NamespacedKey(plugin, "essence_of_nature");

        recipe = new ShapedRecipe(key, item);
        recipe.shape("BGB", "GEG", "BGB");
        recipe.setIngredient('B', Material.SLIME_BALL);
        recipe.setIngredient('G', Material.SUGAR);
        recipe.setIngredient('E', Material.ENDER_EYE);

        Bukkit.addRecipe(recipe);
        lore.clear();

        //winery
        item = new ItemStack(Material.BARREL);
        meta = item.getItemMeta();
        meta.setDisplayName("§6Winery Barrel");
        lore.add(ChatColor.GRAY+"Used to refine beverages that");
        lore.add(ChatColor.GRAY+"impair the mind.");
        meta.setLore(lore);
        item.setItemMeta(meta);

        key = new NamespacedKey(plugin, "winery_barrel");

        recipe = new ShapedRecipe(key, item);
        recipe.shape(" B ","SSS");
        recipe.setIngredient('B', Material.BARREL);
        recipe.setIngredient('S', Material.STICK);

        Bukkit.addRecipe(recipe);
        lore.clear();
    }


}
