package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.PlayerParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
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
