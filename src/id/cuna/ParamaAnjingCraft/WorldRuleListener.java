package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class WorldRuleListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;

    public WorldRuleListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Disable silk touch enchantment
    @EventHandler
    public void enchantItem(EnchantItemEvent event){
        if(event.getEnchantsToAdd().toString().contains("silk_touch"))
            event.getEnchantsToAdd().remove(Enchantment.SILK_TOUCH);
    }

    //Disable breeding
    @EventHandler
    public void breedAnimal(EntityBreedEvent event){
        event.setCancelled(true);
    }

    //Disable nether portal teleport
    @EventHandler
    public void netherPortal(PlayerPortalEvent event){
        event.setCancelled(true);
    }

}
