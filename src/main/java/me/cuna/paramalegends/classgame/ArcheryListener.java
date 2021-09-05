package me.cuna.paramalegends.classgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.archery.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class ArcheryListener implements Listener{

    private final ParamaLegends plugin;
    public DataManager data;

    public final HunterEye hunterEye;
    public final ViperBite viperBite;
    public final TotsukaCreation totsukaCreation;
    public final WindBoost windBoost;
    public final Neurotoxin neurotoxin;
    public final Soulstring soulstring;
    public final HuayraFury huayraFury;
    public final Retreat retreat;
    public final Blast blast;
    public final RoyalArtillery royalArtillery;
    public final WhistlingWind whistlingWind;

    public ArcheryListener(ParamaLegends plugin) {
        this.plugin = plugin;
        data = plugin.getData();
        hunterEye = new HunterEye(plugin);
        viperBite = new ViperBite(plugin);
        totsukaCreation = new TotsukaCreation(plugin);
        windBoost = new WindBoost(plugin);
        neurotoxin = new Neurotoxin(plugin);
        soulstring = new Soulstring(plugin);
        huayraFury = new HuayraFury(plugin);
        retreat = new Retreat(plugin);
        blast = new Blast(plugin);
        royalArtillery = new RoyalArtillery(plugin);
        whistlingWind = new WhistlingWind(plugin);
    }

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        PlayerParama player = plugin.getPlayerParama(event.getPlayer());
        if(event.getItem() == null){
            return;
        }
        if(event.getAction() == Action.PHYSICAL){
            return;
        }

        ItemStack item = event.getItem();
        if(item.getItemMeta() != null){
            switch(item.getItemMeta().getDisplayName()){
                case ChatColor.COLOR_CHAR+"aTotsuka's Creation" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel( 3, ClassGameType.ARCHERY))
                            totsukaCreation.castSpell(player);
                }
                case ChatColor.COLOR_CHAR+"aWind Boost" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel( 4, ClassGameType.ARCHERY))
                            windBoost.castSpell(player);
                }
                case ChatColor.COLOR_CHAR+"aSoulstring" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel( 6, ClassGameType.ARCHERY))
                            soulstring.castSpell(player);
                }
                case ChatColor.COLOR_CHAR+"aRoyal Artillery" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel( 9, ClassGameType.ARCHERY))
                            royalArtillery.castSpell(player);
                }
                case ChatColor.COLOR_CHAR+"aHuayra's Fury"-> {
                    if (player.isNotSilenced())
                        if(player.checkLevel( 7, ClassGameType.ARCHERY))
                            huayraFury.castSpell(player);
                }
            }
        }

    }

    //When player shoots bow
    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event){
        if(event.getEntity() instanceof Player){
            ItemStack arrowItem = event.getConsumable();
            assert arrowItem != null;
            ItemMeta arrowMeta = arrowItem.getItemMeta();
            Player player = (Player) event.getEntity();
            if(arrowMeta != null){
                shootArrow(plugin.getPlayerParama(player), event, arrowMeta.getDisplayName());
            }
            if(event.getProjectile() instanceof AbstractArrow){
                if(player.hasMetadata("WINDBOOSTPARAMA")){
                    double damage = ((AbstractArrow) event.getProjectile()).getDamage();
                    ((AbstractArrow) event.getProjectile()).setDamage(damage*1.15);
                    ((AbstractArrow) event.getProjectile()).setKnockbackStrength(2);
                }
                if(player.hasMetadata("HUAYRAFURY") && event.getProjectile() instanceof Arrow){
                    Arrow arrow = (Arrow) event.getProjectile();
                    if(arrow.isCritical()){
                        //play fireowrk sound
                        arrow.getWorld().playSound(arrow.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.8f, 1.5f );
                        //set arrow proprties
                        arrow.setGlowing(true);
                        arrow.setPierceLevel(127);
                        arrow.setGravity(false);
                        arrow.setVelocity(arrow.getVelocity().multiply(1.5));
                        arrow.setMetadata("HUAYRAARROW", new FixedMetadataValue(plugin, true));
                        Bukkit.getScheduler().runTaskLater(plugin, arrow::remove, 105);
                    }
                }
            }
        }
    }


    public void shootArrow(PlayerParama playerParama, EntityShootBowEvent event, String name){
        switch(name){
            case ChatColor.COLOR_CHAR+"aHunter's Eye" -> {
                if(playerParama.isNotSilenced()){
                    hunterEye.shootArrow(playerParama, event.getProjectile());
                }
            }
            case ChatColor.COLOR_CHAR+"aViper's Bite" -> {
                if(playerParama.isNotSilenced()){
                    if(playerParama.checkLevel( 2, ClassGameType.ARCHERY)){
                        viperBite.shootArrow(playerParama, event.getProjectile());
                    }
                }
            }
            case ChatColor.COLOR_CHAR+"aNeurotoxin" -> {
                if(playerParama.isNotSilenced()){
                    if(playerParama.checkLevel( 5, ClassGameType.ARCHERY)){
                        neurotoxin.shootArrow(playerParama, event.getProjectile());
                    }
                }
            }
            case ChatColor.COLOR_CHAR+"aRetreat" -> {
                if(playerParama.isNotSilenced()){
                    if(playerParama.checkLevel( 7, ClassGameType.ARCHERY)){
                        retreat.shootArrow(playerParama, event.getProjectile());
                    }
                }
            }
            case ChatColor.COLOR_CHAR+"aBlast" -> {
                if(playerParama.isNotSilenced()){
                    if(playerParama.checkLevel( 8, ClassGameType.ARCHERY)){
                        blast.shootArrow(playerParama, event.getProjectile());
                    }
                }
            }
            case ChatColor.COLOR_CHAR+"aWhistling Wind" -> {
                if(event.getProjectile() instanceof SpectralArrow){
                    ((SpectralArrow) event.getProjectile()).setGlowingTicks(0);
                }
                Player player = playerParama.getPlayer();
                if(playerParama.isNotSilenced()){
                    if(playerParama.checkLevel(10, ClassGameType.ARCHERY)){
                        if(whistlingWind.getEntitiesWhistlingWind().containsKey(player)){
                            ((SpectralArrow) event.getProjectile()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                            player.sendMessage(ChatColor.GRAY+"A whistling wind is already in use.");
                            whistlingWind.givePlayerWhistlingWind(player, event.getConsumable());
                        } else {
                            whistlingWind.castWhistlingWind(playerParama, event);
                        }
                    } else {
                        ((SpectralArrow) event.getProjectile()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                        whistlingWind.givePlayerWhistlingWind(player, event.getConsumable());
                    }
                } else {
                    ((SpectralArrow) event.getProjectile()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                    whistlingWind.givePlayerWhistlingWind(player, event.getConsumable());
                }
            }
        }
    }


    //Deal when player places illegal blocks
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.getItemInHand().hasItemMeta()){
            ItemStack placed = event.getItemInHand();
            switch(Objects.requireNonNull(placed.getItemMeta()).getDisplayName()){
                case ChatColor.COLOR_CHAR+"aTotsuka's Creation", ChatColor.COLOR_CHAR+"aSoulstring", ChatColor.COLOR_CHAR+"aHuayra's Fury" -> event.setCancelled(true);
            }
        }
    }

    public void findAir(Location location){
         if(location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
             location.add(0,1,0);
        } else if(location.getBlock().getRelative(BlockFace.DOWN).getType().isAir()){
            location.add(0,-1,0);
        } else if(location.getBlock().getRelative(BlockFace.NORTH).getType().isAir()){
            location.add(0,0,-1);
        } else if(location.getBlock().getRelative(BlockFace.SOUTH).getType().isAir()){
            location.add(0,0,1);
        } else if(location.getBlock().getRelative(BlockFace.WEST).getType().isAir()){
             location.add(-1,0,0);
        } else if(location.getBlock().getRelative(BlockFace.EAST).getType().isAir()){
           location.add(1,0,0);
        }
    }
}

