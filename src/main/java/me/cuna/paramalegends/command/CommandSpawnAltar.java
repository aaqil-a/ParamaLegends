package me.cuna.paramalegends.command;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandSpawnAltar implements CommandExecutor {

    public DataManager data;
    private ParamaLegends plugin;

    public CommandSpawnAltar(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public void sendUsage(Player player){
        player.sendMessage(ChatColor.RED+"Usage: /spawnaltar <altarname>");
    }

    public void sendAltarNames(Player player){
        player.sendMessage(ChatColor.RED+"Valid Altars: OccultAltar, NatureAltar, OddWares, Destiny, ArcheryShop, SwordsmanShop, ReaperShop, MagicShop");
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0){
                sendUsage(player);
            } else {
                switch (args[0].toLowerCase()) {
                    case "occultaltar" ->
                        plugin.startAltarListener.spawnAltar(player.getWorld(), (int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ());
                    case "naturealtar" ->
                        plugin.natureAltarListener.spawnAltar(player.getWorld(), (int) player.getLocation().getX(),(int) player.getLocation().getY(), (int) player.getLocation().getZ());
                    case "oddwares" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.CHEST,ChatColor.COLOR_CHAR+"6Odd Wares").setCustomNameVisible(true);
                    case "destiny" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.END_PORTAL_FRAME,ChatColor.COLOR_CHAR+"6Your Destiny").setCustomNameVisible(true);
                    case "archeryshop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.FLETCHING_TABLE,ChatColor.COLOR_CHAR+"aFletcher's Table");
                    case "swordsmanshop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.ANVIL,ChatColor.COLOR_CHAR+"2Dull Anvil");
                    case "reapershop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.GRINDSTONE,ChatColor.COLOR_CHAR+"4Reaper Grindstone");
                    case "magicshop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.ENCHANTING_TABLE,ChatColor.COLOR_CHAR+"5Ancient Tomes");
                    default -> sendAltarNames(player);
                }
            }
        }
        return true;
    }
}
