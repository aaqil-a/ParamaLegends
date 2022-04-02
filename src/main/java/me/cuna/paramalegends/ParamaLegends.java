package me.cuna.paramalegends;

import me.cuna.paramalegends.altar.AltarManager;
import me.cuna.paramalegends.armor.ArmorManager;
import me.cuna.paramalegends.boss.BossManager;
import me.cuna.paramalegends.command.*;
import me.cuna.paramalegends.food.FoodManager;
import me.cuna.paramalegends.alcohol.AlcoholManager;
import me.cuna.paramalegends.game.*;
import me.cuna.paramalegends.leaderboard.LeaderboardManager;
import me.cuna.paramalegends.leaderboard.NetWorth;
import me.cuna.paramalegends.party.PartyManager;
import me.cuna.paramalegends.shopgame.*;
import me.cuna.paramalegends.classgame.GameClassManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class ParamaLegends extends JavaPlugin {

    public DataManager dataManager;
    public AlcoholManager alcoholManager;
    public PartyManager partyManager;
    public ShopManager shopManager;
    public GameClassManager gameClassManager;
    public FoodManager foodManager;
    public CommandManager commandManager;
    public BossManager bossManager;
    public ArmorManager armorManager;
    public AltarManager altarManager;
    public GameManager gameManager;
    public PlayerManager playerManager;
    public LeaderboardManager leaderboard;
    public Recipes recipes;

    @Override
    public void onEnable() {
        //Initalize all managers
        dataManager = new DataManager(this);
        gameManager = new GameManager(this);
        gameClassManager = new GameClassManager(this);
        foodManager = new FoodManager(this);
        commandManager = new CommandManager(this);
        bossManager = new BossManager(this);
        armorManager = new ArmorManager(this);
        altarManager = new AltarManager(this);
        partyManager = new PartyManager(this);
        playerManager = new PlayerManager(this);
        alcoholManager = new AlcoholManager(this);
        shopManager = new ShopManager(this);
        leaderboard = new LeaderboardManager(this);
        recipes = new Recipes(this);

        //Start saving player's data every minute
        Bukkit.getScheduler().runTaskTimer(this, dataManager::saveAllPlayerData, 1200, 1200);
    }

    public PlayerParama getPlayerParama(Player player){
        return playerManager.getPlayerParama(player);
    }
}
