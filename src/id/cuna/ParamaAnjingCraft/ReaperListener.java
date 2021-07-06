package id.cuna.ParamaAnjingCraft;

import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.Random;

public class ReaperListener implements Listener{
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    private final int[] maxMana = {0,50,100,150,200,250,300,400,500,600,800};
    private final int[] manaRegen = {0,1,1,2,3,3,4,5,6,7,8};
    private final List<String> playerCoatedBladeCooldowns = new ArrayList<String>();
    private final List<String> playerHiddenStrikeCooldowns = new ArrayList<String>();
    private final List<String> playerBlindingSandCooldowns = new ArrayList<String>();
    private final List<String> playerHittingUrselfCooldowns = new ArrayList<String>();
    private final List<String> playerTooSlowCooldowns = new ArrayList<String>();
    private final List<String> playerSecondWindCooldowns = new ArrayList<String>();
    private final List<String> playerBloodyFervourCooldowns = new ArrayList<String>();
    private final List<String> playerGutPunchCooldowns = new ArrayList<String>();
    private final List<String> playerForbiddenSlashCooldowns = new ArrayList<String>();
    private final List<String> playerMementoMoriCooldowns = new ArrayList<String>();
    private final HashMap<Player, BukkitTask> playerManaRegenTasks = new HashMap<>();
    private final HashMap<Player, Integer> playerReaperLevel = new HashMap<Player, Integer>();
    private final HashMap<String, Integer> playerCurrentLevel = new HashMap<String, Integer>();

    public ReaperListener(ParamaAnjingCraft plugin) {
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Get player's Reaper level and set exp level to max mana on join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        playerReaperLevel.put(player, data.getConfig().getInt("players." + player.getUniqueId().toString() + ".magic"));
        player.setExp(0);
        if(playerCurrentLevel.containsKey(player.getUniqueId().toString())){
            player.setLevel(playerCurrentLevel.get(player.getUniqueId().toString()));
        } else {
            player.setLevel(maxMana[playerReaperLevel.get(player)]);
        }
        //Create task to regenerate mana over time
        playerManaRegenTasks.put(player,
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    int curMana = player.getLevel();
                    if(curMana < maxMana[playerReaperLevel.get(player)]){
                        curMana += manaRegen[playerReaperLevel.get(player)] ;
                        if(curMana > maxMana[playerReaperLevel.get(player)])
                            curMana = maxMana[playerReaperLevel.get(player)];
                        player.setExp(0);
                        player.setLevel(curMana);
                        playerCurrentLevel.put(player.getUniqueId().toString(), curMana);
                    }
                }, 0, 20)
        );
    }

    //Cancel all exp gained
    @EventHandler
    public void onPlayerXpChange(PlayerExpChangeEvent event){
        event.setAmount(0);
    }

    //Remove player from plugin memory on leave but store player current mana
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        playerManaRegenTasks.get(player).cancel();
        playerManaRegenTasks.remove(player);
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
                attacker.sendMessage(ChatColor.GREEN + "Pisonya ada poison");
                playerCoatedBladeCooldowns.add(attacker.getUniqueId().toString());
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if(playerCoatedBladeCooldowns.contains(attacker.getUniqueId().toString())){
                        playerCoatedBladeCooldowns.remove(attacker.getUniqueId().toString());
                    }
                }, 80);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            Random rand = new Random();
            int coatedRandom = rand.nextInt(5);
            switch (item.getType()){
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    if (coatedRandom == 1) {
                        castCoatedBlade(attacker, event.getEntity());
                    }else if(item.getItemMeta() != null){
                        switch (item.getItemMeta().getDisplayName()){
                            case "Hidden Strike":
                                break;
                        }
                    }
                }
            }
        }
    }
}
