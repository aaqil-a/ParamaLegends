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
        data = plugin.dataManager;
        whatsNewBook = createBook();
    }

    public ItemStack createBook(){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta)  book.getItemMeta();
        assert meta != null;
        meta.addPage(
                ""+
                        ChatColor.GOLD+ChatColor.BOLD+"1.2.2 - 1.2.? 18/11/21\n"
                        +ChatColor.BLACK+"\u2022 Added a bunch\n  of shit I'll\n  sort it out \n  later.\n"
                        +ChatColor.BLACK+"\u2022 Added /leaderboard \n  command.",
                ""+
                        ChatColor.GOLD+ChatColor.BOLD+"1.2.1 19/9/21\n"
                        +ChatColor.BLACK+"\u2022 Inreased duration\n  of Calamity\n"
                        +ChatColor.BLACK+"\u2022 Increased duration\n  of Terrifying\n   Cruelty.\n"
                        +ChatColor.BLACK+"\u2022 Inreased cooldown\n  of Terrifying\n   Cruelty.\n",
                ""+
                        ChatColor.GOLD+ChatColor.BOLD+"1.2.0 10/9/21\n"
                        +ChatColor.BLACK+"\u2022 Added Nature Boss\n  Fight.\n"
                        +ChatColor.BLACK+"\u2022 Added King Slime.\n"
                        +ChatColor.BLACK+"\u2022 Added mana potion.\n"
                        +ChatColor.BLACK+"\u2022 Increased magic\n  mastery damage\n  bonus.\n"
                        +ChatColor.BLACK+"\u2022 Prowl now increases\n  attack speed.\n",
                ""+
                        ChatColor.GOLD+ChatColor.BOLD+"1.1.8 6/9/21\n"
                        +ChatColor.BLACK+"\u2022 Added magic\n  mastery system.\n"
                        +ChatColor.BLACK+"\u2022 Dash Strike\n  now affects\n  players.\n"
                        +ChatColor.BLACK+"\u2022 Added Huayra's\n  Fury sound.\n"
                        +ChatColor.BLACK+"\u2022 Illusory Orb\n  speed increased.\n"
                        +ChatColor.BLACK+"\u2022 Swordsman\n  spell items now\n  enchanted.",
                ""+
                    ChatColor.GOLD+ChatColor.BOLD+"1.1.7 5/9/21\n"
                    +ChatColor.BLACK+"\u2022 Added Prowl,\n  Rejuvenate and\n  Dash Strike\n  reaper spells.\n"
                    +ChatColor.BLACK+"\u2022 Fixed Illusory\n  Orb Sound.\n",
                ""+
                    ChatColor.GOLD+ChatColor.BOLD+"1.1.6.1 4/9/21\n"
                    +ChatColor.BLACK+"\u2022 Added Custom Food\n  and recipe.\n"
                    +ChatColor.BLACK+"\u2022 For the recipe,\n  you must learn it in\n  aenzt cooking\n  school.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.6 3/9/21\n"
                    +ChatColor.BLACK+"\u2022 Added /whatsnew\n  command.\n"
                    +ChatColor.BLACK+"\u2022 Buffed illagers.\n"
                    +ChatColor.BLACK+"\u2022 Reduced custom\n  arrow prices.\n"
                    +ChatColor.BLACK+"\u2022 Reduced custom\n  arrow mana costs.\n"
                    +ChatColor.BLACK+"\u2022 Dragon's Breath can\n  now be cancelled.\n"
                    +ChatColor.BLACK+"\u2022 Dragon's Breath now\n  affects players.\n"
                    +ChatColor.BLACK+"\u2022 Fixed bug where\n  reapers can 'revive'.\n"
                    +ChatColor.BLACK+"\u2022 Added Custom Food and recipe.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.5 2/9/21\n"
                        +ChatColor.BLACK+"\u2022 Reworked Dragon's\n  Breath.\n"
                        +ChatColor.BLACK+"\u2022 Shop signs now\n  display show owner\n  name.\n"
                        +ChatColor.BLACK+"\u2022 Reaper level 10\n  now increases\n  reaper passive\n  chance.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.4 30/8/21\n"
                        +ChatColor.BLACK+"\u2022 Reworked Huayra's\n  Fury.\n"
                        +ChatColor.BLACK+"\u2022 Added 'Why are you\n  hitting yourself?'\n  custom death\n  message.\n"
                        +ChatColor.BLACK+"\u2022 Gut Punch now has\n  minimum (15) and\n  maximum (50) damage.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  Gut Punch kills don't\n  grant rewards.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.3 28/8/21\n"
                        +ChatColor.BLACK+"\u2022 Added player shop\n  system.\n"
                        +ChatColor.BLACK+"\u2022 Retreat second\n  arrow now fires with\n  same velocity as\n  first arrow.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.2 24/8/21\n"
                        +ChatColor.BLACK+"\u2022 Life Drain now has\n  maximum range.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  Void Raid boss bar\n  is still visible\n  after raid.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.1 23/8/21\n"
                        +ChatColor.BLACK+"\u2022 Blinding Sand now\n  affects players.\n"
                        +ChatColor.BLACK+"\u2022 Summon Lightning\n  now affects players.\n"
                        +ChatColor.BLACK+"\u2022 Increased alcohol\n  effect duration.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.1.0 23/8/21\n"
                        +ChatColor.BLACK+"\u2022 Added alcohol\n  winery and license.\n"
                        +ChatColor.BLACK+"\u2022 Added several\n  alcoholic beverages.\n"
                        +ChatColor.BLACK+"\u2022 Gust range\n  increased.\n"
                        +ChatColor.BLACK+"\u2022 /destiny command\n  now displays GUI.\n"
                        +ChatColor.BLACK+"\u2022 (Finally) added\n  reaper class to\n  destiny GUI.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.7 21/8/21\n"
                        +ChatColor.BLACK+"\u2022 Shields Up\n  resistance\n  decreased.\n"
                        +ChatColor.BLACK+"\u2022 Summon Lightning\n  cast range\n  increased.\n"
                        +ChatColor.BLACK+"\u2022 Life Drain can\n  now be casted on\n  other players.\n"
                        +ChatColor.BLACK+"\u2022 Blinding Sand\n  now works as an\n  AoE ability.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.6 20/8/21\n"
                        +ChatColor.BLACK+"\u2022 Summon Lightning\n  damage increased.\n"
                        +ChatColor.BLACK+"\u2022 Spells and arrows\n  no longer show\n  in shop if player\n  has not fulfilled\n  its prerequisites.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  Blinding Sand\n  does not make\n  arrows miss.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.5 18/8/21\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  non-swordsman\n  abilities can crit.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  non-reaper\n  abilities can cause\n  bleed.\n",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.4 17/8/21\n"
                        +ChatColor.BLACK+"\u2022 Disabled natural\n  health regeneration\n  in the nether.\n\n"
                +ChatColor.GOLD+ChatColor.BOLD+"1.0.3 17/8/21\n"
                        +ChatColor.BLACK+"\u2022 Wind Boost passive\n  speed reduced.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.2 16/8/21\n"
                        +ChatColor.BLACK+"\u2022 Players now lose\n  lectrum on death.\n"
                        +ChatColor.BLACK+"\u2022 Ignite and Life\n  Drain cast size\n  increased.\n"
                        +ChatColor.BLACK+"\u2022 Fixed bug where\n  temples may spawn\n  on trees.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.1 16/8/21\n"
                        +ChatColor.BLACK+"\u2022 Altar now shows\n  recipes for\n  summoning items.\n"
                        +ChatColor.BLACK+"\u2022 Added expanse\n  feature to expand\n  the safe zone.\n"
                        +ChatColor.BLACK+"\u2022 EXP required\n  to level up\n  swordsmanship\n  increased.",
                ""+ChatColor.GOLD+ChatColor.BOLD+"1.0.0 10/8/21\n"
                        +ChatColor.BLACK+"\u2022 Created\n  ParamaLegends.\n"
                        +ChatColor.BLACK+"\u2022 Thanks for\n  playing my game!\n  "+ChatColor.GOLD+"-cuna\n  -aenzt\n  -Hachuwu"
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
