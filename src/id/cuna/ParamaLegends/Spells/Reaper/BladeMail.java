package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class BladeMail {

    private final ParamaLegends plugin;

    private final List<String> playerCooldowns = new ArrayList<>();

    public BladeMail(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
    }

    public void castBladeMail(Player player, Entity entity, double damage){
        if (!playerCooldowns.contains(player.getUniqueId().toString())){
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7f, 1.2f);
            if (entity instanceof LivingEntity){
                damage = damage * 0.1;
                ((LivingEntity) entity).damage(damage + 0.34, player);
            }
            playerCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                playerCooldowns.remove(player.getUniqueId().toString());
            }, 82);
        }
    }

}
