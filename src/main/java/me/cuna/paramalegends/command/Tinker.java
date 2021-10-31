package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class Tinker implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public Tinker(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;

            // Soul Ring
            ItemStack item = new ItemStack(Material.ENDER_EYE);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            List<String> lore = new ArrayList<String>();
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"dSoul Ring");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Consumes 1 heart to temporarily");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "gain 150 mana.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 25 seconds");
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.getWorld().dropItem(player.getLocation(), item);
            lore.clear();
            // Overwhelming Blink
            item.setType(Material.BLAZE_ROD);
            meta.setDisplayName(ChatColor.RESET + "" + ChatColor.COLOR_CHAR+"dOverwhelming Blink");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Instantaneously teleport to");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the targeted location and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "damages nearby monsters.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 15 seconds");
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.getWorld().dropItem(player.getLocation(), item);
            lore.clear();

        }
        return true;
    }


}
