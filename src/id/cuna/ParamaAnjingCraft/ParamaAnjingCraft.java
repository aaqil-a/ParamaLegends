package id.cuna.ParamaAnjingCraft;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.arch.Processor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ParamaAnjingCraft extends JavaPlugin {

    public DataManager data;
    public MobSpawnListener mobSpawnListener;
    public WiseOldManListener wiseOldManListener;
    public DestinyListener destinyListener;
    public OddResellerListener oddResellerListener;
    public WorldRuleListener worldRuleListener;
    public ExperienceListener experienceListener;
    public BanishedMagusListener banishedMagusListener;
    public MagicListener magicListener;
    public CommandStartGame commandStartGame;
    public SuspiciousPeasantListener suspiciousPeasantListener;
    public ReaperListener reaperListener;
    public RetiredWeaponsmithListener retiredWeaponsmithListener;
    public SwordsmanListener swordsmanListener;
    public SeniorRangerListener seniorRangerListener;
    public ArcheryListener archeryListener;

    public final List<Player> playersSilenced = new ArrayList<Player>();
    private boolean isNight = false;
    private boolean playerOnline = false;

    @Override
    public void onEnable() {
        data = new DataManager(this);
        mobSpawnListener = new MobSpawnListener(this);
        wiseOldManListener = new WiseOldManListener(this);
        destinyListener = new DestinyListener(this);
        oddResellerListener = new OddResellerListener(this);
        worldRuleListener = new WorldRuleListener(this);
        experienceListener = new ExperienceListener(this);
        banishedMagusListener = new BanishedMagusListener(this);
        magicListener = new MagicListener(this);
        commandStartGame = new CommandStartGame(this);
        suspiciousPeasantListener = new SuspiciousPeasantListener(this);
        reaperListener = new ReaperListener(this);
        retiredWeaponsmithListener = new RetiredWeaponsmithListener(this);
        swordsmanListener = new SwordsmanListener(this);
        seniorRangerListener = new SeniorRangerListener(this);
        archeryListener = new ArcheryListener(this);

        getCommand("yourmom").setExecutor(new CommandYourMom());
        getCommand("startgame").setExecutor(commandStartGame);
        getServer().getPluginManager().registerEvents(mobSpawnListener, this);
        getServer().getPluginManager().registerEvents(wiseOldManListener, this);
        getServer().getPluginManager().registerEvents(destinyListener, this);
        getServer().getPluginManager().registerEvents(oddResellerListener, this);
        getServer().getPluginManager().registerEvents(worldRuleListener, this);
        getServer().getPluginManager().registerEvents(experienceListener, this);
        getServer().getPluginManager().registerEvents(banishedMagusListener, this);
        getServer().getPluginManager().registerEvents(magicListener, this);
        getServer().getPluginManager().registerEvents(suspiciousPeasantListener, this);
        getServer().getPluginManager().registerEvents(reaperListener, this);
        getServer().getPluginManager().registerEvents(retiredWeaponsmithListener, this);
        getServer().getPluginManager().registerEvents(swordsmanListener, this);
        getServer().getPluginManager().registerEvents(seniorRangerListener, this);
        getServer().getPluginManager().registerEvents(archeryListener, this);

        startNightCheck();

    }

    @Override
    public void onDisable() {
    }

    public void startNightCheck(){
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Player player = null;
            for(Player players: Bukkit.getOnlinePlayers()){
                player = players;
            }
            if(player == null) {
                playerOnline = false;
            } else {
                playerOnline = true;
                if (player.getWorld().getTime() > 13500 && !isNight) {
                    Bukkit.broadcastMessage("Raid begin!");
                    startRaid(player);
                    isNight = true;
                }
            }
        }, 0, 100);
    }

    public void startRaid(Player player){
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.broadcastMessage("Raid end!");
            isNight = false;
        }, 0, 10000);
    }

    public void levelUpMagic(Player player){
        magicListener.levelUp(player);
    }
    public void levelUpSwordsmanship(Player player){
        swordsmanListener.levelUp(player);
    }
    public void levelUpArchery(Player player){
        archeryListener.levelUp(player);
    }

    public double increasedIncomingDamage(double damage, double multiplier){
        String damageString = String.valueOf(damage);
        double toAdd = 0;
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            switch (key) {
                case ".069", ".068" -> toAdd = 0.069;
                case ".034", ".033", ".035" -> toAdd = 0.034;
                case ".072", ".073", ".071" -> toAdd = 0.072;
                case ".016", ".015", ".017" -> toAdd = 0.016;
            }
        }
        return damage*multiplier+toAdd;
    }
    public DataManager getData(){
        return data;
    }

    public List<Player> getPlayersSilenced(){
        return playersSilenced;
    }

}
