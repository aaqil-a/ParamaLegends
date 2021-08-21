package id.cuna.ParamaLegends.ClassListener.ClassTypeListener;

import id.cuna.ParamaLegends.ClassListener.ClassListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.Spells.Reaper.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ReaperListener extends ClassListener implements Listener{

    private final ParamaLegends plugin;
    public DataManager data;

    public final CoatedBlade coatedBlade;
    public final HiddenStrike hiddenStrike;
    public final BlindingSand blindingSand;
    public final BladeMail bladeMail;
    public final SecondWind secondWind;
    public final BloodyFervour bloodyFervour;
    public final GutPunch gutPunch;
    public final ForbiddenSlash forbiddenSlash;
    public final MementoMori mementoMori;

    public ReaperListener(ParamaLegends plugin) {
        super(plugin, ClassType.REAPER);
        this.plugin = plugin;
        data = plugin.getData();

        coatedBlade = new CoatedBlade(plugin, this);
        hiddenStrike = new HiddenStrike(plugin, this);
        bladeMail = new BladeMail(plugin, this);
        secondWind = new SecondWind(plugin, this);
        blindingSand = new BlindingSand(plugin, this);
        bloodyFervour = new BloodyFervour(plugin, this);
        gutPunch = new GutPunch(plugin, this);
        forbiddenSlash = new ForbiddenSlash(plugin, this);
        mementoMori = new MementoMori(plugin, this);
    }

    //Passive listeners
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        double damage = event.getDamage();
        if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity){
            Player defender = (Player) event.getEntity();
            LivingEntity attacker = (LivingEntity) event.getDamager();
            //blade mail
            if (checkLevel(defender, 4, true)) {
                Random rand = new Random();
                int bladeMailRandom = rand.nextInt(5);
                if (bladeMailRandom == 1) {
                    bladeMail.castBladeMail(defender, attacker, damage);
                }
            }
            if (checkLevel(defender, 6, true)) {
                Random rand = new Random();
                int secondWindRandom = rand.nextInt(10);
                if (secondWindRandom == 1){
                    secondWind.castSecondWind(defender, attacker);
                }
            }
            //too slow
            if (checkLevel(defender, 5, true)) {
                Random rand = new Random();
                int tooSlowRandom = rand.nextInt(10);
                if (tooSlowRandom == 1){
                    event.setCancelled(true);
                }
            }
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity){
            LivingEntity defender = (LivingEntity) event.getEntity();
            Player attacker = (Player) event.getDamager();
            //bloody fervour
            if (checkLevel(attacker, 7, true)) {
                Random rand = new Random();
                int bloodyFervourRandom = rand.nextInt(20);
                if (bloodyFervourRandom == 1){
                    bloodyFervour.castBloodyFervour(attacker, defender, damage);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityWeapon(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            switch (item.getType()) {
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    if(item.getItemMeta() != null && item.getItemMeta().getDisplayName().contains("Scythe")){
                        //Coated blade listener
                        Random rand = new Random();
                        int coatedRandom = rand.nextInt(5);
                        if (coatedRandom == 1) {
                            coatedBlade.castCoatedBlade(attacker, event.getEntity());
                        }
                    }
                }
            }
            if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
                switch(item.getItemMeta().getDisplayName()){
                    case "§4Gut Punch" -> {
                        if (!plugin.isSilenced(attacker))
                            if(checkLevel(attacker, 8))
                                gutPunch.castGutPunch(attacker, event.getEntity());
                    }
                    case "§4Memento Mori" -> {
                        if (!plugin.isSilenced(attacker))
                            if(checkLevel(attacker, 10))
                                mementoMori.castMementoMori(attacker, event.getEntity());
                    }
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
        if(event.getAction() == Action.PHYSICAL){
            return;
        }
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if(item.getItemMeta() != null){
            switch(item.getItemMeta().getDisplayName()){
                case "§4Hidden Strike" -> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 2))
                            hiddenStrike.castHiddenStrike(player);
                }
                case "§4Blinding Sand" -> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 3))
                            blindingSand.castBlindingSand(player);
                }
                case "§4Forbidden Slash" -> {
                    if (!plugin.isSilenced(player))
                        if(checkLevel(player, 9))
                            forbiddenSlash.castForbiddenSlash(player);
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
                case "§4Blinding Sand", "§4Gut Punch", "§4Forbidden Slash" -> event.setCancelled(true);
            }
        }
    }

}
