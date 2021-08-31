package me.cuna.paramalegends.spell;

import me.cuna.paramalegends.PlayerParama;
import org.bukkit.entity.Entity;

public interface AttackParama {
    void attackEntity(PlayerParama player, Entity entity, double damage);
}
