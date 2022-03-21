package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.boss.BossType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetupGame implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;
    private boolean currentlySettingUp;

    public SetupGame(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
        currentlySettingUp = false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(currentlySettingUp){
                sender.sendMessage(ChatColor.RED+"Game is currently being setup.");
            } else if(data.getConfig().getInt("world.level")==0){
                currentlySettingUp = true;
                sender.sendMessage(ChatColor.GREEN+"Parama Legends setup begun.");
                sender.sendMessage(ChatColor.GRAY+"Throughout Parama Legends, players will need to traverse multiple areas" +
                        " to accomplish their goals.");
                sender.sendMessage(ChatColor.GRAY+"Mark the areas that players must traverse according to the appropriate biome.");
                sender.sendMessage(ChatColor.GRAY+"It is recommended to space out each area by a fair distance.");
                sender.sendMessage(ChatColor.GRAY+"Use the /locatebiome command to help find an appropriate biome.");
                sender.sendMessage(ChatColor.GOLD+"Mark starting area with wand.");
                sender.sendMessage(ChatColor.GOLD+"Valid Biomes: Any.");

                //create wand item
                ItemStack wand = new ItemStack(Material.SHEARS);
                ItemMeta meta = wand.getItemMeta();
                meta.setDisplayName(ChatColor.COLOR_CHAR+"6Game Wand");
                wand.setItemMeta(meta);
                ((Player) sender).getInventory().addItem(wand);

                plugin.gameManager.setup.setMarkType(BossType.START);
            } else {
                sender.sendMessage(ChatColor.RED+"Game has already been setup. Remove plugin config.yml file to restart game.");
            }
            return true;
        }
        return false;
    }

    public void setCurrentlySettingUp(boolean currentlySettingUp){
        this.currentlySettingUp = currentlySettingUp;
    }

    public boolean isCurrentlySettingUp(){
        return currentlySettingUp;
    }
}
