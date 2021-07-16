package id.cuna.ParamaLegends.GameListener;

import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import javax.annotation.Nullable;

public class ExperienceListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    private final int[] xpNeeded = {0,460,740,960,1160,1200,1440,1500,1780,1860, Integer.MAX_VALUE};

    public ExperienceListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
    }

    public void playerKill(Player player, LivingEntity entity, @Nullable ClassType skill){
        String mob = "";
        String damageString = String.valueOf(entity.getLastDamage());

        //Check class type from damage key
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            switch (key) {
                case ".069", ".068" -> skill = ClassType.MAGIC;
                case ".034", ".033", ".035" -> skill = ClassType.REAPER;
                case ".072", ".073", ".071" -> skill = ClassType.SWORDSMAN;
                case ".016", ".015", ".017" -> skill = ClassType.ARCHERY;
            }
        }
        //Check if entity was exploded by magic
        if(entity.getLastDamageCause() != null && entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
            skill = ClassType.MAGIC;
        }
        //Check if damage source is from magic
        if(skill == null){
            //Check if damage source is from archery or swordsmanship
            skill = switch (player.getInventory().getItemInMainHand().getType()) {
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> ClassType.SWORDSMAN;
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> ClassType.REAPER;
                default -> null;
            };
        }
        //Check mob killed
        mob = switch (entity.getType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED -> "zombie";
            case WITCH -> "witch";
            case SKELETON, STRAY ->"skeleton";
            case CREEPER -> "creeper";
            case SPIDER, CAVE_SPIDER, PHANTOM -> "spider";
            case ENDERMAN -> "enderman";
            default -> "";
        };
        //Grant exp and lectrum to player according to mob killed
        if(!mob.equals("") && (skill != null)){
            addExp(player, skill, data.getConfig().getInt("mobs."+mob+".exp"));
            addLectrum(player, data.getConfig().getInt("mobs."+mob+".lectrum"));
        }
    }

    //Handle xp gained from killing mobs
    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event){
        if (event.getEntity().getKiller() != null) {
           Player player = event.getEntity().getKiller();
           playerKill(player, event.getEntity(), null);
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
                    addExp(player, ClassType.SWORDSMAN, 1);
                }
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    addExp(player, ClassType.REAPER, 1);
                }
            }
        }
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                addExp(player, ClassType.ARCHERY, 2);
                if(event.getEntity() instanceof LivingEntity){
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    if(event.getFinalDamage() >= entity.getHealth()){
                        playerKill(player, entity, ClassType.ARCHERY);
                    }
                 }
            }
        }
    }

    //Send player message when leveling up a certain skill
    public void levelUpMessage(@Nullable Player player, String skill, int level){
        if(player != null){
            player.sendMessage(ChatColor.GOLD + skill + " leveled up to " + level + ".");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" +ChatColor.ITALIC + data.getConfig().getString("levelupmessage."+skill.toLowerCase()+"."+level));
        }
    }

    // Add exp to player and check if player levelled up
    public void addExp(@Nullable Player player, ClassType skillType, int amount){
        String skill = switch(skillType){
            case ARCHERY -> "Archery";
            case MAGIC -> "Magic";
            case SWORDSMAN -> "Swordsmanship";
            case REAPER -> "Reaper";
        };
        if(player != null){
            int currLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+"."+skill.toLowerCase());
            int currExp = data.getConfig().getInt("players."+player.getUniqueId().toString()+"."+skill.toLowerCase()+"exp");
            currExp += amount;
            if(amount >= 10){
                player.sendMessage(ChatColor.GRAY + "+"+amount +" "+skill+" EXP");
            }
            if(currExp >= xpNeeded[currLevel]){
                currExp -= xpNeeded[currLevel];
                currLevel += 1;
                levelUpMessage(player, skill, currLevel);
                switch (skillType) {
                    case MAGIC -> plugin.levelUpMagic(player);
                    case SWORDSMAN -> plugin.levelUpSwordsmanship(player);
                    case ARCHERY -> plugin.levelUpArchery(player);
                }
            }
            data.getConfig().set("players."+player.getUniqueId().toString()+"."+skill.toLowerCase(), currLevel);
            data.getConfig().set("players."+player.getUniqueId().toString()+"."+skill.toLowerCase()+"exp", currExp);
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
