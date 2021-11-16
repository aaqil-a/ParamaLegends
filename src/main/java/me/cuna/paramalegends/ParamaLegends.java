package me.cuna.paramalegends;

import me.cuna.paramalegends.altar.EarthAltarListener;
import me.cuna.paramalegends.altar.NatureAltarListener;
import me.cuna.paramalegends.altar.StartAltarListener;
import me.cuna.paramalegends.armor.SanguineListener;
import me.cuna.paramalegends.boss.fight.BloodMoonListener;
import me.cuna.paramalegends.boss.fight.DragonFightListener;
import me.cuna.paramalegends.boss.fight.NatureFightListener;
import me.cuna.paramalegends.boss.fight.RaidFightListener;
import me.cuna.paramalegends.boss.summon.BloodMoonSummonListener;
import me.cuna.paramalegends.boss.summon.NatureSummonListener;
import me.cuna.paramalegends.boss.summon.RaidSummonListener;
import me.cuna.paramalegends.classgame.*;
import me.cuna.paramalegends.command.*;
import me.cuna.paramalegends.command.Destiny;
import me.cuna.paramalegends.food.FoodListener;
import me.cuna.paramalegends.food.FoodRecipes;
import me.cuna.paramalegends.fun.AlcoholListener;
import me.cuna.paramalegends.fun.AlcoholRecipes;
import me.cuna.paramalegends.fun.WineryListener;
import me.cuna.paramalegends.game.*;
import me.cuna.paramalegends.lib.armorequip.ArmorListener;
import me.cuna.paramalegends.lib.armorequip.DispenserArmorListener;
import me.cuna.paramalegends.shopgame.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class ParamaLegends extends JavaPlugin {

    public DataManager data;
    public MobSpawnListener mobSpawnListener;
    public me.cuna.paramalegends.shopgame.Destiny wiseOldManListener;
    public PlayerManagerListener playerManagerListener;
    public WorldRuleListener worldRuleListener;
    public ExperienceListener experienceListener;
    public DamageModifyingListener damageModifyingListener;
    public SetupListener setupListener;
    public PlayerShopListener playerShopListener;

    public ReaperListener reaperListener;
    public SwordsmanListener swordsmanListener;
    public ArcheryListener archeryListener;
    public MagicListener magicListener;
    public TinkerListener tinkerListener;

    public GameShop banishedMagus;
    public GameShop seniorRanger;
    public GameShop retiredWeaponsmith;
    public GameShop oddWares;
    public GameShop suspiciousPeasant;

    public StartAltarListener startAltarListener;
    public NatureAltarListener natureAltarListener;
    public EarthAltarListener earthAltarListener;

    public RaidSummonListener raidSummonListener;
    public NatureSummonListener natureSummonListener;
    public BloodMoonSummonListener bloodMoonSummonListener;

    public RaidFightListener raidFightListener;
    public NatureFightListener natureFightListener;
    public DragonFightListener dragonFightListener;
    public BloodMoonListener bloodMoonListener;

    public WineryListener wineryListener;
    public AlcoholListener alcoholListener;

    public SanguineListener sanguineListener;

    public StartGame startGame;
    public SetupGame setupGame;
    public Lectrum lectrum;
    public SpawnAltar spawnAltar;
    public RemoveAltar removeAltar;
    public Destiny commandDestiny;
    public DestinySet destinySet;
    public LectrumSet lectrumSet;
    public WorldLevelSet worldLevelSet;
    public WhatsNew whatsNew;
    public Mastery mastery;
    public Tinker tinker;
    public BloodMoon bloodMoon;
    public DragonEnd dragonEnd;
    public SetMayor setMayor;
    public SetSafeZoneSize safeZone;

    public Recipes recipes;
    public AlcoholRecipes alcoholRecipes;
    public FoodRecipes foodRecipes;
    public FoodListener foodListener;

    private final int[] maxMana = {0,50,100,150,200,250,300,400,500,600,800};
    private final int[] manaRegen = {0,1,2,2,3,3,4,5,6,7,8};

    @Override
    public void onEnable() {
        data = new DataManager(this);
        mobSpawnListener = new MobSpawnListener(this);
        playerManagerListener = new PlayerManagerListener(this);
        worldRuleListener = new WorldRuleListener(this);
        experienceListener = new ExperienceListener(this);
        wiseOldManListener = new me.cuna.paramalegends.shopgame.Destiny(this);
        playerShopListener = new PlayerShopListener(this);
        startGame = new StartGame(this);
        setupGame = new SetupGame(this);
        lectrum = new Lectrum(this);
        commandDestiny = new Destiny(this);
        destinySet = new DestinySet(this);
        lectrumSet = new LectrumSet(this);
        whatsNew = new WhatsNew(this);
        spawnAltar = new SpawnAltar(this);
        removeAltar = new RemoveAltar(this);
        worldLevelSet = new WorldLevelSet(this);
        damageModifyingListener = new DamageModifyingListener(this);
        wineryListener = new WineryListener(this);
        setupListener = new SetupListener(this);
        recipes = new Recipes(this);
        foodRecipes = new FoodRecipes(this);
        foodListener = new FoodListener(this);
        alcoholRecipes = new AlcoholRecipes(this);
        alcoholListener = new AlcoholListener(this);
        mastery = new Mastery(this);
        tinker = new Tinker(this);
        bloodMoon = new BloodMoon(this);
        dragonEnd = new DragonEnd(this);
        setMayor = new SetMayor(this);
        safeZone = new SetSafeZoneSize(this);

        initializeNPCShop();
        initializeGameClass();
        initializeAltars();
        initializeSummons();
        initializeBossFights();
        initializeArmor();

        getCommand("yourmom").setExecutor(new YourMom());
        getCommand("startgame").setExecutor(startGame);
        getCommand("setupgame").setExecutor(setupGame);
        getCommand("lectrum").setExecutor(lectrum);
        getCommand("destiny").setExecutor(commandDestiny);
        getCommand("spawnaltar").setExecutor(spawnAltar);
        getCommand("removealtar").setExecutor(removeAltar);
        getCommand("destinyset").setExecutor(destinySet);
        getCommand("lectrumset").setExecutor(lectrumSet);
        getCommand("worldlevelset").setExecutor(worldLevelSet);
        getCommand("whatsnew").setExecutor(whatsNew);
        getCommand("mastery").setExecutor(mastery);
        getCommand("tinker").setExecutor(tinker);
        getCommand("bloodmoon").setExecutor(bloodMoon);
        getCommand("dragonfightstop").setExecutor(dragonEnd);
        getCommand("setmayor").setExecutor(setMayor);
        getCommand("safezone").setExecutor(safeZone);
        getServer().getPluginManager().registerEvents(mobSpawnListener, this);
        getServer().getPluginManager().registerEvents(wiseOldManListener, this);
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

        //initialize armor events
        Bukkit.getPluginManager().registerEvents(new ArmorListener(new ArrayList<>()), this);
        Bukkit.getPluginManager().registerEvents(new DispenserArmorListener(), this);
    }

    public void initializeSummons(){
        raidSummonListener = new RaidSummonListener(this);
        natureSummonListener = new NatureSummonListener(this);
        bloodMoonSummonListener = new BloodMoonSummonListener(this);
    }

    public void registerSummons(){
        getServer().getPluginManager().registerEvents(raidSummonListener, this);
    }

    public void initializeBossFights(){
        raidFightListener = new RaidFightListener(this);
        natureFightListener = new NatureFightListener(this);
        dragonFightListener = new DragonFightListener(this);
        bloodMoonListener = new BloodMoonListener(this);
    }

    public void registerBossFights(){
        getServer().getPluginManager().registerEvents(raidFightListener, this);
    }

    public void initializeNPCShop(){
        banishedMagus = new MagicShop(this);
        seniorRanger = new ArcheryShop(this);
        retiredWeaponsmith = new SwordsmanShop(this);
        suspiciousPeasant = new ReaperShop(this);
        oddWares = new GeneralShop(this);
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
        tinkerListener = new TinkerListener(this);
    }

    public void registerGameClass(){
        getServer().getPluginManager().registerEvents(magicListener, this);
        getServer().getPluginManager().registerEvents(reaperListener, this);
        getServer().getPluginManager().registerEvents(swordsmanListener, this);
        getServer().getPluginManager().registerEvents(archeryListener, this);
    }

    public void registerSpells(){
        getServer().getPluginManager().registerEvents(archeryListener.blast, this);
        getServer().getPluginManager().registerEvents(archeryListener.neurotoxin, this);
        getServer().getPluginManager().registerEvents(archeryListener.retreat, this);
        getServer().getPluginManager().registerEvents(archeryListener.royalArtillery, this);
        getServer().getPluginManager().registerEvents(archeryListener.totsukaCreation, this);
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

    public void initializeArmor(){
        sanguineListener = new SanguineListener(this);
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

    public ClassGameType checkCustomDamageSource(double damage){
        String damageString = String.valueOf(damage);
        if(damageString.substring(damageString.indexOf(".")).length() >= 4){
            String key = damageString.substring(damageString.indexOf("."),damageString.indexOf(".")+4);
            return switch (key) {
                case ".069", ".068" -> ClassGameType.MAGIC;
                case ".034", ".033", ".035" -> ClassGameType.REAPER;
                case ".072", ".073", ".071" -> ClassGameType.SWORDSMAN;
                case ".016", ".015", ".017" -> ClassGameType.ARCHERY;
                default -> null;
            };

        }
        return null;
    }

    public PlayerParama getPlayerParama(Player player){
        return playerManagerListener.getPlayerParama(player);
    }

    public void sendCooldownMessage(PlayerParama player, String spell){
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_PURPLE + spell + ChatColor.GRAY + " is on cooldown."));
    }
    public void sendNoLongerCooldownMessage(PlayerParama player, String spell){
        player.getPlayer().sendMessage(ChatColor.DARK_PURPLE + spell + ChatColor.DARK_GREEN + " is no longer on cooldown.");
    }
    public void sendOutOfRangeMessage(PlayerParama player){
        player.getPlayer().sendMessage(ChatColor.GRAY + "Out of range to cast spell.");
    }

    public int[] getMaxMana(){
        return maxMana;
    }
    public int[] getManaRegen(){
        return manaRegen;
    }
    public DataManager getData(){
        return data;
    }
}
