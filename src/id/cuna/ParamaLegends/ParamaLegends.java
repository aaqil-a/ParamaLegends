package id.cuna.ParamaLegends;

import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ArcheryListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.MagicListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.ReaperListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListeners.SwordsmanListener;
import id.cuna.ParamaLegends.Command.CommandStartGame;
import id.cuna.ParamaLegends.Command.CommandYourMom;
import id.cuna.ParamaLegends.GameListener.*;
import id.cuna.ParamaLegends.NPCListener.*;
import id.cuna.ParamaLegends.NPCListener.NPCShop.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ParamaLegends extends JavaPlugin {

    public DataManager data;
    public MobSpawnListener mobSpawnListener;
    public WiseOldManListener wiseOldManListener;
    public PlayerJoinListener destinyListener;
    public WorldRuleListener worldRuleListener;
    public ExperienceListener experienceListener;
    public CommandStartGame commandStartGame;
    public ReaperListener reaperListener;
    public SwordsmanListener swordsmanListener;
    public ArcheryListener archeryListener;
    public RaidListener raidListener;
    public DamageModifyingListener damageModifyingListener;
    public MagicListener magicListener;

    public NPCShopListener banishedMagus;
    public NPCShopListener oddReseller;
    public NPCShopListener seniorRanger;
    public NPCShopListener retiredWeaponsmith;
    public NPCShopListener suspiciousPeasant;

    public final List<Player> playersSilenced = new ArrayList<Player>();
    private boolean isNight = false;

    @Override
    public void onEnable() {
        data = new DataManager(this);
        mobSpawnListener = new MobSpawnListener(this);
        wiseOldManListener = new WiseOldManListener(this);
        destinyListener = new PlayerJoinListener(this);
        worldRuleListener = new WorldRuleListener(this);
        experienceListener = new ExperienceListener(this);
        magicListener = new MagicListener(this);
        commandStartGame = new CommandStartGame(this);
        reaperListener = new ReaperListener(this);;
        swordsmanListener = new SwordsmanListener(this);
        archeryListener = new ArcheryListener(this);
        raidListener = new RaidListener(this);
        damageModifyingListener = new DamageModifyingListener(this);

        intializeNPCShop();

        getCommand("yourmom").setExecutor(new CommandYourMom());
        getCommand("startgame").setExecutor(commandStartGame);
        getServer().getPluginManager().registerEvents(mobSpawnListener, this);
        getServer().getPluginManager().registerEvents(wiseOldManListener, this);
        getServer().getPluginManager().registerEvents(destinyListener, this);
        getServer().getPluginManager().registerEvents(worldRuleListener, this);
        getServer().getPluginManager().registerEvents(experienceListener, this);
        getServer().getPluginManager().registerEvents(magicListener, this);
        getServer().getPluginManager().registerEvents(reaperListener, this);
        getServer().getPluginManager().registerEvents(swordsmanListener, this);
        getServer().getPluginManager().registerEvents(archeryListener, this);
        getServer().getPluginManager().registerEvents(raidListener, this);
        getServer().getPluginManager().registerEvents(damageModifyingListener, this);


        registerNPCShopListener();

        startNightCheck();

    }

    public void intializeNPCShop(){
        banishedMagus = new BanishedMagus(this);
        oddReseller = new OddReseller(this);
        seniorRanger = new SeniorRanger(this);
        retiredWeaponsmith = new RetiredWeaponsmith(this);
        suspiciousPeasant = new SuspiciousPeasant(this);
    }

    public void registerNPCShopListener(){
        getServer().getPluginManager().registerEvents(banishedMagus, this);
        getServer().getPluginManager().registerEvents(oddReseller, this);
        getServer().getPluginManager().registerEvents(seniorRanger, this);
        getServer().getPluginManager().registerEvents(retiredWeaponsmith, this);
        getServer().getPluginManager().registerEvents(suspiciousPeasant, this);
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
            if(player != null) {
                if (player.getWorld().getTime() > 13500 && !isNight) {
                    Bukkit.broadcastMessage("Raid begin!");
                    startRaid(player);
                    isNight = true;
                }
            }
        }, 0, 100);
    }

    public void startRaid(Player player){
        this.raidListener.startRaid(player);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Bukkit.broadcastMessage("Raid end!");
            isNight = false;
        }, 10000);
    }


    public boolean isSilenced(Player player){
        if(!playersSilenced.contains(player)) {
            return false;
        } else {
            player.sendMessage(ChatColor.DARK_RED + "You are silenced!");
            return true;
        }
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
