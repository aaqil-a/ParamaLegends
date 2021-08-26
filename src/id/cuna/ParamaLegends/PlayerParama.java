package id.cuna.ParamaLegends;

import id.cuna.ParamaLegends.Spells.AttackParama;
import id.cuna.ParamaLegends.Spells.SpellParama;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerParama {

    private final ParamaLegends plugin;
    private final DataManager data;
    private final Player player;
    private BukkitTask manaRegenTask;
    private boolean silenced = false;
    private final List<SpellParama> spellOnCooldown = new ArrayList<>();
    private final List<AttackParama> attackOnCooldown = new ArrayList<>();
    private int playerCurrentMana;
    private int playerManaLevel;
    private int magicLevel;
    private int swordsLevel;
    private int archeryLevel;
    private int reaperLevel;

    public PlayerParama(ParamaLegends plugin, Player player){
        this.plugin = plugin;
        this.data = plugin.getData();
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
        player.sendMessage(ChatColor.GOLD+"Please report any bugs you may find or give feedback thanks");

        //set archery speed passive
        if(archeryLevel >= 4){
            applySpeedPassive();
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


    public void applySpeedPassive(){
        double movementSpeed = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(movementSpeed*1.1);
    }

    public boolean checkLevel(int level, ClassType type){
        return checkLevel(level, type, false);
    }

    // Determine if player level is high enough to cast a spell
    public boolean checkLevel(int level, ClassType type, boolean silent){
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

    public void levelUpMana(int level){
        if(playerManaLevel < level){
            playerManaLevel = level;
        }
    }

    public void levelUp(ClassType type){
        switch(type){
            case SWORDSMAN -> swordsLevel++;
            case MAGIC -> magicLevel++;
            case REAPER -> reaperLevel++;
            case ARCHERY -> archeryLevel++;
        };
    }

    public int getLevelFromClassType(ClassType type){
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

    public Player getPlayer(){
        return player;
    }
}
