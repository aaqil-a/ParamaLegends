package me.cuna.ParamaLegends.Spells;

import me.cuna.ParamaLegends.PlayerParama;

public interface SpellParama {
    void castSpell(PlayerParama player);
    int getManaCost();
}
