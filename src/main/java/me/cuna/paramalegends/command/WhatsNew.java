package me.cuna.paramalegends.command;

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


public class WhatsNew implements CommandExecutor {

    private final ParamaLegends plugin;
    private final ItemStack whatsNewBook;
    public DataManager data;

    public WhatsNew(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        whatsNewBook = createBook();
    }

    public ItemStack createBook(){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta)  book.getItemMeta();
        assert meta != null;
        meta.addPage(""+
                ChatColor.GOLD+ChatColor.BOLD+"1.1.6\n"
                    +ChatColor.BLACK+"\u2022 Added /whatsnew\n  command.\n"
                    +ChatColor.BLACK+"\u2022 Buffed illagers.\n"
                    +ChatColor.BLACK+"\u2022 Reduced custom\n  arrow prices.\n"
                    +ChatColor.BLACK+"\u2022 Reduced custom\n  arrow mana costs.\n"
                    +ChatColor.BLACK+"\u2022 Dragon's Breath can\n  now be cancelled.\n"
                    +ChatColor.BLACK+"\u2022 Dragon's Breath now\n  affects players.\n"
                    +ChatColor.BLACK+"\u2022 Fixed bug where\n  reapers can 'revive'.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.5\n"
                        +ChatColor.BLACK+"\u2022 Reworked Dragon's\n  Breath.\n"
                        +ChatColor.BLACK+"\u2022 Shop signs now\n  display show owner\n  name.\n"
                        +ChatColor.BLACK+"\u2022 Reaper level 10\n  now increases\n  reaper passive\n  chance.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.4\n"
                        +ChatColor.BLACK+"\u2022 Reworked Huayra's\n  Fury.\n"
                        +ChatColor.BLACK+"\u2022 Added 'Why are you\n  hitting yourself?'\n  custom death\n  message.\n"
                        +ChatColor.BLACK+"\u2022 Gut Punch now has\n  minimum (15) and\n  maximum (50) damage.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  Gut Punch kills don't\n  grant rewards.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.3\n"
                        +ChatColor.BLACK+"\u2022 Added player shop\n  system.\n"
                        +ChatColor.BLACK+"\u2022 Retreat second\n  arrow now fires with\n  same velocity as\n  first arrow.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.2\n"
                        +ChatColor.BLACK+"\u2022 Life Drain now has\n  maximum range.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  Void Raid boss bar\n  is still visible\n  after raid.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.1\n"
                        +ChatColor.BLACK+"\u2022 Blinding Sand now\n  affects players.\n"
                        +ChatColor.BLACK+"\u2022 Summon Lightning\n  now affects players.\n"
                        +ChatColor.BLACK+"\u2022 Increased alcohol\n  effect duration.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.0\n"
                        +ChatColor.BLACK+"\u2022 Added alcohol\n  winery and license.\n"
                        +ChatColor.BLACK+"\u2022 Added several\n  alcoholic beverages.\n"
                        +ChatColor.BLACK+"\u2022 Gust range\n  increased.\n"
                        +ChatColor.BLACK+"\u2022 /destiny command\n  now displays GUI.\n"
                        +ChatColor.BLACK+"\u2022 (Finally) added\n  reaper class to\n  destiny GUI.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.7\n"
                        +ChatColor.BLACK+"\u2022 Shields Up\n  resistance\n  decreased.\n"
                        +ChatColor.BLACK+"\u2022 Summon Lightning\n  cast range\n  increased.\n"
                        +ChatColor.BLACK+"\u2022 Life Drain can\n  now be casted on\n  other players.\n"
                        +ChatColor.BLACK+"\u2022 Blinding Sand\n  now works as an\n  AoE ability.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.6\n"
                        +ChatColor.BLACK+"\u2022 Summon Lightning\n  damage increased.\n"
                        +ChatColor.BLACK+"\u2022 Spells and arrows\n  no longer show\n  in shop if player\n  has not fulfilled\n  its prerequisites.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  Blinding Sand\n  does not make\n  arrows miss.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.5\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  non-swordsman\n  abilities can crit.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  non-reaper\n  abilities can cause\n  bleed.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.4\n"
                        +ChatColor.BLACK+"\u2022 Disabled natural\n  health regeneration\n  in the nether.\n\n"
                +ChatColor.GOLD+ChatColor.BOLD+"1.0.3\n"
                        +ChatColor.BLACK+"\u2022 Wind Boost passive\n  speed reduced.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.2\n"
                        +ChatColor.BLACK+"\u2022 Players now lose\n  lectrum on death.\n"
                        +ChatColor.BLACK+"\u2022 Ignite and Life\n  Drain cast size\n  increased.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  temples may spawn\n  on trees.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.1\n"
                        +ChatColor.BLACK+"\u2022 Altar now shows\n  recipes for\n  summoning items.\n"
                        +ChatColor.BLACK+"\u2022 Added expanse\n  feature to expand\n  the safe zone.\n"
                        +ChatColor.BLACK+"\u2022 EXP required\n  to level up\n  swordsmanship\n  increased.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.0\n"
                        +ChatColor.BLACK+"\u2022 Created\n  ParamaLegends.\n"
                        +ChatColor.BLACK+"\u2022 Thanks for\n  playing my game!\n  "+ChatColor.GOLD+"-cuna\n  -aenzt"

        );
        meta.setAuthor("ParamaLegends");
        meta.setTitle("Whats New!");
        book.setItemMeta(meta);
        return book;
    }

    // Command to Start Game
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            player.openBook(whatsNewBook);
            return true;
        }
        return false;
    }


}
