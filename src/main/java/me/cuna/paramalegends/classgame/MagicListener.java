package me.cuna.paramalegends.classgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.magic.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;

public class MagicListener implements Listener {

    private final ParamaLegends plugin;
    public DataManager data;
    private final int[] masteryLevelUp = {0,100,200,400,600,800, Integer.MAX_VALUE};

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
    public String[] spellNames = {
            "flingearth",
            "ignite",
            "gust",
            "lifedrain",
            "blink",
            "summonlightning",
            "illusoryorb",
            "dragonbreath",
            "voicesofthedamned",
            "nova",
    };
    public ArrayList<String> spellNamesFormatted = new ArrayList<>(Arrays.asList(
            ChatColor.COLOR_CHAR+"5Fling Earth",
            ChatColor.COLOR_CHAR+"5Ignite",
            ChatColor.COLOR_CHAR+"5Gust",
            ChatColor.COLOR_CHAR+"5Life Drain",
            ChatColor.COLOR_CHAR+"5Blink",
            ChatColor.COLOR_CHAR+"5Summon Lightning",
            ChatColor.COLOR_CHAR+"5Illusory Orb",
            ChatColor.COLOR_CHAR+"5Dragon's Breath",
            ChatColor.COLOR_CHAR+"5Voices of the Damned",
            ChatColor.COLOR_CHAR+"5Nova"
    ));


    public MagicListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;

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
        //Check if held item is spell
        ItemStack item = event.getItem();
        if(item.getItemMeta() != null){
            //Call cast spell function according to name
            switch(item.getItemMeta().getDisplayName()){
                case ChatColor.COLOR_CHAR+"5Fling Earth":
                    if(player.isNotSilenced())
                        flingEarth.castSpell(player);
                    break;
                case ChatColor.COLOR_CHAR+"5Ignite":
                    if(player.checkLevel(2, ClassGameType.MAGIC) && player.isNotSilenced()){
                        ignite.castSpell(player);
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Gust":
                    if(player.checkLevel(3, ClassGameType.MAGIC) && player.isNotSilenced()){
                        if (event.getAction().equals(Action.LEFT_CLICK_AIR)
                                || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                            gust.castSpellSelf(player);
                        } else {
                            gust.castSpell(player);
                        }
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Life Drain":
                    if(player.checkLevel(4, ClassGameType.MAGIC) && player.isNotSilenced()){
                        lifeDrain.castSpell(player);
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Blink":
                    if(player.checkLevel(5, ClassGameType.MAGIC) && player.isNotSilenced()){
                        blink.castSpell(player);
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Summon Lightning":
                    if(player.checkLevel(6, ClassGameType.MAGIC) && player.isNotSilenced()){
                        summonLightning.castSpell(player);
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Illusory Orb":
                    if(player.checkLevel(7, ClassGameType.MAGIC) && player.isNotSilenced()){
                        illusoryOrb.castSpell(player);
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Dragon's Breath":
                    if(player.checkLevel(8, ClassGameType.MAGIC) && player.isNotSilenced()){
                        dragonBreath.castSpell(player);
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Voices of the Damned":
                    if(player.checkLevel(9, ClassGameType.MAGIC) && player.isNotSilenced()){
                        voicesOfTheDamned.castSpell(player);
                    }
                    break;
                case ChatColor.COLOR_CHAR+"5Nova":
                    if(player.checkLevel(10, ClassGameType.MAGIC) && player.isNotSilenced()){
                        nova.castSpell(player);
                    }
                    break;
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

    public BoundingBox getBoxInFrontOfLocation(Location location, Vector direction, int scale){
        Location start = location.clone().add(new Vector(-0.7*direction.getZ(), 0, 0.7*direction.getX()));
        Location end = location.clone().add((new Vector(0.7*direction.getZ(), 0, -0.7*direction.getX())));
        end.add(direction.clone().multiply(scale));
        //expand boxes y value
        end.add(0, 2, 0);
        start.add(0, -2 ,0);

        return new BoundingBox(
                start.getX(), start.getY(), start.getZ(),
                end.getX(), end.getY(), end.getZ());
    }

    public int[] getMasteryLevelUp() { return masteryLevelUp; }
    public String[] getSpellNames(){
        return spellNames;
    }
}
