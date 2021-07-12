package id.cuna.ParamaLegends.Spells.Archery;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ArcheryListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Retreat {

    private final ParamaLegends plugin;
    private final ArcheryListener archeryListener;

    private final List<String> playersRetreatBoosted = new ArrayList<>();

    public Retreat(ParamaLegends plugin, ArcheryListener archeryListener){
        this.plugin = plugin;
        this.archeryListener = archeryListener;
    }

    public void castRetreat(Player player, Entity entity, boolean noMana){
        int manaCost = 70;
        if(noMana){
            manaCost = 0;
        }
        if(entity instanceof Arrow){
            if(archeryListener.subtractMana(player, manaCost)){
                if(!playersRetreatBoosted.contains(player.getUniqueId().toString())) {
                    double oldSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();;
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        if(!playersRetreatBoosted.contains(player.getUniqueId().toString())){
                            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(oldSpeed*1.1);
                            playersRetreatBoosted.add(player.getUniqueId().toString());
                        }
                    }, 5);
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(oldSpeed);
                        playersRetreatBoosted.remove(player.getUniqueId().toString());
                    }, 65);
                }

                Bukkit.getScheduler().runTaskLater(plugin, ()->{
                    Vector arrowVelocity = player.getLocation().getDirection();
                    Arrow arrow2 = player.launchProjectile(Arrow.class, arrowVelocity);
                    arrow2.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                }, 5);
                Vector velocity = player.getLocation().getDirection().setY(0).normalize();
                player.setVelocity(velocity.multiply(-0.8).setY(0.3));
            }
        }
    }

}
