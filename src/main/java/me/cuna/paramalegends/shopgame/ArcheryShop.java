package me.cuna.paramalegends.shopgame;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArcheryShop extends GameShop {

    private final ParamaLegends plugin;

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<Integer, Integer>(){{
        put(2,1);
        put(4,3);
        put(6,40);
        put(8,80);
        put(10,10);
        put(12,200);
        put(14,15);
        put(16,300);
        put(20,20);
        put(22,400);
        put(24,400);
    }};

    public ArcheryShop(ParamaLegends plugin) {
        super(plugin, "§aFletcher's Table");
        this.plugin = plugin;
    }

    //Get prices
    @Override
    public HashMap<Integer, Integer> getPrices(){
        return prices;
    }

    //Attack player when npc attacked according to npc type
    @Override
    public void NPCAttack(Player player, Entity npc){
        player.getWorld().playSound(npc.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 0f);
        Arrow arrow1 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(3,0,3), EntityType.ARROW);
        arrow1.setVelocity(new Vector(-0.5, 0, -0.5));
        arrow1.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Arrow arrow2 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(0,4,0), EntityType.ARROW);
        arrow2.setVelocity(new Vector(0,-1,0));
        arrow2.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Arrow arrow3 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(-3,0,3), EntityType.ARROW);
        arrow3.setVelocity(new Vector(0.5,0,-0.5));
        arrow3.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Arrow arrow4 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(3,0,-3), EntityType.ARROW);
        arrow4.setVelocity(new Vector(-0.5,0,0.5));
        arrow4.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Arrow arrow5 = (Arrow) player.getWorld().spawnEntity(player.getEyeLocation().add(-3,0,-3), EntityType.ARROW);
        arrow5.setVelocity(new Vector(0.5,0,0.5));
        arrow5.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            arrow1.remove();
            arrow2.remove();
            arrow3.remove();
            arrow4.remove();
            arrow5.remove();
            player.damage(10, npc);
        }, 6);
    }

    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        int playerLevel = data.getConfig().getInt("players."+player.getUniqueId().toString()+".archery");
        Inventory gui;
        gui = Bukkit.createInventory(null,27, "§aRanger Gear");

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(0, item);
        lore.clear();

        //Hunter's Eye
        ItemStack tippedArrowItem = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta tippedArrowMeta = (PotionMeta) tippedArrowItem.getItemMeta();
        tippedArrowMeta.setColor(Color.YELLOW);
        tippedArrowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aHunter's Eye");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Tag an enemy, revealing them");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "for some time and increasing");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "its incoming damage.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.hunterEye.getManaCost());
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 1");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "1 Lectrum");
        tippedArrowMeta.setLore(lore);
        tippedArrowItem.setAmount(8);
        tippedArrowItem.setItemMeta(tippedArrowMeta);
        gui.setItem(2, tippedArrowItem);
        lore.clear();

        if(playerLevel >= 2){
            // Viper's Bite;
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aViper's Bite");
            tippedArrowMeta.setColor(Color.GREEN);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Poisons your target for a short");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "duration.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.viperBite.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 2");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "3 Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            gui.setItem(4, tippedArrowItem);
            lore.clear();
        }
        if(playerLevel >= 3){
            // Totsuka's Creation
            item.setType(Material.COBWEB);
            meta.setDisplayName(ChatColor.RESET + "§aTotsuka's Creation");
            meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Throw a web to the targeted");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "location, rooting enemies caught");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "in its location.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.totsukaCreation.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.archeryListener.totsukaCreation.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 3");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "40 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(6, item);
            lore.clear();
        }
        if(playerLevel >= 4){
            // Wind Boost
            item.setType(Material.FEATHER);
            meta.setDisplayName(ChatColor.RESET + "§aWind Boost");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Harness the power of wind to");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "boost your arrows, dealing more");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "damage and knockback.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.windBoost.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.archeryListener.windBoost.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 4");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "80 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(8, item);
            lore.clear();
        }
        if(playerLevel >= 5){
            // Neurotoxin
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aNeurotoxin");
            tippedArrowMeta.setColor(Color.LIME);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbue your arrow with neurotoxin,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "poisoning and slowing targets for");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "a medium duration.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.neurotoxin.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 5");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            gui.setItem(10, tippedArrowItem);
            lore.clear();
        }
        if(playerLevel >= 6){
            // Soulstring
            item.setType(Material.TRIPWIRE_HOOK);
            meta.setDisplayName(ChatColor.RESET + "§aSoulstring");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summons a bow companion that");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "periodically shoots very sharp");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "arrows at nearby enemies.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.soulstring.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.archeryListener.soulstring.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 6");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(12, item);
            lore.clear();
        }
        if(playerLevel >= 7){
            // Retreat
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aRetreat");
            tippedArrowMeta.setColor(Color.WHITE);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Shoots two arrows with the first");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "dealing less damage. After shooting,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "you fall back a short distance and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "your speed is temporarily increased.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.retreat.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 7");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "15 Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            gui.setItem(14, tippedArrowItem);
            lore.clear();
            // Huayra's Fury
            item.setType(Material.SKELETON_SKULL);
            meta.setDisplayName(ChatColor.RESET + "§aHuayra's Fury");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "You reawaken the wrath of Huayra,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "cursing critical arrows you shoot");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "to strike at incredible speeds and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "have the ability to pierce through");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "your enemies.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.huayraFury.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.archeryListener.huayraFury.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 7");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(16, item);
            lore.clear();
        }
        if(playerLevel >= 8){
            // Blast
            tippedArrowMeta.setDisplayName(ChatColor.RESET + "§aBlast");
            tippedArrowMeta.setColor(Color.RED);
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbue your arrow with gunpowder,");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "making it explode on impact.");;
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.blast.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 8");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "20 Lectrum");
            tippedArrowMeta.setLore(lore);
            tippedArrowItem.setItemMeta(tippedArrowMeta);
            tippedArrowItem.setAmount(8);
            gui.setItem(20, tippedArrowItem);
            lore.clear();
        }
        if(playerLevel >= 9){
            // Royal Artillery
            item.setType(Material.NAUTILUS_SHELL);
            meta.setDisplayName(ChatColor.RESET + "§aRoyal Artillery");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Calls in a continuous arrow");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "barrage on the targeted location.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.royalArtillery.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: "+plugin.archeryListener.royalArtillery.getCooldown()/20+" seconds");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 9");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(22, item);
            lore.clear();
        }
        if(playerLevel >= 10){
            // Whistling Wind
            item.setType(Material.SPECTRAL_ARROW);
            meta.setDisplayName(ChatColor.RESET + "§aWhistling Wind");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "An arrow that directs itself towards");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "every enemy around its shooter and");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "returns after.");
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: "+plugin.archeryListener.whistlingWind.getManaCost());
            lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Archery 10");
            lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.setItem(24, item);
            lore.clear();
        }
        return gui;
    }
}