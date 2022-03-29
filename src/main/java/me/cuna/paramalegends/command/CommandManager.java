package me.cuna.paramalegends.command;

import me.cuna.paramalegends.ParamaLegends;

public class CommandManager {

    public ParamaLegends plugin;
    public StartGame startGame;
    public SetupGame setupGame;
    public Lectrum lectrum;
    public SpawnAltar spawnAltar;
    public RemoveAltar removeAltar;
    public Destiny destiny;
    public DestinySet destinySet;
    public LectrumSet lectrumSet;
    public WorldLevelSet worldLevelSet;
    public WhatsNew whatsNew;
    public BloodMoon bloodMoon;
    public DragonEnd dragonEnd;
    public SetMayor setMayor;
    public SetSafeZoneSize safeZone;
    public Leaderboard leaderboard;
    public Tip tip;
    public Party party;

    public CommandManager(ParamaLegends plugin){
        this.plugin = plugin;

        startGame = new StartGame(plugin);
        setupGame = new SetupGame(plugin);
        lectrum = new Lectrum(plugin);
        destiny = new Destiny(plugin);
        destinySet = new DestinySet(plugin);
        lectrumSet = new LectrumSet(plugin);
        whatsNew = new WhatsNew(plugin);
        spawnAltar = new SpawnAltar(plugin);
        removeAltar = new RemoveAltar(plugin);
        worldLevelSet = new WorldLevelSet(plugin);
        bloodMoon = new BloodMoon(plugin);
        dragonEnd = new DragonEnd(plugin);
        setMayor = new SetMayor(plugin);
        safeZone = new SetSafeZoneSize(plugin);
        leaderboard = new Leaderboard(plugin);
        tip = new Tip(plugin);
        party = new Party(plugin);
        
        setCommandExecutors();
    }
    
    public void setCommandExecutors(){
        plugin.getCommand("startgame").setExecutor(startGame);
        plugin.getCommand("setupgame").setExecutor(setupGame);
        plugin.getCommand("lectrum").setExecutor(lectrum);
        plugin.getCommand("destiny").setExecutor(destiny);
        plugin.getCommand("spawnaltar").setExecutor(spawnAltar);
        plugin.getCommand("removealtar").setExecutor(removeAltar);
        plugin.getCommand("destinyset").setExecutor(destinySet);
        plugin.getCommand("lectrumset").setExecutor(lectrumSet);
        plugin.getCommand("worldlevelset").setExecutor(worldLevelSet);
        plugin.getCommand("whatsnew").setExecutor(whatsNew);
        plugin.getCommand("bloodmoon").setExecutor(bloodMoon);
        plugin.getCommand("dragonfightstop").setExecutor(dragonEnd);
        plugin.getCommand("setmayor").setExecutor(setMayor);
        plugin.getCommand("safezone").setExecutor(safeZone);
        plugin.getCommand("leaderboard").setExecutor(leaderboard);
        plugin.getCommand("tip").setExecutor(tip);
        plugin.getCommand("party").setExecutor(party);
    }
}
