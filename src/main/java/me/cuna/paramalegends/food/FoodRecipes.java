package me.cuna.paramalegends.food;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FoodRecipes {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final ItemStack rice;

    public FoodRecipes(ParamaLegends plugin){
        this.plugin = plugin;
        this.data = plugin.getData();

        //sushi
        ItemStack sushi = makeSkull( "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTUzNDdkYWRmNjgxOTlmYTdhMWI2NmYwNDgxYWQ4ZTlkYWVlMTUxMDg2NWFkZDZmMzNkMTVmYjM3OGQxM2U5MSJ9fX0=");
        ItemMeta sushiItemmeta = sushi.getItemMeta();
        sushiItemmeta.setDisplayName(ChatColor.COLOR_CHAR+"5Sushi");
        sushi.setItemMeta(sushiItemmeta);

        //rice
        rice = makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzM3N2UzZDZjMzc5ZmUzNGEyZTZhZmFiYmEzMmU3YWVjZjc3YmNkMzFhMWMzODM2ZWMzNTRhOTM1YTdlOSJ9fX0=");
        ItemMeta riceItemmeta = rice.getItemMeta();
        riceItemmeta.setDisplayName(ChatColor.COLOR_CHAR+"5Bowl Of Rice");
        rice.setItemMeta(riceItemmeta);

        //sandwich
        ItemStack sandwich = makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTVmYWZkODk3MmI2Yjc2OTBmYjEzMWRjM2Y5MTdjNTU5OTkzOGY4N2I1ODRjMmY1ZTdkNDBhMGRlNDFlNTJmIn19fQ==");
        ItemMeta sandwichItemMeta = sandwich.getItemMeta();
        sandwichItemMeta.setDisplayName(ChatColor.COLOR_CHAR + "5Sandwich");
        sandwich.setItemMeta(sandwichItemMeta);

        //cilor
        ItemStack cilor = makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmUzMDUyYzUzNWUxNDU5N2E0MTNlYzMyYjMyYWFmZGQyODY4NmZkYWI2ZWVkNzMwMzBlMWI5NGY3YzM4ZmYifX19");
        ItemMeta cilorMeta = cilor.getItemMeta();
        cilorMeta.setDisplayName(ChatColor.COLOR_CHAR + "5Cilor");
        cilor.setItemMeta(cilorMeta);

        //hot chocolate
        ItemStack coldChoco = makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRhZTE5MmNlYzI4NTBiMjQ1YjgyM2ExNWNlNTVmMzMyZjA5YzQ5MWIxNWE5NjQ1Yzk4MmI4OGM1NjRkNGMyIn19fQ==");
        ItemMeta coldChocoMeta = coldChoco.getItemMeta();
        coldChocoMeta.setDisplayName(ChatColor.COLOR_CHAR + "5Cold Chocolate");
        coldChoco.setItemMeta(coldChocoMeta);

        ItemStack hotChoco = makeSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRhZTE5MmNlYzI4NTBiMjQ1YjgyM2ExNWNlNTVmMzMyZjA5YzQ5MWIxNWE5NjQ1Yzk4MmI4OGM1NjRkNGMyIn19fQ==");
        ItemMeta hotChocoMeta = hotChoco.getItemMeta();
        hotChocoMeta.setDisplayName(ChatColor.COLOR_CHAR + "5Hot Chocolate");
        hotChoco.setItemMeta(hotChocoMeta);

        NamespacedKey sushiKey = new NamespacedKey(plugin, "sushi");
        NamespacedKey riceKey = new NamespacedKey(plugin, "rice");
        NamespacedKey sandwichKey = new NamespacedKey(plugin, "sandwich");
        NamespacedKey cilorKey = new NamespacedKey(plugin, "cilor");
        NamespacedKey hotChocoKey = new NamespacedKey(plugin, "hotchoco");
        NamespacedKey coldChocoKey = new NamespacedKey(plugin, "coldchoco");

        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(hotChocoKey, hotChoco,  new RecipeChoice.ExactChoice(coldChoco), 100, 61);
        Bukkit.addRecipe(furnaceRecipe);

        //sushi recipe
        ShapedRecipe recipe = new ShapedRecipe(sushiKey, sushi);
        recipe.shape(" S ","KRK");
        recipe.setIngredient('R', new RecipeChoice.ExactChoice(rice));
        recipe.setIngredient('S', Material.SALMON);
        recipe.setIngredient('K', Material.KELP);
        Bukkit.addRecipe(recipe);

        //rice recipe
        recipe = new ShapedRecipe(riceKey, rice);
        recipe.shape("WWW", "WWW", " B ");
        recipe.setIngredient('W', Material.WHEAT);
        recipe.setIngredient('B', Material.BOWL);
        Bukkit.addRecipe(recipe);

        //sandwich recipe
        recipe = new ShapedRecipe(sandwichKey, sandwich);
        recipe.shape(" B ", "PEK", " B ");
        recipe.setIngredient('B', Material.BREAD);
        recipe.setIngredient('P', Material.COOKED_PORKCHOP);
        recipe.setIngredient('E', Material.EGG);
        recipe.setIngredient('K', Material.KELP);
        Bukkit.addRecipe(recipe);

        //cilor recipe
        recipe = new ShapedRecipe(cilorKey, cilor);
        recipe.shape(" E ", "WWW", "EEE");
        recipe.setIngredient('E', Material.EGG);
        recipe.setIngredient('W', Material.WHEAT);
        Bukkit.addRecipe(recipe);

        //hotchoco reccipe
        recipe = new ShapedRecipe(coldChocoKey, coldChoco);
        recipe.shape("AAA", "CCC", "AMA");
        recipe.setIngredient('A', Material.AIR);
        recipe.setIngredient('C', Material.COCOA_BEANS);
        recipe.setIngredient('M', Material.MILK_BUCKET);
        Bukkit.addRecipe(recipe);
    }

    public static ItemStack getSkull(String url, String itemName, String itemlore) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty()) {
            return head;
        }

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }
        headMeta.setDisplayName(itemName);
        List<String> lore = new ArrayList<>();
        lore.add(itemlore);
        headMeta.setLore(lore);
        head.setItemMeta(headMeta);
        return head;
    }
    public ItemStack makeSkull(String base64EncodedString) {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        assert meta != null;
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64EncodedString));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(meta);
        return skull;
    }

    public ItemStack getRice() {
        return rice;
    }
}
