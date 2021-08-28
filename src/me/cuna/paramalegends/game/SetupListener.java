package me.cuna.paramalegends.game;

import me.cuna.paramalegends.boss.BossType;
import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetupListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    private BossType markType;
    private boolean verify;
    private final List<Biome> validNatureBiomes = new ArrayList<>(Arrays.asList(Biome.PLAINS, Biome.SAVANNA, Biome.FOREST, Biome.BIRCH_FOREST));

    public SetupListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        verify = false;
    }

    //Disable usage of certain game items
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(event.getItem() != null && event.getItem().hasItemMeta()){
            ItemMeta meta = event.getItem().getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals("§6Game Wand")){
                event.setCancelled(true);
                if(verify){
                    spawnAltar(event.getPlayer(), event.getClickedBlock());
                    verify = false;
                } else {
                    markLocation(event.getPlayer(), event.getClickedBlock());
                }
            }
        }
    }

    public void setMarkType(BossType type){
        markType = type;
    }

    private String path;
    public void markLocation(Player player, Block clicked){
        if(plugin.commandSetupGame.isCurrentlySettingUp() && clicked != null){
            //determine config.yml path for marked location type
            path = switch(markType){
                case START -> "world.startlocation";
                case NATURE -> "world.naturelocation";
                case EARTH -> "world.earthlocation";
                case WATER -> "world.waterlocation";
                case FIRE -> "world.firelocation";
                case VOID -> "world.voidlocation";
            };

            switch(markType){
                case NATURE->{
                    if(!validNatureBiomes.contains(clicked.getBiome())){
                        player.sendMessage(ChatColor.RED+"Invalid biome for nature area.");
                        return;
                    }
                }
                case EARTH->{
                    if(clicked.getLocation().getY() > 40){
                        player.sendMessage(ChatColor.RED+"Altar must be located at Y=40 or below.");
                        return;
                    }
                }
            }

            List<Integer> position = new ArrayList<>(Arrays.asList(clicked.getX(), clicked.getY(), clicked.getZ()));

            //save location to game config
            data.getConfig().set(path, position);
            data.saveConfig();

            verify = true;
            player.sendMessage(ChatColor.GREEN+"Mark block again to confirm.");

        } else if(clicked != null) {
            player.sendMessage(ChatColor.RED+"Game is not currently being setup.");
        }
    }

    public void spawnAltar(Player player, Block clicked){
        List<Integer> altarLocation = data.getConfig().getIntegerList(path);
        int altarX = altarLocation.get(0);
        int altarY = altarLocation.get(1);
        int altarZ = altarLocation.get(2);
        if(clicked == null || (clicked.getX() != altarX) || (clicked.getY() != altarY) || (clicked.getZ() != altarZ)){
            player.sendMessage(ChatColor.GREEN+"Mark location cancelled.");
        } else {
            switch(markType){
                case START -> {
                    player.sendMessage(ChatColor.GREEN+"Starting area marked.");

                    plugin.startAltarListener.spawnAltar(player.getWorld(), altarX, altarY, altarZ);

                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        player.sendMessage(ChatColor.GOLD+"Mark nature game area with wand.");
                        player.sendMessage(ChatColor.GOLD+"Valid Biomes: Plains, Savanna, Forest and Birch Forest.");
                    }, 20);
                    markType = BossType.NATURE;
                }
                case NATURE -> {
                    Bukkit.getScheduler().runTaskLater(plugin, ()->
                            plugin.natureAltarListener.spawnAltar(player.getWorld(), altarX, altarY, altarZ), 20);
                    player.sendMessage(ChatColor.GREEN+"Nature area marked.");
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        player.sendMessage(ChatColor.GOLD+"Mark earth game area with wand.");
                        player.sendMessage(ChatColor.GOLD+"Valid Biomes: Mountains and Gravelly Mountains.");
                    }, 20);
                    markType = BossType.EARTH;
                }
                case EARTH -> {
                    Bukkit.getScheduler().runTaskLater(plugin, ()->
                            plugin.earthAltarListener.spawnAltar(player.getWorld(), altarX, altarY, altarZ), 20);
                    player.sendMessage(ChatColor.GREEN+"Earth area marked.");
                    Bukkit.getScheduler().runTaskLater(plugin, ()->{
                        player.sendMessage(ChatColor.GOLD+"Mark water game area with wand.");
                        player.sendMessage(ChatColor.GOLD+"Valid Biomes: your mom.");
                    }, 20);
                    markType = BossType.WATER;
                    finishSetup(player);
                }
                case VOID -> finishSetup(player);
            }
        }
    }

    public void finishSetup(Player player){
        plugin.commandSetupGame.setCurrentlySettingUp(false);
        data.getConfig().set("world.level", 1);
        player.sendMessage(ChatColor.GREEN+"Parama Legends setup complete. Begin game with /startgame.");
        player.sendMessage(ChatColor.GRAY+"It is recommended to set spawn-protection in server.properties to 0.");
    }
    
}
