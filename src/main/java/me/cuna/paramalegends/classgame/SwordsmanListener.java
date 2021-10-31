package me.cuna.paramalegends.classgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.swordsman.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class SwordsmanListener implements Listener{

    private final ParamaLegends plugin;
    public DataManager data;

    public final ShieldsUp shieldsUp;
    public final PhoenixDive phoenixDive;
    public final Enrage enrage;
    public final Onslaught onslaught;
    public final TerrifyingCruelty terrifyingCruelty;
    public final Calamity calamity;
    public final Superconducted superconducted;
    public SwordsmanListener(ParamaLegends plugin) {
        this.plugin = plugin;
        data = plugin.getData();

        shieldsUp = new ShieldsUp(plugin);
        phoenixDive = new PhoenixDive(plugin);
        enrage = new Enrage(plugin);
        onslaught = new Onslaught(plugin);
        terrifyingCruelty = new TerrifyingCruelty(plugin);
        calamity = new Calamity(plugin);
        superconducted = new Superconducted(plugin);
    }

    //Set cripple attack count to 0
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        event.getPlayer().setMetadata("CRIPPLE", new FixedMetadataValue(plugin, 0));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            PlayerParama playerParama = plugin.getPlayerParama(attacker);
            ItemStack item = attacker.getPlayer().getInventory().getItemInMainHand();
            switch (item.getType()){
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    //Check if attack cripples and add to counter
                    if(playerParama.getLevelFromClassType(ClassGameType.SWORDSMAN) >= 2){
                        int crippleCount = attacker.getMetadata("CRIPPLE").get(0).asInt();
                        crippleCount++;
                        if(crippleCount >= 5){
                            crippleCount = 0;
                            if(event.getEntity() instanceof LivingEntity){
                                LivingEntity crippled = (LivingEntity) event.getEntity();
                                crippled.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 4, false, false, false));
                                BukkitTask bleed = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                    plugin.experienceListener.addExp(attacker, ClassGameType.SWORDSMAN, 1);
                                    crippled.damage(1.072);
                                }, 0, 20);
                                Bukkit.getScheduler().runTaskLater(plugin, bleed::cancel, 82);
                            }
                        }
                        attacker.setMetadata("CRIPPLE", new FixedMetadataValue(plugin, crippleCount));
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
        if(event.getItem() == null){
            return;
        }
        if(event.getAction() == Action.PHYSICAL){
            return;
        }
        //Check if held item is book
        ItemStack item = event.getItem();
        PlayerParama playerParama = plugin.getPlayerParama(player);
        if (item.getItemMeta() != null)
            switch (item.getItemMeta().getDisplayName()) {
                case ChatColor.COLOR_CHAR+"2Shields Up" -> {
                    event.setCancelled(true);
                    if (playerParama.checkLevel(3, ClassGameType.SWORDSMAN) && playerParama.isNotSilenced())
                        shieldsUp.castSpell(playerParama);
                }
                case ChatColor.COLOR_CHAR+"2Phoenix Dive" -> {
                    event.setCancelled(true);
                    if (playerParama.checkLevel(5, ClassGameType.SWORDSMAN) && playerParama.isNotSilenced())
                        phoenixDive.castSpell(playerParama);
                }
                case ChatColor.COLOR_CHAR+"2Enrage" -> {
                    event.setCancelled(true);
                    if (playerParama.checkLevel(6, ClassGameType.SWORDSMAN) && playerParama.isNotSilenced())
                        enrage.castSpell(playerParama);
                }
                case ChatColor.COLOR_CHAR+"2Onslaught" -> {
                    event.setCancelled(true);
                    if (playerParama.checkLevel(7, ClassGameType.SWORDSMAN) && playerParama.isNotSilenced())
                        onslaught.castSpell(playerParama);
                }
                case ChatColor.COLOR_CHAR+"2Terrifying Cruelty" -> {
                    event.setCancelled(true);
                    if (playerParama.checkLevel(8, ClassGameType.SWORDSMAN) && playerParama.isNotSilenced())
                        terrifyingCruelty.castSpell(playerParama);
                }
                case ChatColor.COLOR_CHAR+"2Superconducted" -> {
                    event.setCancelled(true);
                    if (playerParama.checkLevel(9, ClassGameType.SWORDSMAN) && playerParama.isNotSilenced())
                        superconducted.castSpell(playerParama);
                }
                case ChatColor.COLOR_CHAR+"2Calamity" -> {
                    event.setCancelled(true);
                    if (playerParama.checkLevel(10, ClassGameType.SWORDSMAN) && playerParama.isNotSilenced())
                        calamity.castSpell(playerParama);
                }
            }
    }

}

