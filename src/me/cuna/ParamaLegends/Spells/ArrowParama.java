package me.cuna.ParamaLegends.Spells;

import me.cuna.ParamaLegends.PlayerParama;
import org.bukkit.entity.Entity;

public interface ArrowParama {
    void shootArrow(PlayerParama player, Entity arrow);
    int getManaCost();
}
