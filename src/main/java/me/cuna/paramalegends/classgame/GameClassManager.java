package me.cuna.paramalegends.classgame;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public class GameClassManager {

    public ParamaLegends plugin;
    public ArcheryListener archery;
    public MagicListener magic;
    public ReaperListener reaper;
    public SwordsmanListener swordsman;

    public GameClassManager(ParamaLegends plugin){
        this.plugin = plugin;
        archery = new ArcheryListener(plugin);
        magic = new MagicListener(plugin);
        reaper = new ReaperListener(plugin);
        swordsman = new SwordsmanListener(plugin);

        registerListeners();
        registerSpells();
    }
    
    public void registerListeners(){
        plugin.getServer().getPluginManager().registerEvents(archery, plugin);
        plugin.getServer().getPluginManager().registerEvents(magic, plugin);
        plugin.getServer().getPluginManager().registerEvents(reaper, plugin);
        plugin.getServer().getPluginManager().registerEvents(swordsman, plugin);
    }

    public void registerSpells(){
        plugin.getServer().getPluginManager().registerEvents(archery.blast, plugin);
        plugin.getServer().getPluginManager().registerEvents(archery.neurotoxin, plugin);
        plugin.getServer().getPluginManager().registerEvents(archery.retreat, plugin);
        plugin.getServer().getPluginManager().registerEvents(archery.royalArtillery, plugin);
        plugin.getServer().getPluginManager().registerEvents(archery.totsukaCreation, plugin);
        plugin.getServer().getPluginManager().registerEvents(archery.whistlingWind, plugin);

        plugin.getServer().getPluginManager().registerEvents(magic.flingEarth, plugin);
        plugin.getServer().getPluginManager().registerEvents(magic.illusoryOrb, plugin);
        plugin.getServer().getPluginManager().registerEvents(magic.voicesOfTheDamned, plugin);

        plugin.getServer().getPluginManager().registerEvents(reaper.blindingSand, plugin);

        plugin.getServer().getPluginManager().registerEvents(swordsman.shieldsUp, plugin);
        plugin.getServer().getPluginManager().registerEvents(swordsman.superconducted, plugin);
        plugin.getServer().getPluginManager().registerEvents(swordsman.terrifyingCruelty, plugin);
    }


    public void sendCooldownMessage(PlayerParama player, String spell){
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_PURPLE + spell + ChatColor.GRAY + " is on cooldown."));
    }
    public void sendNoLongerCooldownMessage(PlayerParama player, String spell){
        player.getPlayer().sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }
    public void sendOutOfRangeMessage(PlayerParama player){
        player.getPlayer().sendMessage(ChatColor.GRAY + "Out of range to cast spell.");
    }

}
