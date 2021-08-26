package id.cuna.ParamaLegends.Spells;

import id.cuna.ParamaLegends.PlayerParama;
import org.bukkit.entity.Entity;

public interface ArrowParama {
    void shootArrow(PlayerParama player, Entity arrow);
    int getManaCost();
}
