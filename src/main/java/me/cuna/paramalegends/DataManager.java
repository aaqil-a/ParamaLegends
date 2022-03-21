package me.cuna.paramalegends;

import me.cuna.paramalegends.classgame.ClassGameType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class DataManager {

    private ParamaLegends plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    public final List<PlayerParama> toSave = new ArrayList<>();

    public DataManager(ParamaLegends plugin){
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), "config.yml");
        dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource("config.yml");
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig(){
        if (dataConfig == null)
            reloadConfig();
        return dataConfig;
    }
    public void saveConfig() {
        if (dataConfig == null || configFile == null)
            return;
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save ParamaLegends config to " + configFile, e);
        }
    }
    public void saveDefaultConfig(){
        if(configFile == null)
            configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }


    /**
     * Save all players inside toSave
     * Lectrum, Levels, Experience,
     * and Mastery to the config.yml file.
     */
    public void saveAllPlayerData(){
        for(PlayerParama playerParama : toSave){
            savePlayerData(playerParama);
        }
        toSave.clear();
        saveConfig();
    }

    /**
     * Save a player's Lectrum, Levels, Experience,
     * and Mastery to the config.yml file.
     */
    public void savePlayerData(PlayerParama playerParama){
        Player player = playerParama.getPlayer();
        getConfig().set("players." + player.getUniqueId() + ".lectrum", playerParama.getLectrum());
        getConfig().set("players." + player.getUniqueId()+ ".swordsmanship", playerParama.getClassLevel(ClassGameType.SWORDSMAN));
        getConfig().set("players." + player.getUniqueId() + ".swordsmanshipexp", playerParama.getClassExp(ClassGameType.SWORDSMAN));
        getConfig().set("players." + player.getUniqueId() + ".archery", playerParama.getClassLevel(ClassGameType.ARCHERY));
        getConfig().set("players." + player.getUniqueId() + ".archeryexp", playerParama.getClassExp(ClassGameType.ARCHERY));
        getConfig().set("players." + player.getUniqueId() + ".magic", playerParama.getClassLevel(ClassGameType.MAGIC));
        getConfig().set("players." + player.getUniqueId() + ".magicexp", playerParama.getClassExp(ClassGameType.MAGIC));
        getConfig().set("players." + player.getUniqueId() + ".reaper", playerParama.getClassLevel(ClassGameType.REAPER));
        getConfig().set("players." + player.getUniqueId() + ".reaperexp", playerParama.getClassExp(ClassGameType.REAPER));

        //set mastery levels
        for(String spell : plugin.gameClassManager.magic.getSpellNames()){
            getConfig().set("players."+player.getUniqueId()+".mastery."+spell, playerParama.getMasteryLevel(spell));
            getConfig().set("players."+player.getUniqueId()+".masteryexp."+spell, playerParama.getMasteryExp(spell));
        }

        saveConfig();
    }

}
