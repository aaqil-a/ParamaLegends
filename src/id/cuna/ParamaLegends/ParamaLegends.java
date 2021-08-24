package id.cuna.ParamaLegends;

import id.cuna.ParamaLegends.BossListener.AltarTypeListener.EarthAltarListener;
import id.cuna.ParamaLegends.BossListener.AltarTypeListener.NatureAltarListener;
import id.cuna.ParamaLegends.BossListener.AltarTypeListener.StartAltarListener;
import id.cuna.ParamaLegends.BossListener.BossFightListener.RaidFightListener;
import id.cuna.ParamaLegends.BossListener.BossSummonListener.RaidSummonListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ArcheryListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.MagicListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.ReaperListener;
import id.cuna.ParamaLegends.ClassListener.ClassTypeListener.SwordsmanListener;
import id.cuna.ParamaLegends.Command.*;
import id.cuna.ParamaLegends.FunListener.AlcoholListener;
import id.cuna.ParamaLegends.FunListener.AlcoholRecipes;
import id.cuna.ParamaLegends.FunListener.WineryListener;
import id.cuna.ParamaLegends.GameListener.*;
import id.cuna.ParamaLegends.NPCListener.*;
import id.cuna.ParamaLegends.NPCListener.NPCShop.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
    public DamageModifyingListener damageModifyingListener;
    public SetupListener setupListener;

    public ReaperListener reaperListener;
    public SwordsmanListener swordsmanListener;
    public ArcheryListener archeryListener;
    public MagicListener magicListener;

    public NPCShopListener banishedMagus;
    public NPCShopListener seniorRanger;
    public NPCShopListener retiredWeaponsmith;
    public NPCShopListener oddWares;
    public NPCShopListener suspiciousPeasant;

    public StartAltarListener startAltarListener;
    public NatureAltarListener natureAltarListener;
    public EarthAltarListener earthAltarListener;

    public RaidSummonListener raidSummonListener;

    public RaidFightListener raidFightListener;

    public WineryListener wineryListener;
    public AlcoholListener alcoholListener;

    public CommandStartGame commandStartGame;
    public CommandSetupGame commandSetupGame;
    public CommandLectrum commandLectrum;
    public CommandSpawnAltar commandSpawnAltar;
    public CommandRemoveAltar commandRemoveAltar;
    public CommandDestiny commandDestiny;
    public CommandDestinySet commandDestinySet;
    public CommandLectrumSet commandLectrumSet;
    public CommandWorldLevelSet commandWorldLevelSet;

    public Recipes recipes;
    public AlcoholRecipes alcoholRecipes;

    public final List<Player> playersSilenced = new ArrayList<Player>();

    @Override
    public void onEnable() {
        data = new DataManager(this);
        mobSpawnListener = new MobSpawnListener(this);
        destinyListener = new PlayerJoinListener(this);
        worldRuleListener = new WorldRuleListener(this);
        experienceListener = new ExperienceListener(this);
        wiseOldManListener = new WiseOldManListener(this);
        commandStartGame = new CommandStartGame(this);
        commandSetupGame = new CommandSetupGame(this);
        commandLectrum = new CommandLectrum(this);
        commandDestiny = new CommandDestiny(this);
        commandDestinySet = new CommandDestinySet(this);
        commandLectrumSet = new CommandLectrumSet(this);
        commandSpawnAltar = new CommandSpawnAltar(this);
        commandRemoveAltar = new CommandRemoveAltar(this);
        commandWorldLevelSet = new CommandWorldLevelSet(this);
        damageModifyingListener = new DamageModifyingListener(this);
        wineryListener = new WineryListener(this);
        setupListener = new SetupListener(this);
        recipes = new Recipes(this);
        alcoholRecipes = new AlcoholRecipes(this);
        alcoholListener = new AlcoholListener(this);

        initializeNPCShop();
        initializeGameClass();
        initializeAltars();
        initializeSummons();
        initializeBossFights();

        getCommand("yourmom").setExecutor(new CommandYourMom());
        getCommand("startgame").setExecutor(commandStartGame);
        getCommand("setupgame").setExecutor(commandSetupGame);
        getCommand("lectrum").setExecutor(commandLectrum);
        getCommand("destiny").setExecutor(commandDestiny);
        getCommand("spawnaltar").setExecutor(commandSpawnAltar);
        getCommand("removealtar").setExecutor(commandRemoveAltar);
        getCommand("destinyset").setExecutor(commandDestinySet);
        getCommand("lectrumset").setExecutor(commandLectrumSet);
        getCommand("worldlevelset").setExecutor(commandWorldLevelSet);
        getServer().getPluginManager().registerEvents(mobSpawnListener, this);
        getServer().getPluginManager().registerEvents(wiseOldManListener, this);
        getServer().getPluginManager().registerEvents(destinyListener, this);
        getServer().getPluginManager().registerEvents(worldRuleListener, this);
        getServer().getPluginManager().registerEvents(experienceListener, this);
        getServer().getPluginManager().registerEvents(damageModifyingListener, this);
        getServer().getPluginManager().registerEvents(setupListener, this);
        getServer().getPluginManager().registerEvents(wineryListener, this);
        getServer().getPluginManager().registerEvents(alcoholListener, this);

        registerNPCShopListener();
        registerGameClass();
        registerSpells();
        registerAltars();
        registerSummons();
        registerBossFights();

    }

    public void initializeSummons(){
        raidSummonListener = new RaidSummonListener(this);
    }

    public void registerSummons(){
        getServer().getPluginManager().registerEvents(raidSummonListener, this);
    }

    public void initializeBossFights(){
        raidFightListener = new RaidFightListener(this);
    }

    public void registerBossFights(){
        getServer().getPluginManager().registerEvents(raidFightListener, this);
    }

    public void initializeNPCShop(){
        banishedMagus = new BanishedMagus(this);
        seniorRanger = new SeniorRanger(this);
        retiredWeaponsmith = new RetiredWeaponsmith(this);
        suspiciousPeasant = new SuspiciousPeasant(this);
        oddWares = new OddWares(this);
    }

    public void registerNPCShopListener(){
        getServer().getPluginManager().registerEvents(banishedMagus, this);
        getServer().getPluginManager().registerEvents(seniorRanger, this);
        getServer().getPluginManager().registerEvents(retiredWeaponsmith, this);
        getServer().getPluginManager().registerEvents(suspiciousPeasant, this);
        getServer().getPluginManager().registerEvents(oddWares, this);
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
        earthAltarListener = new EarthAltarListener(this);
    }

    public void registerAltars(){
        getServer().getPluginManager().registerEvents(startAltarListener, this);
        getServer().getPluginManager().registerEvents(natureAltarListener, this);
        getServer().getPluginManager().registerEvents(earthAltarListener, this);
    }


    @Override
    public void onDisable() {
    }

    public boolean isSilenced(Player player){
        if(!playersSilenced.contains(player)) {
            return false;
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_RED + "You are silenced!"));
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
    public void levelUpReaper(Player player){
        reaperListener.levelUp(player);
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

    public ClassType checkCustomDamageSource(double damage){
        String damageString = String.valueOf(damage);
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            return switch (key) {
                case ".069", ".068" -> ClassType.MAGIC;
                case ".034", ".033", ".035" -> ClassType.REAPER;
                case ".072", ".073", ".071" -> ClassType.SWORDSMAN;
                case ".016", ".015", ".017" -> ClassType.ARCHERY;
                default -> null;
            };

        }
        return null;
    }

    public DataManager getData(){
        return data;
    }

    public List<Player> getPlayersSilenced(){
        return playersSilenced;
    }

}
