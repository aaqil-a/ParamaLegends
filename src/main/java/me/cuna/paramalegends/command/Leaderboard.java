package me.cuna.paramalegends.command;

import com.mojang.datafixers.util.Pair;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;


public class Leaderboard implements CommandExecutor {

    private final ParamaLegends plugin;
    public DataManager data;

    public Leaderboard(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public ItemStack createBook(){
        List<Pair<String, Integer>> top = new ArrayList<>(plugin.leaderboard.getNetWorth());

        Pair<String, Integer> blank = new Pair<>("---", 0);
        //fill if empty
        if(top.size() < 10){
            for(int i = 9-top.size(); i >= 0; i--){
                top.add(i, blank);
            }
        }

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta)  book.getItemMeta();
        assert meta != null;
        meta.addPage(
                ""+
                        ChatColor.GOLD+ChatColor.BOLD+"Top Net Worth\n"
                        +ChatColor.BLACK+String.format("1\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(9).getFirst(), top.get(9).getSecond())
                        +ChatColor.BLACK+String.format("2\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(8).getFirst(), top.get(8).getSecond())
                        +ChatColor.BLACK+String.format("3\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(7).getFirst(), top.get(7).getSecond())
                        +ChatColor.BLACK+String.format("4\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(6).getFirst(), top.get(6).getSecond())
                        +ChatColor.BLACK+String.format("5\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(5).getFirst(), top.get(5).getSecond())
                        +ChatColor.BLACK+String.format("6\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(4).getFirst(), top.get(4).getSecond())
                        ,ChatColor.BLACK+String.format("7\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(3).getFirst(), top.get(3).getSecond())
                        +ChatColor.BLACK+String.format("8\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(2).getFirst(), top.get(2).getSecond())
                        +ChatColor.BLACK+String.format("9\u2022 "+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(1).getFirst(), top.get(1).getSecond())
                        +ChatColor.BLACK+String.format("10\u2022"+ChatColor.GOLD+"%s\n  "+ChatColor.GRAY+" %s Lectrum\n", top.get(0).getFirst(), top.get(0).getSecond())
        );
        meta.setAuthor("ParamaLegends");
        meta.setTitle("Leaderboards");
        book.setItemMeta(meta);
        return book;
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            player.openBook(createBook());
            return true;
        }
        return false;
    }


}
