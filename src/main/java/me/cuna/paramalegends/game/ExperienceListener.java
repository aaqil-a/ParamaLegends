package me.cuna.paramalegends.game;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.classgame.ClassGameType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class ExperienceListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    public final int[] xpNeeded = {0,920,1480,1920,2320,2400,4320,6750,8010,8370, Integer.MAX_VALUE};
    public final int[] xpNeededSwordsman = {0,1196,1924,2496,3016,3120,5616,8775,10413,10881, Integer.MAX_VALUE};
    private final int[] maxLevel = {0, 6, 8, 10, 10, 10};
    private int worldLevel;

    public ExperienceListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        this.worldLevel = data.getConfig().getInt("world.level");
    }

    public void playerKill(Player player, LivingEntity entity, ClassGameType skill){
        String damageString = String.valueOf(entity.getLastDamage());

        //Check class type from damage key
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            switch (key) {
                case ".069", ".068" -> skill = ClassGameType.MAGIC;
                case ".034", ".033", ".035" -> skill = ClassGameType.REAPER;
                case ".072", ".073", ".071" -> skill = ClassGameType.SWORDSMAN;
                case ".016", ".015", ".017" -> skill = ClassGameType.ARCHERY;
            }
        }
        if(skill == null){
            //Check if damage source is from swordsmanship or reaper
            skill = switch (player.getInventory().getItemInMainHand().getType()) {
                case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> ClassGameType.SWORDSMAN;
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> ClassGameType.REAPER;
                default -> null;
            };
            if(skill == null){
                skill = switch (player.getInventory().getItemInOffHand().getType()) {
                    case WOODEN_SWORD, STONE_SWORD, GOLDEN_SWORD, IRON_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> ClassGameType.SWORDSMAN;
                    case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> ClassGameType.REAPER;
                    default -> null;
                };
            }
        }
        //Check mob killed
        String mob = switch (entity.getType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED, ZOMBIFIED_PIGLIN, ZOGLIN, PILLAGER, VINDICATOR, VEX -> "zombie";
            case WITCH -> "witch";
            case SKELETON, STRAY, GHAST ->"skeleton";
            case CREEPER -> "creeper";
            case SPIDER, CAVE_SPIDER, PHANTOM, GUARDIAN -> "spider";
            case ENDERMAN, ELDER_GUARDIAN, RAVAGER -> "enderman";
            default -> "";
        };
        //Grant exp and lectrum to player according to mob killed
        if(!mob.equals("") && (skill != null)){
            addExp(player, skill, data.getConfig().getInt("mobs."+mob+".exp"), data.getConfig().getInt("mobs."+mob+".lectrum"));
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
                    addExp(player, ClassGameType.SWORDSMAN, 1);
                }
                case WOODEN_HOE, STONE_HOE, GOLDEN_HOE, IRON_HOE, DIAMOND_HOE, NETHERITE_HOE -> {
                    addExp(player, ClassGameType.REAPER, 1);
                }
            }
        }
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                addExp(player, ClassGameType.ARCHERY, 2);
                if(event.getEntity() instanceof LivingEntity){
                    LivingEntity entity = (LivingEntity) event.getEntity();
                    if(event.getFinalDamage() >= entity.getHealth()){
                        playerKill(player, entity, ClassGameType.ARCHERY);
                    }
                 }
            }
        }
    }

    //Send player message when leveling up a certain skill
    public void levelUpMessage(Player player, String skill, int level){
        if(player != null){
            player.sendMessage(ChatColor.GOLD + skill + " leveled up to " + level + ".");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" +ChatColor.ITALIC + data.getConfig().getString("levelupmessage."+skill.toLowerCase()+"."+level));
        }
    }

    public void addExp(Player player, ClassGameType skilltype, int amount){
        addExp(player, skilltype, amount, 0);
    }

    // Add exp to player and check if player levelled up
    public void addExp(Player player, ClassGameType skillType, int amount, int lectrum){
        String skill = switch(skillType){
            case ARCHERY -> "Archery";
            case MAGIC -> "Magic";
            case SWORDSMAN -> "Swordsmanship";
            case REAPER -> "Reaper";
        };
        if(player != null){
            PlayerParama playerParama = plugin.getPlayerParama(player);
            int currLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+"."+skill.toLowerCase());
            int currExp = data.getConfig().getInt("players."+player.getUniqueId().toString()+"."+skill.toLowerCase()+"exp");
            currExp += amount;
            if(amount >= 10){
                sendActionBarMessage(player,lectrum, amount);
            }
            if(skillType.equals(ClassGameType.SWORDSMAN)){
                if(currExp >= xpNeededSwordsman[currLevel]){
                    if(currLevel < maxLevel[worldLevel]){
                        currExp -= xpNeededSwordsman[currLevel];
                        currLevel += 1;
                        levelUpMessage(player, skill, currLevel);
                        playerParama.levelUp(ClassGameType.SWORDSMAN);
                        playerParama.levelUpMana(currLevel);
                    } else {
                        currExp = xpNeededSwordsman[currLevel];
                    }
                }
            } else {
                if(currExp >= xpNeeded[currLevel]){
                    if(currLevel < maxLevel[worldLevel]){
                        currExp -= xpNeeded[currLevel];
                        currLevel += 1;
                        levelUpMessage(player, skill, currLevel);
                        playerParama.levelUp(skillType);
                        playerParama.levelUpMana(currLevel);
                    } else {
                        currExp = xpNeeded[currLevel];
                    }
                }
            }

            data.getConfig().set("players."+player.getUniqueId().toString()+"."+skill.toLowerCase(), currLevel);
            data.getConfig().set("players."+player.getUniqueId().toString()+"."+skill.toLowerCase()+"exp", currExp);
            data.saveConfig();
        }
    }

    //Add lectrum gained to player
    public void addLectrum(Player player, int amount){
        if(player != null){
            int currLectrum = data.getConfig().getInt("players."+player.getUniqueId().toString()+".lectrum");
            currLectrum += amount;
            data.getConfig().set("players."+player.getUniqueId().toString()+".lectrum", currLectrum);
            data.saveConfig();
        }
    }

    //send message action bar
    public void sendActionBarMessage(Player player, int lectrum, int exp){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GRAY + "+" + lectrum + " Lectrum " + "+" + exp +" EXP"));
    }

    public void setWorldLevel(int worldLevel){
        data.getConfig().set("world.level", worldLevel);
        data.saveConfig();
        this.worldLevel = worldLevel;
    }
    public int getWorldLevel(){
        return worldLevel;
    }

}
