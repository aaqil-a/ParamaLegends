package me.cuna.paramalegends.classgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import me.cuna.paramalegends.PlayerParama;
import me.cuna.paramalegends.spell.SpellParama;
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
    public String[] spellNames = {
            "dragonbreath",
            "flingearth",
            "gust",
            "ignite",
            "illusoryorb",
            "lifedrain",
            "nova",
            "summonlightning",
            "voicesofthedamned"
    };

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
                        gust.castSpell(player);
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

    //mastery listener
    private final int[] masteryLevelUp = {0,100,200,400,600,800, Integer.MAX_VALUE};
    public void addMastery(PlayerParama playerParama, String spellName, int exp){
        Player player = playerParama.getPlayer();
        int masteryLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+".mastery."+spellName);
        if(masteryLevel == 0){
            data.getConfig().set("players."+player.getUniqueId().toString()+".mastery."+spellName, 1);
            data.getConfig().set("players."+player.getUniqueId().toString()+".masteryexp."+spellName, exp);
        } else {
            int masteryExp = data.getConfig().getInt("players."+player.getUniqueId().toString()+".masteryexp."+spellName);
            masteryExp += exp;

            // level up
            if(masteryExp >= masteryLevelUp[masteryLevel]){
                data.getConfig().set("players."+player.getUniqueId().toString()+".mastery."+spellName, masteryLevel+1);
                data.getConfig().set("players."+player.getUniqueId().toString()+".masteryexp."+spellName, 0);
                playerParama.setMasteryLevel(spellName, masteryLevel+1);

                player.sendMessage(ChatColor.GOLD+
                        switch(spellName){
                            case "dragonbreath" -> "Dragon's Breath";
                            case "flingearth" -> "Fling Earth";
                            case "illusoryorb" -> "Illusory Orb";
                            case "lifedrain" -> "Life Drain";
                            case "summonlightning" -> "Summon Lightning";
                            case "voicesofthedamned" -> "Voices of The Damned";
                            default -> spellName.substring(0,1).toUpperCase()+spellName.substring(1);
                        }
                        +" mastery leveled up to "+(masteryLevel+1));
            } else {
                // no level up
                data.getConfig().set("players."+player.getUniqueId().toString()+".masteryexp."+spellName, masteryExp);
            }
        }
        data.saveConfig();
    }

    public String[] getSpellNames(){
        return spellNames;
    }
}
