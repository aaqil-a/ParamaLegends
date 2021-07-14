package id.cuna.ParamaLegends.ClassListener.ClassTypeListeners;

import id.cuna.ParamaLegends.ClassListener.ClassListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.Spells.Swordsman.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;

public class SwordsmanListener extends ClassListener implements Listener{

    private final ParamaLegends plugin;
    public DataManager data;

    public final ShieldsUp shieldsUp;
    public final PhoenixDive phoenixDive;
    public final Enrage enrage;
    public final Onslaught onslaught;
    public final TerrifyingCruelty terrifyingCruelty;
    public final Calamity calamity;
    public final Superconducted superconducted;

    private final HashMap<Player, Integer> playerCrippleAttackCount = new HashMap<Player, Integer>();

    public SwordsmanListener(ParamaLegends plugin) {
        super(plugin, ClassType.SWORDSMAN);
        this.plugin = plugin;
        data = plugin.getData();

        shieldsUp = new ShieldsUp(plugin, this);
        phoenixDive = new PhoenixDive(plugin, this);
        enrage = new Enrage(plugin, this);
        onslaught = new Onslaught(plugin, this);
        terrifyingCruelty = new TerrifyingCruelty(plugin, this);
        calamity = new Calamity(plugin, this);
        superconducted = new Superconducted(plugin, this);
    }

    //Set cripple attack count to 0
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        int classLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".swordsmanship");
        super.getPlayerLevel().put(player, classLevel);
        playerCrippleAttackCount.put(player, 0);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            switch (item.getType()){
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    //Check if attack cripples and add to counter
                    if(getPlayerLevel().get(attacker) >= 2){
                        int crippleCount = playerCrippleAttackCount.get(attacker);
                        crippleCount++;
                        if(crippleCount >= 5){
                            crippleCount = 0;
                            if(event.getEntity() instanceof LivingEntity){
                                LivingEntity crippled = (LivingEntity) event.getEntity();
                                crippled.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 4, false, false, false));
                                BukkitTask bleed = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                    plugin.experienceListener.addExp(attacker, ClassType.SWORDSMAN, 1);
                                    crippled.damage(1.072);
                                }, 0, 20);
                                Bukkit.getScheduler().runTaskLater(plugin, bleed::cancel, 82);
                            }
                        }
                        playerCrippleAttackCount.put(attacker, crippleCount);
                    }
                }
            }
        }
        if(event.getDamager() instanceof Firework){
            event.setCancelled(true);
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

        if (item.getItemMeta() != null)
            switch (item.getItemMeta().getDisplayName()) {
                case "§2Shields Up" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 3) && !plugin.isSilenced(player))
                        shieldsUp.castShieldsUp(player);
                }
                case "§2Phoenix Dive" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 5) && !plugin.isSilenced(player))
                        phoenixDive.castPhoenixDive(player);
                }
                case "§2Enrage" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 6) && !plugin.isSilenced(player))
                        enrage.castEnrage(player);
                }
                case "§2Onslaught" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 7) && !plugin.isSilenced(player))
                        onslaught.castOnslaught(player);
                }
                case "§2Terrifying Cruelty" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 8) && !plugin.isSilenced(player))
                        terrifyingCruelty.castTerrifyingCruelty(player);
                }
                case "§2Superconducted" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 9) && !plugin.isSilenced(player))
                        superconducted.castSuperconducted(player);
                }
                case "§2Calamity" -> {
                    event.setCancelled(true);
                    if (checkLevel(player, 10) && !plugin.isSilenced(player))
                        calamity.castCalamity(player);
                }
            }
    }

    public List<Player> getPlayersShielded(){
        return shieldsUp.getPlayersShielded();
    }
    public List<Player> getPlayersEnraging(){
        return enrage.getPlayersEnraging();
    }
    public List<Entity> getEntitiesTerrified(){
        return terrifyingCruelty.getEntitiesTerrified();
    }
    public List<Player> getPlayersCalamity(){
        return calamity.getPlayersCalamity();
    }
    public List<Entity> getEntitiesBlinded() {
        return superconducted.getEntitiesBlinded();
    }

}

