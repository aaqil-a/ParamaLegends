package id.cuna.ParamaLegends.Command;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.CHEST,"§6Odd Wares").setCustomNameVisible(true);
                    case "destiny" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.END_PORTAL_FRAME,"§6Your Destiny").setCustomNameVisible(true);
                    case "archeryshop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.FLETCHING_TABLE,"§aFletcher's Table");
                    case "sworsdmanshop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.ANVIL,"§2Dull Anvil");
                    case "reapershop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.GRINDSTONE,"§4Reaper Grindstone");
                    case "magicshop" ->
                        plugin.commandStartGame.spawnArmorStand(player.getLocation(), Material.ENCHANTING_TABLE,"§5Ancient Tomes");
                    default -> sendAltarNames(player);
                }
            }
        }
        return true;
    }
}
