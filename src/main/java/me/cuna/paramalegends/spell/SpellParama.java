package me.cuna.paramalegends.spell;

import me.cuna.paramalegends.PlayerParama;

public interface SpellParama {
    void castSpell(PlayerParama player);
    int getManaCost();
    int getCooldown();
}
