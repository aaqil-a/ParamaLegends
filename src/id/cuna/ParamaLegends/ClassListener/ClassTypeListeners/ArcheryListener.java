package id.cuna.ParamaLegends.ClassListener.ClassTypeListeners;

import id.cuna.ParamaLegends.ClassListener.ClassListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.Spells.Archery.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ArcheryListener extends ClassListener implements Listener{

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

    private final List<Entity> entitiesPoisoned = new ArrayList<>();

    public ArcheryListener(ParamaLegends plugin) {
        super(plugin, ClassType.ARCHERY);
        this.plugin = plugin;
        data = plugin.getData();

        hunterEye = new HunterEye(plugin, this);
        viperBite = new ViperBite(plugin, this);
        totsukaCreation = new TotsukaCreation(plugin, this);
        windBoost = new WindBoost(plugin, this);
        neurotoxin = new Neurotoxin(plugin, this);
        soulstring = new Soulstring(plugin, this);
        huayraFury = new HuayraFury(plugin, this);
        retreat = new Retreat(plugin, this);
        blast = new Blast(plugin, this);
        royalArtillery = new RoyalArtillery(plugin, this);
        whistlingWind = new WhistlingWind(plugin, this);
    }

    //Get player's archery level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        int classLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".archery");
        super.getPlayerLevel().put(player, classLevel);
        if(classLevel >= 4){
            applySpeedPassive(player);
        }
    }

    //Change player's level in playerArcheryLevle hashmap when leveling up
    @Override
    public void levelUp(Player player){
        int curLevel = super.getPlayerLevel().get(player);
        super.getPlayerLevel().replace(player, curLevel+1);
        if(curLevel >= 4){
            applySpeedPassive(player);
        }
    }

    public void applySpeedPassive(Player player){
        double movementSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(movementSpeed*1.2);
    }

    public void shootArrow(Player player, EntityShootBowEvent event, String name, boolean noMana){
        switch(name){
            case "§aHunter's Eye" -> {
                if(!plugin.isSilenced(player)){
                    hunterEye.castHuntersEye(player, event.getProjectile(), noMana);
                }
            }
            case "§aViper's Bite" -> {
                if(!plugin.isSilenced(player)){
                    if(checkLevel(player, 2)){
                        viperBite.castViperBite(player, event.getProjectile(), noMana);
                    }
                }
            }
            case "§aNeurotoxin" -> {
                if(!plugin.isSilenced(player)){
                    if(checkLevel(player, 5)){
                        neurotoxin.castNeurotoxin(player, event.getProjectile(), noMana);
                    }
                }
            }
            case "§aRetreat" -> {
                if(!plugin.isSilenced(player)){
                    if(checkLevel(player, 7)){
                        retreat.castRetreat(player, event.getProjectile(), noMana);
                    }
                }
            }
            case "§aBlast" -> {
                if(!plugin.isSilenced(player)){
                    if(checkLevel(player, 8)){
                        blast.castBlast(player, event.getProjectile(), noMana);
                    }
                }
            }
            case "§aWhistling Wind" -> {
                if(event.getProjectile() instanceof SpectralArrow){
                    ((SpectralArrow) event.getProjectile()).setGlowingTicks(0);
                }
                if(!plugin.isSilenced(player)){
                    if(checkLevel(player, 10)){
                        if(whistlingWind.getEntitiesWhistlingWind().containsKey(player)){
                            ((SpectralArrow) event.getProjectile()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                            player.sendMessage(ChatColor.GRAY+"A whistling wind is already in use.");
                            whistlingWind.givePlayerWhistlingWind(player, event.getConsumable());
                        } else {
                            whistlingWind.castWhistlingWind(player, event, noMana);
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

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        //Check if held item is book

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if(item.getItemMeta() != null){
            switch(item.getItemMeta().getDisplayName()){
                case "§aTotsuka's Creation" -> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 3))
                            totsukaCreation.castTotsuka(player);
                }
                case "§aWind Boost" -> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 4))
                            windBoost.castWindBoost(player);
                }
                case "§aSoulstring" -> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 6))
                            soulstring.castSoulstring(player);
                }
                case "§aRoyal Artillery" -> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 9))
                            royalArtillery.castRoyalArtillery(player);
                }
                case "§aHuayra's Fury"-> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 7))
                            huayraFury.castHuayraFury(player);
                }
            }
        }

    }

    //When player shoots bow
    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event){
        if(event.getEntity() instanceof Player){
            ItemStack arrowItem = event.getConsumable();
            ItemMeta arrowMeta = arrowItem.getItemMeta();
            Player player = (Player) event.getEntity();
            if(arrowMeta != null){
                shootArrow(player, event, arrowMeta.getDisplayName(), false);
            }
            if(event.getProjectile() instanceof AbstractArrow){
                if(windBoost.getPlayersWindBoosted().contains(player.getUniqueId().toString())){
                    double damage = ((AbstractArrow) event.getProjectile()).getDamage();
                    ((AbstractArrow) event.getProjectile()).setDamage(damage*1.15);
                    ((AbstractArrow) event.getProjectile()).setKnockbackStrength(1);
                }
            }
        }
    }

    //Deal when player places illegal blocks
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        if(event.getItemInHand().hasItemMeta()){
            ItemStack placed = event.getItemInHand();
            switch(placed.getItemMeta().getDisplayName()){
                case "§aTotsuka's Creation", "§aSoulstring", "§aHuayra's Fury" -> event.setCancelled(true);
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

    public List<Entity> getEntitiesPoisoned(){
        return entitiesPoisoned;
    }
    public List<String> getPlayersWindBoosted(){
        return windBoost.getPlayersWindBoosted();
    }
    public List<Entity> getEntitiesHunterEye(){
        return hunterEye.getEntitiesHunterEye();
    }

    public HunterEye getHunterEye(){
        return hunterEye;
    }
    public ViperBite getViperBite(){
        return viperBite;
    }
    public Neurotoxin getNeurotoxin() {
        return neurotoxin;
    }
    public Retreat getRetreat() {
        return retreat;
    }
    public Blast getBlast() {
        return blast;
    }
}

