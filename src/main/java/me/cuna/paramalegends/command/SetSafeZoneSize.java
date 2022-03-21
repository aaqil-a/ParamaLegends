package me.cuna.paramalegends.command;

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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SetSafeZoneSize implements CommandExecutor {

    public DataManager data;

    public SetSafeZoneSize(final ParamaLegends plugin){
        data = plugin.dataManager;
    }

    public void sendUsage(CommandSender sender){
        sender.sendMessage(ChatColor.RED+"Usage: /safezone <minimize/maximize>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            sendUsage(sender);
        } else if(sender instanceof Player) {
            if(args[0].equalsIgnoreCase("minimize")){
                Player player = (Player) sender;
                // Get worldguard regions
                World worldGuard = BukkitAdapter.adapt(player.getWorld());
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(worldGuard);
                ProtectedRegion safezone = regions.getRegion("safezone");
                //create safe zone
                BlockVector3 min = safezone.getMinimumPoint();
                BlockVector3 max = safezone.getMaximumPoint();
                ProtectedRegion region = new ProtectedCuboidRegion("safezone", min.subtract(-16, 0, -16), max.subtract(16,0,16));
                region.setPriority(1);
                region.setFlag(Flags.GREET_MESSAGE, ChatColor.GREEN + "You feel slightly more protected...");
                region.setFlag(Flags.FAREWELL_MESSAGE, ChatColor.RED + "The wilderness exudes a threatening aura...");
                region.setFlag(Flags.HEALTH_REGEN, StateFlag.State.ALLOW);
                region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);

                regions.addRegion(region);
            } else if(args[0].equalsIgnoreCase("maximize")){
                Player player = (Player) sender;
                // Get worldguard regions
                World worldGuard = BukkitAdapter.adapt(player.getWorld());
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regions = container.get(worldGuard);
                ProtectedRegion safezone = regions.getRegion("safezone");
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
            } else {
                sendUsage(sender);
            }

        }
        return true;
    }


}
