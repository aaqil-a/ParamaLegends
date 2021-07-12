package id.cuna.ParamaLegends.ClassListener.ClassTypeListeners;

import id.cuna.ParamaLegends.ClassListener.ClassListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.Spells.Reaper.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ReaperListener extends ClassListener implements Listener{

    private final ParamaLegends plugin;
    public DataManager data;

    public final CoatedBlade coatedBlade;
    public final HiddenStrike hiddenStrike;
    private final BlindingSand blindingSand;
    private final BladeMail bladeMail;
    private final SecondWind secondWind;

    private final List<String> playerBloodyFervourCooldowns = new ArrayList<>();
    private final List<String> playerGutPunchCooldowns = new ArrayList<>();

    public ReaperListener(ParamaLegends plugin) {
        super(plugin, ClassType.REAPER);
        this.plugin = plugin;
        data = plugin.getData();

        coatedBlade = new CoatedBlade(plugin, this);
        hiddenStrike = new HiddenStrike(plugin, this);
        bladeMail = new BladeMail(plugin, this);
        secondWind = new SecondWind(plugin, this);
        blindingSand = new BlindingSand(plugin, this);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        double damage = event.getDamage();
        if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity){
            Player defender = (Player) event.getEntity();
            LivingEntity attacker = (LivingEntity) event.getDamager();
            if (getPlayerLevel().get(defender) >= 4) {
                Random rand = new Random();
                int bladeMailRandom = rand.nextInt(5);
                if (bladeMailRandom == 1) {
                    bladeMail.castBladeMail(defender, attacker, damage);
                }
            }
        }
        if (event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();
            LivingEntity attacker = (LivingEntity) event.getDamager();
            if (getPlayerLevel().get(defender) >= 6) {
                Random rand = new Random();
                int secondWindRandom = rand.nextInt(10);
                if (secondWindRandom == 1){
                    secondWind.castSecondWind(defender, attacker);
                }
            }
            if (getPlayerLevel().get(defender) >= 5) {
                Random rand = new Random();
                int tooSlowRandom = rand.nextInt(10);
                if (tooSlowRandom == 1){
                    event.setCancelled(true);
                }
            }
        }
        if (event.getDamager() instanceof Player){
            LivingEntity defender = (LivingEntity) event.getEntity();
            Player attacker = (Player) event.getDamager();
            if (checkLevel(attacker, 7)) {
                Random rand = new Random();
                int bloodyFervourRandom = rand.nextInt(20);
                if (bloodyFervourRandom == 1){
                    castBloodyFervour(attacker, defender, event);
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
                    Random rand = new Random();
                    int coatedRandom = rand.nextInt(5);
                    if (coatedRandom == 1) {
                        coatedBlade.castCoatedBlade(attacker, event.getEntity());
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

    public void castBloodyFervour (Player player, Entity entity, EntityDamageByEntityEvent event){
        if (playerBloodyFervourCooldowns.contains(player.getUniqueId().toString())) {
            return;
        } else if (subtractMana(player, 0)){
            double damage = event.getDamage();
            double currHealth = player.getHealth();
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
            if (currHealth < maxHealth){
                player.setHealth(currHealth + damage);
                player.sendMessage("Bloody Fervour Active.");
            }
        }
    }

    public void castGutPunch (Player player , Entity entity, EntityDamageByEntityEvent event) {
        if (playerGutPunchCooldowns.contains(player.getUniqueId().toString())) {
            event.setDamage(0);
            player.sendMessage(ChatColor.RESET + "" +ChatColor.RED +"Gut Punch is in Cooldown !");
            return;
        }else if (subtractMana(player, 0)){
            if(entity instanceof LivingEntity){
                double currDamage = event.getDamage();
                double entityHealth = ((LivingEntity) entity).getHealth();
                double maxHealth = ((LivingEntity) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                double percentHealth = (entityHealth / maxHealth);
                double finalDamage = (currDamage + (currDamage * percentHealth)) + 0.34;
                if (entity instanceof Player){
                    ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 62, 3));
                    ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 62, 3));
                }
                event.setDamage(finalDamage);
                playerGutPunchCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerGutPunchCooldowns.contains(player.getUniqueId().toString())){
                        playerGutPunchCooldowns.remove(player.getUniqueId().toString());
                        player.sendMessage(ChatColor.RESET + "" +ChatColor.GREEN +"Gut Punch is off Cooldown !");
                    }
                }, 182);
            }
        }
    }
}
