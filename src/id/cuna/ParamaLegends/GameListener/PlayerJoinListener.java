package id.cuna.ParamaLegends.GameListener;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerJoinListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;

    private final int maxDepth;
    private final int[] maxMana = {0,50,100,150,200,250,300,400,500,600,800};
    private final int[] manaRegen = {0,1,1,2,3,3,4,5,6,7,8};
    private final HashMap<String, Integer> playerCurrentLevel = new HashMap<String, Integer>();
    private final HashMap<Player, BukkitTask> playerManaRegenTasks = new HashMap<>();
    private final List<Player> playerInsideDepths = new ArrayList<Player>();

    public PlayerJoinListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();
        maxDepth = data.getConfig().getInt("world.maxdepth");
    }

    //Start mana regen tasks and set default data of exp
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if (!data.getConfig().contains("players." + player.getUniqueId().toString())){
            data.getConfig().set("players." + player.getUniqueId().toString() + ".joined", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".lectrum", 50);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".swordsmanship", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".swordsmanshipexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".archery", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".archeryexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".magic", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".magicexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".mining", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".miningexp", 0);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".reaper", 1);
            data.getConfig().set("players." + player.getUniqueId().toString() + ".reaperexp", 0);
            data.saveConfig();
        }

        int magicLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".magic");
        int swordsLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".swordsmanship");
        int archeryLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".archery");
        int reaperLevel =data.getConfig().getInt("players." + player.getUniqueId().toString() + ".reaper");
        int manaLevel = Math.max(Math.max(magicLevel,swordsLevel),Math.max(archeryLevel,reaperLevel));
        player.setExp(0);
        if(playerCurrentLevel.containsKey(player.getUniqueId().toString())){
            player.setLevel(playerCurrentLevel.get(player.getUniqueId().toString()));
        } else {
            player.setLevel(maxMana[manaLevel]);
        }
        //Create task to regenerate mana over time
        playerManaRegenTasks.put(player,
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    int curMana = player.getLevel();
                    if(curMana < maxMana[manaLevel]){
                        curMana += manaRegen[manaLevel] ;
                        if(curMana > maxMana[manaLevel])
                            curMana = maxMana[manaLevel];
                        player.setExp(0);
                        player.setLevel(curMana);
                        playerCurrentLevel.put(player.getUniqueId().toString(), curMana);
                    }
                    if(player.getInventory().getItemInOffHand().getType().equals(Material.SHIELD)){
                        ItemStack item = player.getInventory().getItemInOffHand();
                        player.getInventory().setItemInOffHand(null);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY + "The shield feels much to heavy to use on one hand.");
                    }
                }, 0, 20)
        );
    }
}
