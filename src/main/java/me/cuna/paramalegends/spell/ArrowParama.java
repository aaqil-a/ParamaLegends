package me.cuna.paramalegends.spell;

import me.cuna.paramalegends.PlayerParama;
import org.bukkit.entity.Entity;

public interface ArrowParama {
    void shootArrow(PlayerParama player, Entity arrow);
    int getManaCost();
}
