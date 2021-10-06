package me.cuna.paramalegends.classgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.tinker.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class TinkerListener implements Listener{

    private final ParamaLegends plugin;
    public DataManager data;

    public SoulRing soulRing;
    public OverwhelmingBlink overwhelmingBlink;

    public TinkerListener(ParamaLegends plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        data = plugin.getData();

        soulRing = new SoulRing(plugin);
        overwhelmingBlink = new OverwhelmingBlink(plugin);

    }

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getItem() == null){
            return;
        }
        if(event.getAction() == Action.PHYSICAL){
            return;
        }
        //Check if held item is tinker spell
        ItemStack item = event.getItem();
        PlayerParama playerParama = plugin.getPlayerParama(player);
        if (item.getItemMeta() != null)
            switch (item.getItemMeta().getDisplayName()) {
                case ChatColor.COLOR_CHAR+"dSoul Ring" -> {
                    event.setCancelled(true);
                    if (playerParama.isNotSilenced())
                        soulRing.castSpell(playerParama);
                }
                case ChatColor.COLOR_CHAR+"dOverwhelming Blink" -> {
                    event.setCancelled(true);
                    if (playerParama.isNotSilenced())
                        overwhelmingBlink.castSpell(playerParama);
                }

            }
    }

    //Deal when player places illegal blocks
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.getItemInHand().hasItemMeta()){
            ItemStack placed = event.getItemInHand();
            switch(Objects.requireNonNull(placed.getItemMeta()).getDisplayName()){
                case ChatColor.COLOR_CHAR+"" -> event.setCancelled(true);
            }
        }
    }

}

