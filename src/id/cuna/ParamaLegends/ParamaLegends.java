package id.cuna.ParamaLegends;

import id.cuna.ParamaLegends.BossListener.AltarTypeListener.NatureAltarListener;
import id.cuna.ParamaLegends.BossListener.AltarTypeListener.StartAltarListener;
import id.cuna.ParamaLegends.BossListener.BossTypeListener.RaidListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.MagicListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.Command.CommandStartGame;
import id.cuna.ParamaLegends.Command.CommandYourMom;
import id.cuna.ParamaLegends.GameListener.*;
import id.cuna.ParamaLegends.NPCListener.*;
import id.cuna.ParamaLegends.NPCListener.NPCShop.*;
import org.bukkit.ChatColor;
import org.bukkit.World;
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
    public RaidListener raidListener;
    public DamageModifyingListener damageModifyingListener;

    public ReaperListener reaperListener;
    public SwordsmanListener swordsmanListener;
    public ArcheryListener archeryListener;
    public MagicListener magicListener;

    public NPCShopListener banishedMagus;
    public NPCShopListener oddReseller;
    public NPCShopListener seniorRanger;
    public NPCShopListener retiredWeaponsmith;
    public NPCShopListener suspiciousPeasant;

    public StartAltarListener startAltarListener;
    public NatureAltarListener natureAltarListener;

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
        commandStartGame = new CommandStartGame(this);
        raidListener = new RaidListener(this);
        damageModifyingListener = new DamageModifyingListener(this);

        initializeNPCShop();
        initializeGameClass();
        initializeAltars();

        getCommand("yourmom").setExecutor(new CommandYourMom());
        getCommand("startgame").setExecutor(commandStartGame);
        getServer().getPluginManager().registerEvents(mobSpawnListener, this);
        getServer().getPluginManager().registerEvents(wiseOldManListener, this);
        getServer().getPluginManager().registerEvents(destinyListener, this);
        getServer().getPluginManager().registerEvents(worldRuleListener, this);
        getServer().getPluginManager().registerEvents(experienceListener, this);
        getServer().getPluginManager().registerEvents(raidListener, this);
        getServer().getPluginManager().registerEvents(damageModifyingListener, this);

        registerNPCShopListener();
        registerGameClass();
        registerSpells();
        registerAltars();

    }

    public void initializeNPCShop(){
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

    public void initializeGameClass(){
        magicListener = new MagicListener(this);
        reaperListener = new ReaperListener(this);;
        swordsmanListener = new SwordsmanListener(this);
        archeryListener = new ArcheryListener(this);
    }

    public void registerGameClass(){
        getServer().getPluginManager().registerEvents(magicListener, this);
        getServer().getPluginManager().registerEvents(reaperListener, this);
        getServer().getPluginManager().registerEvents(swordsmanListener, this);
        getServer().getPluginManager().registerEvents(archeryListener, this);
    }

    public void registerSpells(){
        getServer().getPluginManager().registerEvents(archeryListener.blast, this);
        getServer().getPluginManager().registerEvents(archeryListener.hunterEye, this);
        getServer().getPluginManager().registerEvents(archeryListener.neurotoxin, this);
        getServer().getPluginManager().registerEvents(archeryListener.royalArtillery, this);
        getServer().getPluginManager().registerEvents(archeryListener.totsukaCreation, this);
        getServer().getPluginManager().registerEvents(archeryListener.viperBite, this);
        getServer().getPluginManager().registerEvents(archeryListener.whistlingWind, this);

        getServer().getPluginManager().registerEvents(magicListener.flingEarth, this);
        getServer().getPluginManager().registerEvents(magicListener.illusoryOrb, this);
        getServer().getPluginManager().registerEvents(magicListener.voicesOfTheDamned, this);

        getServer().getPluginManager().registerEvents(reaperListener.blindingSand, this);

        getServer().getPluginManager().registerEvents(swordsmanListener.shieldsUp, this);
        getServer().getPluginManager().registerEvents(swordsmanListener.superconducted, this);
        getServer().getPluginManager().registerEvents(swordsmanListener.terrifyingCruelty, this);
    }

    public void initializeAltars(){
        natureAltarListener = new NatureAltarListener(this);
        startAltarListener = new StartAltarListener(this);
    }

    public void registerAltars(){
        getServer().getPluginManager().registerEvents(startAltarListener, this);
        getServer().getPluginManager().registerEvents(natureAltarListener, this);
    }


    @Override
    public void onDisable() {
    }

    public void spawnBossAltar(World world){
        switch(data.getConfig().getInt("world.level")){
            case 1 -> natureAltarListener.createAltarLocation(world);
        }
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