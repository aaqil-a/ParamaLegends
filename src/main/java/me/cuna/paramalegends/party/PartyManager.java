package me.cuna.paramalegends.party;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;

public class PartyManager {

    private final ParamaLegends plugin;
    public DataManager data;

    public PartyManager(ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public void createParty(PlayerParama playerParama){
        Party party = new Party(this);
        playerParama.setParty(party);
    }

    public ParamaLegends getPlugin(){
        return plugin;
    }
}
