package me.cuna.paramalegends.classgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.reaper.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

public class ReaperListener implements Listener{

    private final ParamaLegends plugin;
    public DataManager data;

    public final CoatedBlade coatedBlade;
    public final HiddenStrike hiddenStrike;
    public final BlindingSand blindingSand;
    public final BladeMail bladeMail;
    public final SecondWind secondWind;
    public final Prowl prowl;
    public final BloodyFervour bloodyFervour;
    public final GutPunch gutPunch;
    public final ForbiddenSlash forbiddenSlash;
    public final MementoMori mementoMori;

    public ReaperListener(ParamaLegends plugin) {
        this.plugin = plugin;
        data = plugin.getData();

        coatedBlade = new CoatedBlade(plugin);
        hiddenStrike = new HiddenStrike(plugin);
        bladeMail = new BladeMail(plugin);
        secondWind = new SecondWind(plugin);
        blindingSand = new BlindingSand(plugin);
        bloodyFervour = new BloodyFervour(plugin);
        gutPunch = new GutPunch(plugin);
        forbiddenSlash = new ForbiddenSlash(plugin);
        mementoMori = new MementoMori(plugin);
        prowl = new Prowl(plugin);
    }

    //Passive listeners
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        double damage = event.getDamage();
        if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity){
            Player defender = (Player) event.getEntity();
            LivingEntity attacker = (LivingEntity) event.getDamager();
            PlayerParama player = plugin.getPlayerParama(defender);
            int level = player.getLevelFromClassType(ClassGameType.REAPER);
            //blade mail
            if (player.checkLevel(4, ClassGameType.REAPER, true)) {
                Random rand = new Random();
                int bladeMailRandom = rand.nextInt(5);
                if (bladeMailRandom == 1 || (level>=10 && bladeMailRandom<3)) {
                    bladeMail.attackEntity(player, attacker, damage);
                }
            }
            //second wind
            if (player.checkLevel(6, ClassGameType.REAPER, true)) {
                Random rand = new Random();
                int secondWindRandom = rand.nextInt(10);
                if (secondWindRandom == 1 || (level>=10 && secondWindRandom<4)){
                    secondWind.attackEntity(player, attacker, damage);
                }
            }
            //too slow
            if (player.checkLevel(5, ClassGameType.REAPER, true)) {
                Random rand = new Random();
                int tooSlowRandom = rand.nextInt(10);
                if (tooSlowRandom == 1  || (level>=10 && tooSlowRandom<4)){
                    event.setCancelled(true);
                }
            }
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity){
            LivingEntity defender = (LivingEntity) event.getEntity();
            Player attacker = (Player) event.getDamager();
            PlayerParama player = plugin.getPlayerParama(attacker);
            int level = player.getLevelFromClassType(ClassGameType.REAPER);
            //bloody fervour
            if (player.checkLevel(7, ClassGameType.REAPER, true)) {
                Random rand = new Random();
                int bloodyFervourRandom = rand.nextInt(20);
                if (bloodyFervourRandom == 1 || (level>=10 && bloodyFervourRandom<6)){
                    bloodyFervour.attackEntity(player, defender, damage);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityWeapon(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            PlayerParama player = plugin.getPlayerParama(attacker);
            int level = player.getLevelFromClassType(ClassGameType.REAPER);
            ItemStack item = Objects.requireNonNull(attacker.getPlayer()).getInventory().getItemInMainHand();
            switch (item.getType()) {
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    if(item.getItemMeta() != null && item.getItemMeta().getDisplayName().contains("Scythe")){
                        //Coated blade listener
                        Random rand = new Random();
                        int coatedRandom = rand.nextInt(5);
                        if (coatedRandom == 1 || (level>=10 && coatedRandom<3)) {
                            coatedBlade.attackEntity(player, event.getEntity(), 1);
                        }
                    }
                }
            }
            if(item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()){
                switch(item.getItemMeta().getDisplayName()){
                    case ChatColor.COLOR_CHAR+"4Gut Punch" -> {
                        if (player.isNotSilenced()){
                            if(!player.checkCooldown(gutPunch)){
                                event.setCancelled(true);
                            }
                            if(player.checkLevel( 8, ClassGameType.REAPER)){
                                gutPunch.attackEntity(player, event.getEntity(), event.getDamage());
                            }
                        }

                    }
                    case ChatColor.COLOR_CHAR+"4Memento Mori" -> {
                        if(!player.checkCooldown(mementoMori)) {
                            event.setCancelled(true);
                        }
                        if (player.isNotSilenced())
                            if(player.checkLevel(10, ClassGameType.REAPER))
                                mementoMori.attackEntity(player, event.getEntity(), event.getDamage());
                    }
                }
            }
        }
    }

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        if(event.getItem() == null){
            return;
        }
        if(event.getAction() == Action.PHYSICAL){
            return;
        }
        //Check if held item is book
        ItemStack item = event.getItem();
        PlayerParama player = plugin.getPlayerParama(event.getPlayer());
        if(item.getItemMeta() != null){
            switch(item.getItemMeta().getDisplayName()){
                case ChatColor.COLOR_CHAR+"4Hidden Strike" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel(2, ClassGameType.REAPER))
                            hiddenStrike.castSpell(player);
                }
                case ChatColor.COLOR_CHAR+"4Blinding Sand" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel(3, ClassGameType.REAPER))
                            blindingSand.castSpell(player);
                }
                case ChatColor.COLOR_CHAR+"4Forbidden Slash" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel(9, ClassGameType.REAPER))
                            forbiddenSlash.castSpell(player);
                }
                case ChatColor.COLOR_CHAR+"4Prowl" -> {
                    if (player.isNotSilenced())
                        if(player.checkLevel(5, ClassGameType.REAPER))
                            prowl.castSpell(player);
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
                case ChatColor.COLOR_CHAR+"4Blinding Sand", ChatColor.COLOR_CHAR+"4Gut Punch", ChatColor.COLOR_CHAR+"4Forbidden Slash" -> event.setCancelled(true);
            }
        }
    }

}
