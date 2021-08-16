package id.cuna.ParamaLegends.ClassListener.ClassTypeListener;

import id.cuna.ParamaLegends.ClassListener.ClassListener;
import id.cuna.ParamaLegends.ClassType;
import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.ParamaLegends;
import id.cuna.ParamaLegends.Spells.Magic.*;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class MagicListener extends ClassListener implements Listener {

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
        super(plugin, ClassType.MAGIC);
        this.plugin = plugin;
        data = plugin.getData();

        //Initialize all spell instances
        flingEarth = new FlingEarth(plugin, this);
        ignite = new Ignite(plugin, this);
        gust = new Gust(plugin, this);
        lifeDrain = new LifeDrain(plugin, this);
        blink = new Blink(plugin, this);
        summonLightning = new SummonLightning(plugin, this);
        illusoryOrb = new IllusoryOrb(plugin, this);
        dragonBreath = new DragonBreath(plugin, this);
        voicesOfTheDamned = new VoicesOfTheDamned(plugin, this);
        nova = new Nova(plugin, this);
    }

    //When player right clicks a spell
    @EventHandler
    public void onCastSpell(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        //Check if held item is book
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType().equals(Material.ENCHANTED_BOOK)){
            if(item.getItemMeta() != null){
                //Call cast spell function according to book name
                switch(item.getItemMeta().getDisplayName()){
                    case "§5Fling Earth":
                        if(!plugin.isSilenced(player))
                            flingEarth.castFlingEarth(player);
                        break;
                    case "§5Ignite":
                        if(checkLevel(player, 2) && !plugin.isSilenced(player)){
                            ignite.castIgnite(player);
                        }
                        break;
                    case "§5Gust":
                        if(checkLevel(player, 3) && !plugin.isSilenced(player)){
                            gust.castGust(player);
                        }
                        break;
                    case "§5Life Drain":
                        if(checkLevel(player, 4) && !plugin.isSilenced(player)){
                            lifeDrain.castLifeDrain(player);
                        }
                        break;
                    case "§5Blink":
                        if(checkLevel(player, 5) && !plugin.isSilenced(player)){
                            blink.castBlink(player);
                        }
                        break;
                    case "§5Summon Lightning":
                        if(checkLevel(player, 6) && !plugin.isSilenced(player)){
                            summonLightning.castSummonLightning(player);
                        }
                        break;
                    case "§5Illusory Orb":
                        if(checkLevel(player, 7) && !plugin.isSilenced(player)){
                            illusoryOrb.castIllusoryOrb(player);
                        }
                        break;
                    case "§5Dragon's Breath":
                        if(checkLevel(player, 8) && !plugin.isSilenced(player)){
                            dragonBreath.castDragonBreath(player);
                        }
                        break;
                    case "§5Voices of the Damned":
                        if(checkLevel(player, 9) && !plugin.isSilenced(player)){
                            voicesOfTheDamned.castVoicesOfTheDamned(player);
                        }
                        break;
                    case "§5Nova":
                        if(checkLevel(player, 10) && !plugin.isSilenced(player)){
                            nova.castNova(player);
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
