package me.cuna.paramalegends.spell.archery;

import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class HuayraFury implements Listener, SpellParama {

    private final ParamaLegends plugin;
    private final int manaCost = 150;

    public HuayraFury(ParamaLegends plugin){
        this.plugin = plugin;
    }

    public void castSpell(PlayerParama playerParama){
        if (playerParama.checkCooldown(this)){
            plugin.sendCooldownMessage(playerParama, "Huayra's Fury");
        } else if(playerParama.subtractMana(manaCost)){
            Bukkit.broadcastMessage("Huayra's Fury!");
        }
    }

    public int getManaCost(){
        return manaCost;
    }

}
