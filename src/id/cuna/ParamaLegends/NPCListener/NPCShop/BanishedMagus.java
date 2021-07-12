package id.cuna.ParamaLegends.NPCListener.NPCShop;

import id.cuna.ParamaLegends.DataManager;
import id.cuna.ParamaLegends.NPCListener.NPCShopListener;
import id.cuna.ParamaLegends.ParamaLegends;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BanishedMagus extends NPCShopListener {

    //Prices array
    public static HashMap<Integer, Integer> prices = new HashMap<>(){{
        put(2,10);
        put(4,20);
        put(6,30);
        put(8,60);
        put(10,80);
        put(12,200);
        put(14,200);
        put(16,300);
        put(20,400);
        put(24,600);
    }};

    public BanishedMagus(ParamaLegends plugin) {
        super(plugin, "§5Banished Magus");
    }

    //Get prices
    @Override
    public HashMap<Integer, Integer> getPrices(){
        return prices;
    }

    //Send player message when opening gui
    @Override
    public String getNPCMessage(){
        return ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "Even the wisdom of wise mages can be bartered for.";
    }

    //Attack player when npc attacked according to npc type
    @Override
    public void NPCAttack(Player player, Entity npc){
        Entity cloudEntity = player.getLocation().getWorld().spawnEntity(player.getLocation().add(0,1,0), EntityType.AREA_EFFECT_CLOUD);
        AreaEffectCloud cloud = (AreaEffectCloud) cloudEntity;
        cloud.setParticle(Particle.CRIT_MAGIC);
        cloud.setDuration(1);
        player.damage(10, npc);
    }

    //Create shop gui
    @Override
    public Inventory createGui(Player player, DataManager data){
        Inventory gui;
        gui = Bukkit.createInventory(null,27, "§5Magic Tomes");

        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();

        // Your Lectrum
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.RESET + "Your Lectrum");
        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "" + data.getConfig().getInt("players." + player.getUniqueId().toString() + ".lectrum"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(0, item);
        lore.clear();

        // Fling Earth
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Fling Earth");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Gathers nearby earth into");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "a dense cube, then flinging");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "it towards whatever may be");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "in its path.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 10");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 3 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 1");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "10 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(2, item);
        lore.clear();

        // Ignite
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Ignite");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Sets targets ablaze within a");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "small area, dealing damage");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "over time.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 20");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 7 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 2");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "20 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, item);
        lore.clear();

        // Gust
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Gust");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Pushes away targets within an");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "area of where you are facing.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 30");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 3");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "30 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(6, item);
        lore.clear();


        // Life Drain
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Life Drain");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Periodically drains the life of");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "nearby beings when activated.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 20 per second");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 4");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "60 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(8, item);
        lore.clear();

        // Blink
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Blink");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Instantaneously teleport to");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the targeted location.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 30");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 15 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 5");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "80 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(10, item);
        lore.clear();

        // Summon Lightning
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Summon Lightning");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Call upon the wrath of the");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "thundergod to summon lightning");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "and smite all enemies in an");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "area.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 150");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 30 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 6");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(12, item);
        lore.clear();

        // Illusory orb
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Illusory Orb");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Casts an arcane orb that hurts");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "any being it comes into contact");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "with. Casting the spell while an");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "orb is travelling teleports the");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "caster to its location.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 100");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 10 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 7");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "200 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(14, item);
        lore.clear();

        // Dragon breath
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Dragon's Breath");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Imbuing your essence with draconic");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "powers, you unleash a wide dragon");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "breath attack in front of you");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "that harms all beings it comes");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "into contact with.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 200");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 20 seconds");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 8");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "300 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(16, item);
        lore.clear();

        // Voices of the Damned
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Voices of the Damned");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Summons a portal to the realm of");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "the damned. Creatures will appear");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "from the portal and attack any");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "creatures that are in sight.");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 400");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 1 minute");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 9");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "400 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(20, item);
        lore.clear();

        // Nova
        item.setType(Material.ENCHANTED_BOOK);
        meta.setDisplayName(ChatColor.RESET + "§5Nova");
        lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "爆裂, 爆裂, la la la");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Mana Cost: 600");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: 2 minutes");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Prerequisite: Magic 10");
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "600 Lectrum");
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(24, item);
        lore.clear();

        return gui;
    }


}
