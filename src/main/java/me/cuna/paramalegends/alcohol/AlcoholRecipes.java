package me.cuna.paramalegends.alcohol;


import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class AlcoholRecipes {

    private final ParamaLegends plugin;
    private final DataManager data;

    public AlcoholRecipes(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.dataManager;

        //apple wine
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        potionMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"dUnaged Apple Wine");
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 60, 1), true);
        potionMeta.setColor(Color.ORANGE);
        potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(potionMeta);


        NamespacedKey key = new NamespacedKey(plugin, "unaged_apple_wine");

        ShapelessRecipe recipe = new ShapelessRecipe(key, item);
        recipe.addIngredient(Material.POTION);
        recipe.addIngredient(Material.SUGAR);
        recipe.addIngredient(Material.APPLE);
        recipe.addIngredient(Material.GUNPOWDER);

        Bukkit.addRecipe(recipe);

        //vodka
        item = new ItemStack(Material.POTION);
        potionMeta = (PotionMeta) item.getItemMeta();
        potionMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"dUnaged Vodka");
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 60, 2), true);
        potionMeta.setColor(Color.WHITE);
        potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(potionMeta);

        key = new NamespacedKey(plugin, "unaged_vodka");

        recipe = new ShapelessRecipe(key, item);
        recipe.addIngredient(Material.POTION);
        recipe.addIngredient(Material.SUGAR);
        recipe.addIngredient(Material.WHEAT);
        recipe.addIngredient(Material.POTATO);


        Bukkit.addRecipe(recipe);

        //pale ale
        item = new ItemStack(Material.POTION);
        potionMeta = (PotionMeta) item.getItemMeta();
        potionMeta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"dUnaged Pale Ale");
        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 80, 1), true);
        potionMeta.setColor(Color.RED);
        potionMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(potionMeta);

        key = new NamespacedKey(plugin, "unaged_pale_ale");

        recipe = new ShapelessRecipe(key, item);
        recipe.addIngredient(Material.POTION);
        recipe.addIngredient(Material.SUGAR);
        recipe.addIngredient(Material.SWEET_BERRIES);

        Bukkit.addRecipe(recipe);
        
    }

}
