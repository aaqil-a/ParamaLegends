package id.cuna.ParamaLegends.Spells.Reaper;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class SecondWind {

    private final ParamaLegends plugin;

    private final List<String> playerCooldowns = new ArrayList<>();

    public SecondWind(ParamaLegends plugin, ReaperListener reaperListener){
        this.plugin = plugin;
    }

    public void castSecondWind (Player player, Entity entity){
        if (!playerCooldowns.contains(player.getUniqueId().toString())){
            if (entity instanceof LivingEntity){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 62, 2));
                player.sendMessage(ChatColor.GREEN+"You gain a second wind.");
                playerCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerCooldowns.remove(player.getUniqueId().toString());
                }, 162);
            }
        }
    }

}
