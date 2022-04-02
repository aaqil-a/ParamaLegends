package me.cuna.paramalegends.leaderboard;

import com.mojang.datafixers.util.Pair;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.leaderboard.LeaderboardCriteria;
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


public class LeaderboardCommand implements CommandExecutor {

    private final ParamaLegends plugin;
    private final List<LeaderboardCriteria> criteria = new ArrayList<>();
    private final Pair<String, Integer> blank = new Pair<>("---", 0);
    public DataManager data;

    public LeaderboardCommand(final ParamaLegends plugin, LeaderboardManager manager){
        this.plugin = plugin;
        data = plugin.dataManager;

        criteria.add(manager.netWorthCriteria);
        criteria.add(manager.playTimeCriteria);
    }

    public ItemStack createBook(){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta)  book.getItemMeta();
        meta.setAuthor("ParamaLegends");
        meta.setTitle("Leaderboards");

        criteria.forEach(criteria -> {
            List<Pair<String, Integer>> top = criteria.getLeaderboard().stream().toList();
            String name = criteria.getName();
            String criterion = criteria.getCriterion();


            StringBuilder firstPage = new StringBuilder("" + ChatColor.GOLD + ChatColor.BOLD + String.format("Top %s \n", name));
            for(int i = 0; i <= Math.min(top.size()-1, 5); i++){
                firstPage.append(ChatColor.BLACK).append(String.format("%d\u2022 " + ChatColor.GOLD + "%s\n  " + ChatColor.GRAY + " %d %s\n", i+1,top.get(i).getFirst(), top.get(i).getSecond(), criterion));
            }

            StringBuilder secondPage = new StringBuilder();
            for(int i = 6; i <= Math.min(top.size()-1, 9); i++){
                secondPage.append(ChatColor.BLACK).append(String.format("%d\u2022 " + ChatColor.GOLD + "%s\n  " + ChatColor.GRAY + " %d %s\n", i+1,top.get(i).getFirst(), top.get(i).getSecond(), criterion));
            }

            meta.addPage(
                firstPage.toString(),
                        secondPage.toString()
            );
        });

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
