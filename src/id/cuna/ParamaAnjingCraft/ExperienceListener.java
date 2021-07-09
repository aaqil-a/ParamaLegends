package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.material.Crops;
import org.bukkit.metadata.FixedMetadataValue;

import javax.annotation.Nullable;
import java.util.Locale;

public class ExperienceListener implements Listener {

    private final ParamaAnjingCraft plugin;
    public DataManager data;
    private final int[] xpNeeded = {0,460,740,960,1160,1200,1440,1500,1780,1860, Integer.MAX_VALUE};

    public ExperienceListener(final ParamaAnjingCraft plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    //Set metadata of placed blocks to ensure no xp is given when destroyed
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Block b = event.getBlock();
        b.setMetadata("PLACED", new FixedMetadataValue(plugin, "PLACED"));
    }

    //Give player xp when mining certain blocks
    @EventHandler
    public void onMine(BlockBreakEvent event){
        Block b = event.getBlock();
        Player player = event.getPlayer();
        if (b.getMetadata("PLACED").isEmpty()){
            switch (player.getInventory().getItemInMainHand().getType()) {
                case WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE -> {
                    switch (b.getType()) {
                        case COAL_ORE, DEEPSLATE_COAL_ORE -> addExp(player, "mining", 2);
                        case COPPER_ORE, DEEPSLATE_COPPER_ORE, REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE, LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> addExp(player, "mining", 3);
                        case IRON_ORE, DEEPSLATE_IRON_ORE -> addExp(player, "mining", 4);
                        case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE, EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> addExp(player, "mining", 10);
                        case GOLD_ORE, DEEPSLATE_GOLD_ORE -> addExp(player, "mining", 5);
                    }
                }
            }

        }
    }

    public void playerKill(Player player, LivingEntity entity, String skill){
        String mob = "";
        String damageString = String.valueOf(entity.getLastDamage());
        //Check if damage source is from magic
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            switch (key) {
                case ".069", ".068" -> skill = "magic";
                case ".034", ".033", ".035" -> skill = "reaper";
                case ".072", ".073", ".071" -> skill = "swordsmanship";
                case ".016", ".015", ".017" -> skill = "archery";
            }
        }
        //Check if entity was exploded by magic
        if(entity.getLastDamageCause() != null && entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
            skill = "magic";
        }

        if(skill.equals("")){
            //Check if damage source is from archery or swordsmanship
            switch (player.getInventory().getItemInMainHand().getType()) {
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    skill = "swordsmanship";
                }
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    skill = "reaper";
                }
            }
        }
        //Check mob killed
        switch (entity.getType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED -> {
                mob = "zombie";
            }
            case WITCH -> {
                mob = "witch";
            }
            case SKELETON, STRAY -> {
                mob = "skeleton";
            }
            case CREEPER -> {
                mob = "creeper";
            }
            case SPIDER, CAVE_SPIDER -> {
                mob = "spider";
            }
        }
        //Grant exp and lectrum to player according to mob killed
        if(!mob.equals("") && !skill.equals("")){
            addExp(player, skill, data.getConfig().getInt("mobs."+mob+".exp"));
            addLectrum(player, data.getConfig().getInt("mobs."+mob+".lectrum"));
        }
    }

    //Handle xp gained from killing mobs
    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event){
        if (event.getEntity().getKiller() != null) {
           Player player = event.getEntity().getKiller();
           playerKill(player, event.getEntity(), "");
        }
    }

    //Give player exp for ever attack with sword or bow.
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            switch (player.getInventory().getItemInMainHand().getType()) {
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> {
                    addExp(player, "swordsmanship", 1);
                }
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    addExp(player, "reaper", 1);
                }
            }
        }
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                addExp(player, "archery", 2);
                if(event.getEntity() instanceof LivingEntity){
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    if(event.getFinalDamage() >= entity.getHealth()){
                        playerKill(player, entity, "archery");
                    }
                 }
            }
        }
    }

    //Send player message when leveling up a certain skill
    public void levelUpMessage(@Nullable Player player, String skill, int level){
        if(player != null){
            player.sendMessage(ChatColor.GOLD + skill.substring(0,1).toUpperCase() + skill.substring(1) + " leveled up to " + level + ".");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" +ChatColor.ITALIC + data.getConfig().getString("levelupmessage."+skill+"."+level));
        }
    }

    // Add exp to player and check if player levelled up
    public void addExp(@Nullable Player player, String skill, int amount){
        if(player != null && !skill.equals("")){
            int currLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+"."+skill);
            int currExp = data.getConfig().getInt("players."+player.getUniqueId().toString()+"."+skill+"exp");
            currExp += amount;
            if(amount >= 10){
                player.sendMessage(ChatColor.GRAY + "+"+amount +" "+skill.substring(0,1).toUpperCase() + skill.substring(1) +" EXP");
            }
            if(currExp >= xpNeeded[currLevel]){
                currExp -= xpNeeded[currLevel];
                currLevel += 1;
                levelUpMessage(player, skill, currLevel);
                switch (skill) {
                    case "magic" -> plugin.levelUpMagic(player);
                    case "swordsmanship" -> plugin.levelUpSwordsmanship(player);
                    case "archery" -> plugin.levelUpArchery(player);
                }
            }
            data.getConfig().set("players."+player.getUniqueId().toString()+"."+skill, currLevel);
            data.getConfig().set("players."+player.getUniqueId().toString()+"."+skill+"exp", currExp);
            data.saveConfig();
        }
    }

    //Add lectrum gained to player
    public void addLectrum(@Nullable Player player, int amount){
        if(player != null){
            int currLectrum = data.getConfig().getInt("players."+player.getUniqueId().toString()+".lectrum");
            player.sendMessage(ChatColor.GRAY + "+" + amount + " Lectrum");
            currLectrum += amount;
            data.getConfig().set("players."+player.getUniqueId().toString()+".lectrum", currLectrum);
            data.saveConfig();
        }
    }


}
