package id.cuna.ParamaLegends.Spells;

import id.cuna.ParamaLegends.PlayerParama;

public interface SpellParama {
    void castSpell(PlayerParama player);
    int getManaCost();
}
