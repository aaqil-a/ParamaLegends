package me.cuna.ParamaLegends.ClassListener;

import me.cuna.ParamaLegends.ClassType;
import me.cuna.ParamaLegends.DataManager;
import me.cuna.ParamaLegends.ParamaLegends;
import me.cuna.ParamaLegends.PlayerParama;

import me.cuna.ParamaLegends.Spells.Magic.*;
import org.bukkit.block.BlockFace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class MagicListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;

    public final FlingEarth flingEarth;
    public final Ignite ignite;
    public final Gust gust;
    public final LifeDrain lifeDrain;
    public final Blink blink;
    public final SummonLightning summonLightning;
    public final IllusoryOrb illusoryOrb;
    public final DragonBreath dragonBreath;
    public final VoicesOfTheDamned voicesOfTheDamned;
    public final Nova nova;

    public MagicListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.getData();

        //Initialize all spell instances
        flingEarth = new FlingEarth(plugin);
        ignite = new Ignite(plugin);
        gust = new Gust(plugin);
        lifeDrain = new LifeDrain(plugin);
        blink = new Blink(plugin);
        summonLightning = new SummonLightning(plugin);
        illusoryOrb = new IllusoryOrb(plugin);
        dragonBreath = new DragonBreath(plugin);
        voicesOfTheDamned = new VoicesOfTheDamned(plugin);
        nova = new Nova(plugin);
    }

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        PlayerParama player = plugin.getPlayerParama(event.getPlayer());
        if(event.getItem() == null){
            return;
        }
        if(event.getAction() == Action.PHYSICAL){
            return;
        }
        //Check if held item is book
        ItemStack item = event.getItem();
        if (item.getType().equals(Material.ENCHANTED_BOOK)){
            if(item.getItemMeta() != null){
                //Call cast spell function according to book name
                switch(item.getItemMeta().getDisplayName()){
                    case "§5Fling Earth":
                        if(player.isNotSilenced())
                            flingEarth.castSpell(player);
                        break;
                    case "§5Ignite":
                        if(player.checkLevel(2, ClassType.MAGIC) && player.isNotSilenced()){
                            ignite.castSpell(player);
                        }
                        break;
                    case "§5Gust":
                        if(player.checkLevel(3, ClassType.MAGIC) && player.isNotSilenced()){
                            gust.castSpell(player);
                        }
                        break;
                    case "§5Life Drain":
                        if(player.checkLevel(4, ClassType.MAGIC) && player.isNotSilenced()){
                            lifeDrain.castSpell(player);
                        }
                        break;
                    case "§5Blink":
                        if(player.checkLevel(5, ClassType.MAGIC) && player.isNotSilenced()){
                            blink.castSpell(player);
                        }
                        break;
                    case "§5Summon Lightning":
                        if(player.checkLevel(6, ClassType.MAGIC) && player.isNotSilenced()){
                            summonLightning.castSpell(player);
                        }
                        break;
                    case "§5Illusory Orb":
                        if(player.checkLevel(7, ClassType.MAGIC) && player.isNotSilenced()){
                            illusoryOrb.castSpell(player);
                        }
                        break;
                    case "§5Dragon's Breath":
                        if(player.checkLevel(8, ClassType.MAGIC) && player.isNotSilenced()){
                            dragonBreath.castSpell(player);
                        }
                        break;
                    case "§5Voices of the Damned":
                        if(player.checkLevel(9, ClassType.MAGIC) && player.isNotSilenced()){
                            voicesOfTheDamned.castSpell(player);
                        }
                        break;
                    case "§5Nova":
                        if(player.checkLevel(10, ClassType.MAGIC) && player.isNotSilenced()){
                            nova.castSpell(player);
                        }
                        break;
                }
            }
        }
    }

    //Create firework explosion at given location
    public void createFireworkEffect(Location location, Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BURST)
                .flicker(false).trail(false).withColor(Color.WHITE, Color.YELLOW).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.setSilent(true);
        Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 1);
    }

    public void teleportToAir(Player player, Location location, Vector direction){
        teleportToAir(player, location.setDirection(direction));
    }

    //Teleports player to a safe area near a location if exists
    public void teleportToAir(Player player, Location location){
        if(location.getBlock().getType().isAir()){
            player.teleport(location);
        } else if(location.getBlock().getRelative(BlockFace.UP).getType().isAir()) {
            player.teleport(location.add(0,1,0));
        } else if(location.getBlock().getRelative(BlockFace.DOWN).getType().isAir()){
            player.teleport(location.add(0,-1,0));
        } else if(location.getBlock().getRelative(BlockFace.NORTH).getType().isAir()){
            player.teleport(location.add(0,0,-1));
        } else if(location.getBlock().getRelative(BlockFace.SOUTH).getType().isAir()){
            player.teleport(location.add(0,0,1));
        } else if(location.getBlock().getRelative(BlockFace.WEST).getType().isAir()){
            player.teleport(location.add(-1,0,0));
        } else if(location.getBlock().getRelative(BlockFace.EAST).getType().isAir()){
            player.teleport(location.add(1,0,0));
        }
    }
}
