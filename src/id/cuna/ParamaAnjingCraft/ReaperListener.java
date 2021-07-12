package id.cuna.ParamaAnjingCraft;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ReaperListener implements Listener{
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    private final List<String> playerCoatedBladeCooldowns = new ArrayList<>();
    private final List<String> playerTooSlowCooldowns = new ArrayList<>();
    private final List<String> playerBladeMailCooldowns = new ArrayList<>();
    private final List<String> playerSecondWindCooldowns = new ArrayList<>();
    private final List<String> playerBloodyFervourCooldowns = new ArrayList<>();
    private final List<String> playerGutPunchCooldowns = new ArrayList<>();
    private final List<String> playerForbiddenSlashCooldowns = new ArrayList<>();
    private final List<String> playerHiddenStrikeCooldowns = new ArrayList<>();
    private final HashMap<Player, Integer> playerReaperLevel = new HashMap<>();

    public ReaperListener(ParamaAnjingCraft plugin) {
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Get player's Reaper level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        playerReaperLevel.put(player, data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaper"));
    }

    //Remove player from plugin memory on leave
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        playerReaperLevel.remove(player);
    }

    // Mana Handler
    public boolean subtractMana(Player player, int manaCost){
        int currMana = player.getLevel();
        if(manaCost <= currMana){
            player.setLevel(currMana - manaCost);
            return true;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "Not enough mana.");
            return false;
        }
    }

    public boolean checkLevel(Player player, int level){
        if(playerReaperLevel.get(player) < level){
            player.sendMessage(ChatColor.GRAY + "You do not understand how to use this yet.");
            return false;
        } else {
            return true;
        }
    }

    public void levelUp(Player player){
        int curLevel = playerReaperLevel.get(player);
        playerReaperLevel.replace(player, curLevel+1);
    }

    //Cooldown Handler
    public void sendCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.GRAY + " is on cooldown.");
    }
    public void sendNoLongerCooldownMessage(Player player, String spell){
        player.sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }

    public void castCoatedBlade (Player attacker, Entity entity) {
        if (playerCoatedBladeCooldowns.contains(attacker.getUniqueId().toString())){
            return;
        } else if (subtractMana(attacker, 0)) {
            if (entity instanceof LivingEntity) {
                BukkitTask poison = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    ((LivingEntity) entity).damage(1.034, attacker);
                }, 0, 20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    poison.cancel();
                }, 42);
                attacker.sendMessage(ChatColor.GREEN + "Coated Blade Active.");
                playerBladeMailCooldowns.add(attacker.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerBladeMailCooldowns.contains(attacker.getUniqueId().toString())){
                        playerBladeMailCooldowns.remove(attacker.getUniqueId().toString());
                    }
                }, 80);
            }
        }
    }

    public void castBladeMail (Player player, Entity entity, double damage){
        if (playerBladeMailCooldowns.contains(player.getUniqueId().toString())){
            return;
        } else if (subtractMana(player, 0)) {
            if (entity instanceof LivingEntity){
                damage = damage * (10/100);
                ((LivingEntity) entity).damage(damage + 0.34, player);
            }
            player.sendMessage("BladeMail Active");
            playerCoatedBladeCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerCoatedBladeCooldowns.contains(player.getUniqueId().toString())){
                    playerCoatedBladeCooldowns.remove(player.getUniqueId().toString());
                }
            }, 82);
        }
    }

    public void castTooSlow (Player player, Entity entity, EntityDamageByEntityEvent event){
        if(playerTooSlowCooldowns.contains(player.getUniqueId().toString())){
            return;
        } else if (subtractMana(player, 0)){
            if (entity instanceof LivingEntity){
                event.setDamage(0);
                player.sendMessage("You dodged the attack, you receive 0 damage.");
                playerTooSlowCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerTooSlowCooldowns.contains(player.getUniqueId().toString())){
                        playerTooSlowCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 42);
            }
        }
    }

    public void castSecondWind (Player player, Entity entity, EntityDamageByEntityEvent event){
        if (playerSecondWindCooldowns.contains(player.getUniqueId().toString())){
            return;
        } else if (subtractMana(player, 0)){
            if (entity instanceof LivingEntity){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 62, 4));
                player.sendMessage("Second Wind Active, you got speed buff");
                playerSecondWindCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerSecondWindCooldowns.contains(player.getUniqueId().toString())){
                        playerSecondWindCooldowns.remove(player.getUniqueId().toString());
                    }
                }, 102);
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

    public void castForbiddenSlash (Player player, Entity entity, EntityDamageByEntityEvent event){
        if (playerForbiddenSlashCooldowns.contains(player.getUniqueId().toString())) {
            event.setDamage(0);
            player.sendMessage(ChatColor.RESET + "" +ChatColor.RED +"Forbidden Slash is in Cooldown !");
            return;
        } else if(subtractMana(player, 0)) {
            if(entity instanceof  LivingEntity){
                double currDamage = event.getDamage();
                double finalDamage = currDamage + currDamage + 0.34;
                event.setDamage(finalDamage);
            }
            playerForbiddenSlashCooldowns.add(player.getUniqueId().toString());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(playerForbiddenSlashCooldowns.contains(player.getUniqueId().toString())){
                    playerForbiddenSlashCooldowns.remove(player.getUniqueId().toString());
                    player.sendMessage(ChatColor.RESET + "" +ChatColor.GREEN +"Forbidden Slash is off Cooldown !");
                }
            }, 402);
        }
    }

    public void castHiddenStrike (Player player, Entity entity, EntityDamageByEntityEvent event){
        if (playerHiddenStrikeCooldowns.contains(player.getUniqueId().toString())) {
            event.setDamage(0);
            player.sendMessage(ChatColor.RESET + "" + ChatColor.RED + "Hidden Strike is in Cooldown !");
            return;
        } else if(subtractMana(player, 0)){
            if(entity instanceof  LivingEntity){
                double currDamage = event.getDamage();
                double finalDamage = (currDamage + (currDamage * 0.5) + 0.34);
                event.setDamage(finalDamage);
                playerHiddenStrikeCooldowns.add(player.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerHiddenStrikeCooldowns.contains(player.getUniqueId().toString())){
                        playerHiddenStrikeCooldowns.remove(player.getUniqueId().toString());
                        player.sendMessage(ChatColor.RESET + "" +ChatColor.GREEN +"Hidden Strike is off Cooldown !");
                    }
                }, 202);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntitySkill(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player && event.getDamager() instanceof LivingEntity){
            Player defender = (Player) event.getEntity();
            LivingEntity attacker = (LivingEntity) event.getDamager();
            if (checkLevel(defender, 4)) {
                Random rand = new Random();
                int bladeMailRandom = rand.nextInt(5);
                if (bladeMailRandom == 1) {
                    double damage = event.getDamage();
                    castBladeMail(defender, attacker, damage);
                }
            }
        }
        if (event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();
            LivingEntity attacker = (LivingEntity) event.getDamager();
            if (checkLevel(defender, 6)) {
                Random rand = new Random();
                int secondWindRandom = rand.nextInt(10);
                if (secondWindRandom == 1){
                    castSecondWind(defender, attacker, event);
                }
            }
            if (checkLevel(defender, 5)) {
                Random rand = new Random();
                int tooSlowRandom = rand.nextInt(10);
                if (tooSlowRandom == 1){
                    castTooSlow(defender, attacker, event);
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityWeapon(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            switch (item.getType()) {
                case WOODEN_HOE, GOLDEN_HOE -> {
                    event.setDamage(event.getDamage() + 3);
                }
                case IRON_HOE -> {
                    event.setDamage(event.getDamage() + 5);
                }
                case STONE_HOE -> {
                    event.setDamage(event.getDamage()+4);
                }
                case DIAMOND_HOE -> {
                    event.setDamage(event.getDamage() + 6);
                }
                case NETHERITE_HOE -> {
                    event.setDamage(event.getDamage() + 7);
                }
            }
            Random rand = new Random();
            int coatedRandom = rand.nextInt(5);
            switch (item.getType()) {
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    if (coatedRandom == 1) {
                        castCoatedBlade(attacker, event.getEntity());
                    }
                    if (item.getItemMeta() != null){
                        switch (item.getItemMeta().getDisplayName()){
                            case "§4Gut Punch" :
                                castGutPunch(attacker, event.getEntity(), event);
                                break;
                            case "§4Hidden Strike":
                                castHiddenStrike(attacker, event.getEntity(),event);
                                break;
                            case "§4Blinding Sand":
                                attacker.sendMessage("Bro ini skill susah / devnya males");
                                break;
                            case "§4Forbidden Slash":
                                castForbiddenSlash(attacker, event.getEntity(), event);
                                break;
                        }
                    }
                }
            }
        }
    }
}
