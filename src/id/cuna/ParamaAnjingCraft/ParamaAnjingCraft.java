package id.cuna.ParamaAnjingCraft;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public final List<Player> playersSilenced = new ArrayList<Player>();

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
    }

    @Override
    public void onDisable() {
    }

    public void levelUpMagic(Player player){
        magicListener.levelUp(player);
    }

    public DataManager getData(){
        return data;
    }

    public List<Player> getPlayersSilenced(){
        return playersSilenced;
    }

}
