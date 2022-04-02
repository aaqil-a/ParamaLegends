package me.cuna.paramalegends;

import me.cuna.paramalegends.classgame.ClassGameType;
import me.cuna.paramalegends.party.Party;
import me.cuna.paramalegends.shopgame.ShopGUI;
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
    private final HashMap<String, Integer> magicMasteryLevel = new HashMap<>();
    private final HashMap<String, Integer> magicMasteryExp = new HashMap<>();
    private final DataManager data;
    private int playerCurrentMana;
    private int playerManaLevel;
    private int magicLevel = 1;
    private int magicExp = 0;
    private int swordsLevel = 1;
    private int swordsExp = 0;
    private int archeryLevel = 1;
    private int archeryExp = 0;
    private int reaperLevel = 1;
    private int reaperExp = 0;
    private int lectrum = 50;
    private Party party = null;
    private Party partyInvited = null;
    private ShopGUI openShopGui = null;
    private boolean displayingMessage = false;

    public PlayerParama(ParamaLegends plugin, Player player){
        this.plugin = plugin;
        this.data = plugin.dataManager;
        this.player = player;

        if (!data.getConfig().contains("players." + player.getUniqueId())){
            data.getConfig().set("players." + player.getUniqueId() + ".joined", 1);
            data.getConfig().set("players." + player.getUniqueId() + ".lectrum", 50);
            data.getConfig().set("players." + player.getUniqueId() + ".swordsmanship", 1);
            data.getConfig().set("players." + player.getUniqueId() + ".swordsmanshipexp", 0);
            data.getConfig().set("players." + player.getUniqueId() + ".archery", 1);
            data.getConfig().set("players." + player.getUniqueId() + ".archeryexp", 0);
            data.getConfig().set("players." + player.getUniqueId() + ".magic", 1);
            data.getConfig().set("players." + player.getUniqueId() + ".magicexp", 0);
            data.getConfig().set("players." + player.getUniqueId() + ".reaper", 1);
            data.getConfig().set("players." + player.getUniqueId() + ".reaperexp", 0);
            data.saveConfig();
        } else {
            magicLevel = data.getConfig().getInt("players." + player.getUniqueId() + ".magic");
            swordsLevel = data.getConfig().getInt("players." + player.getUniqueId() + ".swordsmanship");
            archeryLevel = data.getConfig().getInt("players." + player.getUniqueId() + ".archery");
            reaperLevel = data.getConfig().getInt("players." + player.getUniqueId() + ".reaper");
            magicExp = data.getConfig().getInt("players." + player.getUniqueId() + ".magicexp");
            swordsExp = data.getConfig().getInt("players." + player.getUniqueId() + ".swordsmanshipexp");
            archeryExp = data.getConfig().getInt("players." + player.getUniqueId() + ".archeryexp");
            reaperExp = data.getConfig().getInt("players." + player.getUniqueId() + ".reaperexp");
            lectrum = data.getConfig().getInt("players." + player.getUniqueId() + ".lectrum");
        }

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
        for(String spell : plugin.gameClassManager.magic.getSpellNames()){
            int masteryLevel = data.getConfig().getInt("players."+player.getUniqueId()+".mastery."+spell);
            int masteryExp = data.getConfig().getInt("players."+player.getUniqueId()+".masteryexp."+spell);
            magicMasteryLevel.put(spell, masteryLevel);
            magicMasteryExp.put(spell, masteryExp);
        }
    }

    public void addPlayerManaRegenTasks() {
        //Create task to regenerate mana over time
        manaRegenTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (playerCurrentMana < plugin.playerManager.getMaxMana()[playerManaLevel]) {
                playerCurrentMana += plugin.playerManager.getManaRegen()[playerManaLevel];
                if (playerCurrentMana > plugin.playerManager.getMaxMana()[playerManaLevel])
                    playerCurrentMana = plugin.playerManager.getMaxMana()[playerManaLevel];
                if(!this.displayingMessage){
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE + "Mana: " + playerCurrentMana + "/" + plugin.playerManager.getMaxMana()[playerManaLevel]));
                }
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
        if(getClassLevel(type) < level){
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
            displayActionBarMessage(ChatColor.RED + "Not enough mana.");
            return false;
        }
    }

    public void addMana(int mana){
        playerCurrentMana = Math.min(playerCurrentMana+mana, plugin.playerManager.getMaxMana()[playerManaLevel]);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.BLUE + "Mana: " + playerCurrentMana + "/" + plugin.playerManager.getMaxMana()[playerManaLevel]));
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

    /**
     * Returns the player's class level corresponding
     * to a given class.
     * @param type The class
     * @return The player's class level
     */
    public int getClassLevel(ClassGameType type){
        return switch(type){
            case SWORDSMAN -> swordsLevel;
            case MAGIC -> magicLevel;
            case REAPER -> reaperLevel;
            case ARCHERY -> archeryLevel;
        };
    }

    /**
     * Returns the player's class exp corresponding
     * to a given class.
     * @param type The class
     * @return The player's class exp
     */
    public int getClassExp(ClassGameType type){
        return switch(type){
            case SWORDSMAN -> swordsExp;
            case MAGIC -> magicExp;
            case REAPER -> reaperExp;
            case ARCHERY -> archeryExp;
        };
    }

    /**
     * Sets the player's class level
     * @param type The class
     * @param level The level to set to
     */
    public void setClassLevel(ClassGameType type, int level){
        switch(type){
            case SWORDSMAN -> swordsLevel = level;
            case MAGIC -> magicLevel = level;
            case REAPER -> reaperLevel = level;
            case ARCHERY -> archeryLevel = level;
        };
        data.toSave.add(this);
    }

    /**
     * Sets the player's class exp
     * @param type The class
     * @param exp The amount of exp
     */
    public void setClassExp(ClassGameType type, int exp){
        switch(type){
            case SWORDSMAN -> swordsExp = exp;
            case MAGIC -> magicExp = exp;
            case REAPER -> reaperExp = exp;
            case ARCHERY -> archeryExp = exp;
        };
        data.toSave.add(this);
    }

    public void addMastery(String spellName, int exp){
        int masteryLevel = getMasteryLevel(spellName);
        if(masteryLevel == 0){
            magicMasteryLevel.put(spellName, 1);
            magicMasteryExp.put(spellName, exp);
        } else {
            int masteryExp = magicMasteryExp.get(spellName);
            masteryExp += exp;

            // level up
            if(masteryExp >= plugin.gameClassManager.magic.getMasteryLevelUp()[masteryLevel]){
                masteryLevel += 1;
                masteryExp = 0;

                player.sendMessage(ChatColor.GOLD+
                        switch(spellName){
                            case "dragonbreath" -> "Dragon's Breath";
                            case "flingearth" -> "Fling Earth";
                            case "illusoryorb" -> "Illusory Orb";
                            case "lifedrain" -> "Life Drain";
                            case "summonlightning" -> "Summon Lightning";
                            case "voicesofthedamned" -> "Voices of The Damned";
                            default -> spellName.substring(0,1).toUpperCase()+spellName.substring(1);
                        }
                        +" mastery leveled up to "+(masteryLevel));
                magicMasteryLevel.put(spellName, masteryLevel);
            }
            magicMasteryExp.put(spellName, masteryExp);
        }
        data.toSave.add(this);
    }

    public void setSilenced(boolean silenced){
        this.silenced = silenced;
    }
    public boolean isNotSilenced(){
        if(silenced){
            displayActionBarMessage(ChatColor.RED + "You are silenced!");
        }
        return !silenced;
    }

    public void addLectrum(int amount){
        lectrum += amount;
        plugin.leaderboard.netWorthCriteria.updateNetWorth(player.getUniqueId().toString());
        data.toSave.add(this);
    }
    public void removeLectrum(int amount){
        lectrum -= amount;
        plugin.leaderboard.netWorthCriteria.updateNetWorth(player.getUniqueId().toString());
        data.toSave.add(this);
    }
    public void setLectrum(int value){
        lectrum = value;
        data.toSave.add(this);
    }


    public int getLectrum(){
        return lectrum;
    }
    public void addToReaperRefreshCooldown(String spell, BukkitTask task){
        refreshReaperCooldown.put(spell,task);
    }
    public void removeFromReaperRefreshCooldown(String spell){refreshReaperCooldown.remove(spell);}

    public int getMasteryLevel(String key){
        if(magicMasteryLevel.containsKey(key)) return magicMasteryLevel.get(key);
        return 0;
    }
    public int getMasteryExp(String key){
        if(magicMasteryExp.containsKey(key)) return magicMasteryExp.get(key);
        return 0;
    }

    public Player getPlayer(){
        return player;
    }

    public boolean hasParty(){
        return party != null;
    }

    public Party getParty(){
        return party;
    }

    public void setParty(Party party){
        this.party = party;
        if(party != null){
            party.getMembers().add(this);
        }
    }

    public boolean hasPartyInvited(){
        return partyInvited != null;
    }

    public Party getPartyInvited(){
        return partyInvited;
    }

    public void setPartyInvited(Party party){
        this.partyInvited = party;
    }

    public void setOpenShopGui(ShopGUI gui){
        this.openShopGui = gui;
    }

    public ShopGUI getOpenShopGui(){
        return this.openShopGui;
    }

    public void displayActionBarMessage(String message){
        this.displayingMessage = true;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        Bukkit.getScheduler().runTaskLater(plugin, ()->{
            this.displayingMessage = false;
        }, 60);
    }
}
