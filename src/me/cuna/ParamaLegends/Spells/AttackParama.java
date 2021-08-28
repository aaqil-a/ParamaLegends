package me.cuna.ParamaLegends.Spells;

import me.cuna.ParamaLegends.PlayerParama;
import org.bukkit.entity.Entity;

public interface AttackParama {
    void attackEntity(PlayerParama player, Entity entity, double damage);
}
