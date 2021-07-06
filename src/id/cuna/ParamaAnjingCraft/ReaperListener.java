package id.cuna.ParamaAnjingCraft;

import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.w3c.dom.Attr;

import java.util.*;
import java.util.function.Predicate;
import java.util.jar.Attributes;

public class ReaperListener implements Listener{
    private final ParamaAnjingCraft plugin;
    public DataManager data;
    private final List<String> playerCoatedBladeCooldowns = new ArrayList<String>();
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
        playerReaperLevel.put(player, data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaper"));
        modifyLore(player);
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

    public void modifyLore(Player player){
        Inventory playerInv = player.getInventory();
        ItemStack[] itemArray = playerInv.getStorageContents();
        for (ItemStack item : itemArray){
            if (item != null){
                int newDamage = 0;
                switch (item.getType()) {
                    case WOODEN_HOE, GOLDEN_HOE -> {
                        newDamage = 4;
                    }
                    case IRON_HOE -> {
                        newDamage = 6;
                    }
                    case STONE_HOE -> {
                        newDamage = 5;
                    }
                    case DIAMOND_HOE -> {
                        newDamage = 7;
                    }
                    case NETHERITE_HOE -> {
                        newDamage = 8;
                    }
                }
                if (newDamage > 0){
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = meta.getLore();
                    if(lore == null) {
                        lore = new ArrayList<String>();
                        lore.add("");
                        lore.add(ChatColor.GRAY+ "When in Main Hand:");
                        lore.add(" " + ChatColor.DARK_GREEN + "" + newDamage + " Attack Damage");
                    }
                    meta.setLore(lore);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    item.setItemMeta(meta);
                }
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
            switch (item.getType()) {
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    if (coatedRandom == 1) {
                        castCoatedBlade(attacker, event.getEntity());
                    } else if (item.getItemMeta() != null) {
                        switch (item.getItemMeta().getDisplayName()) {
                            case "Hidden Strike":
                                break;
                        }
                    }
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
        }
    }
}
