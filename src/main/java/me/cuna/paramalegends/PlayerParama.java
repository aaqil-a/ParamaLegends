package me.cuna.paramalegends;

import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.spell.ArrowParama;
import me.cuna.paramalegends.spell.AttackParama;
import me.cuna.paramalegends.spell.SpellParama;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PlayerParama {

    private final ParamaLegends plugin;
    private final Player player;
    private BukkitTask manaRegenTask;
    private boolean silenced = false;
    private final List<SpellParama> spellOnCooldown = new ArrayList<>();
    private final List<AttackParama> attackOnCooldown = new ArrayList<>();
    private final List<ArrowParama> arrowOnCooldown = new ArrayList<>();
    private final HashMap<String, BukkitTask> playerTasks = new HashMap<>();
    private final HashMap<String, Entity> playerEntities = new HashMap<>();
    public final HashMap<String, BukkitTask> refreshReaperCooldown = new HashMap<>();
    public final HashMap<String, BukkitTask> refreshTinkerCooldown = new HashMap<>();
    private final HashMap<String, Integer> magicMasteryLevel = new HashMap<>();
    private final DataManager data;
    private int playerCurrentMana;
    private int playerManaLevel;
    private int magicLevel;
    private int swordsLevel;
    private int archeryLevel;
    private int reaperLevel;

    public PlayerParama(ParamaLegends plugin, Player player){
        this.plugin = plugin;
        data = plugin.getData();
        this.player = player;

        if (!data.getConfig().contains("players." + player.getUniqueId().toString())){
            data.getConfig().set("players." + player.getUniqueId().toString() + ".joined", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", 50);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".swordsmanship", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".swordsmanshipexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".archery", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".archeryexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".magic", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".magicexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".mining", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".miningexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".reaper", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".reaperexp", 0);
            data.saveConfig();
        }

        magicLevel = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".magic");
        swordsLevel = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".swordsmanship");
        archeryLevel = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".archery");
        reaperLevel = data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaper");
        playerManaLevel = Math.max(Math.max(magicLevel,swordsLevel),Math.max(archeryLevel,reaperLevel));
        playerCurrentMana = 0;
        addPlayerManaRegenTasks();

        //welcome message
        player.sendMessage(ChatColor.GOLD+"This server is running a very early version of Parama Legends");
        player.sendMessage(ChatColor.GOLD+"Check out what's new using /whatsnew");

        //set archery speed passive
        if(archeryLevel >= 4){
            applySpeedPassive();
        }

        //set mastery levels
        for(String spell : plugin.magicListener.getSpellNames()){
            int masteryLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+".mastery."+spell);
            magicMasteryLevel.put(spell, masteryLevel);
        }
    }

    public void addPlayerManaRegenTasks() {
        //Create task to regenerate mana over time
        manaRegenTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (playerCurrentMana < plugin.getMaxMana()[playerManaLevel]) {
                playerCurrentMana += plugin.getManaRegen()[playerManaLevel];
                if (playerCurrentMana > plugin.getMaxMana()[playerManaLevel])
                    playerCurrentMana = plugin.getMaxMana()[playerManaLevel];
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE + "Mana: " + playerCurrentMana + "/" + plugin.getMaxMana()[playerManaLevel]));
            } else {
                manaRegenTask.cancel();
            }
        }, 0, 20);
    }

    public void addToCooldown(SpellParama spell){
        spellOnCooldown.add(spell);
    }
    public void removeFromCooldown(SpellParama spell){
        spellOnCooldown.remove(spell);
    }
    public boolean checkCooldown(SpellParama spell){
        return spellOnCooldown.contains(spell);
    }

    public void addToCooldown(AttackParama attack){
        attackOnCooldown.add(attack);
    }
    public void removeFromCooldown(AttackParama attack){
        attackOnCooldown.remove(attack);
    }
    public boolean checkCooldown(AttackParama attack){
        return attackOnCooldown.contains(attack);
    }

    public void addToCooldown(ArrowParama arrow){
        arrowOnCooldown.add(arrow);
    }
    public void removeFromCooldown(ArrowParama arrow){
        arrowOnCooldown.remove(arrow);
    }
    public boolean checkCooldown(ArrowParama arrow){
        return arrowOnCooldown.contains(arrow);
    }

    public void addTask(String key, BukkitTask task){
        if(playerTasks.containsKey(key)){
            playerTasks.get(key).cancel();
        }
        playerTasks.put(key, task);
    }
    public boolean hasTask(String key){
        return playerTasks.containsKey(key);
    }
    public void cancelTask(String key){
        if(playerTasks.containsKey(key)){
            playerTasks.get(key).cancel();
            playerTasks.remove(key);
        }
    }
    public void cancelAllTasks(){
        playerTasks.forEach((k, v)-> v.cancel());
        playerTasks.clear();
    }

    public void addEntity(String key, Entity entity){
        if(playerEntities.containsKey(key)){
            playerEntities.get(key).remove();
        }
        playerEntities.put(key, entity);
    }
    public Entity getEntity(String key){
        if(playerEntities.containsKey(key)) return playerEntities.get(key);
        return null;
    }
    public HashMap<String, Entity> getEntities(){
        return playerEntities;
    }
    public void removeEntity(String key){
        if(playerEntities.containsKey(key)){
            playerEntities.get(key).remove();
            playerEntities.remove(key);
        }
    }
    public void removeAllEntities(){
        playerEntities.forEach((k, v)-> v.remove());
        playerEntities.clear();
    }

    public void applySpeedPassive(){
        double movementSpeed = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(movementSpeed*1.1);
    }

    public boolean checkLevel(int level, ClassGameType type){
        return checkLevel(level, type, false);
    }

    // Determine if player level is high enough to cast a spell
    public boolean checkLevel(int level, ClassGameType type, boolean silent){
        if(getLevelFromClassType(type) < level){
            if(!silent) player.sendMessage(ChatColor.GRAY + "You do not understand how to use this spell yet.");
            return false;
        } else {
            return true;
        }
    }

    // Determine if player has enough mana to cast a spell and subtract mana if possible
    public boolean subtractMana(int manaCost){
        if(manaCost <= playerCurrentMana){
            playerCurrentMana -= manaCost;
            manaRegenTask.cancel();
            addPlayerManaRegenTasks();
            return true;
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Not enough mana."));
            return false;
        }
    }

    public void addMana(int mana){
        playerCurrentMana = Math.min(playerCurrentMana+mana, plugin.getMaxMana()[playerManaLevel]);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE + "Mana: " + playerCurrentMana + "/" + plugin.getMaxMana()[playerManaLevel]));
    }

    public void levelUpMana(int level){
        if(playerManaLevel < level){
            playerManaLevel = level;
        }
    }

    public void levelUp(ClassGameType type){
        switch(type){
            case SWORDSMAN -> swordsLevel++;
            case MAGIC -> magicLevel++;
            case REAPER -> reaperLevel++;
            case ARCHERY -> archeryLevel++;
        }
    }

    public void setLevel(ClassGameType type, int level){
        switch(type){
            case SWORDSMAN -> swordsLevel = level;
            case MAGIC -> magicLevel = level;
            case REAPER -> reaperLevel = level;
            case ARCHERY -> archeryLevel = level;
        }
    }

    public int getLevelFromClassType(ClassGameType type){
        return switch(type){
            case SWORDSMAN -> swordsLevel;
            case MAGIC -> magicLevel;
            case REAPER -> reaperLevel;
            case ARCHERY -> archeryLevel;
        };
    }

    public void setSilenced(boolean silenced){
        this.silenced = silenced;
    }
    public boolean isNotSilenced(){
        if(silenced){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "You are silenced!"));
        }
        return !silenced;
    }

    public void addLectrum(int amount){
        int lectrum = data.getConfig().getInt("players."+player.getUniqueId().toString()+".lectrum");
        lectrum += amount;
        data.getConfig().set("players."+player.getUniqueId().toString()+".lectrum", lectrum);
        data.saveConfig();
        plugin.leaderboard.updateNetWorth(player.getUniqueId().toString());
    }
    public void removeLectrum(int amount){
        int lectrum = data.getConfig().getInt("players."+player.getUniqueId().toString()+".lectrum");
        lectrum -= amount;
        data.getConfig().set("players."+player.getUniqueId().toString()+".lectrum", lectrum);
        data.saveConfig();
        plugin.leaderboard.updateNetWorth(player.getUniqueId().toString());
    }

    public int getLectrum(){
        return data.getConfig().getInt("players."+player.getUniqueId().toString()+".lectrum");
    }
    public void addToReaperRefreshCooldown(String spell, BukkitTask task){
        refreshReaperCooldown.put(spell,task);
    }
    public void removeFromReaperRefreshCooldown(String spell){refreshReaperCooldown.remove(spell);}
    public void addToTinkerRefreshCooldown(String spell, BukkitTask task){
        refreshTinkerCooldown.put(spell,task);
    }
    public void removeFromTinkerRefreshCooldown(String spell){refreshTinkerCooldown.remove(spell);}

    public int getMasteryLevel(String key){
        if(magicMasteryLevel.containsKey(key)) return magicMasteryLevel.get(key);
        return 0;
    }
    public void setMasteryLevel(String key, int level){
        magicMasteryLevel.put(key, level);
    }

    public Player getPlayer(){
        return player;
    }
}
