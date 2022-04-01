package me.cuna.paramalegends.game;

import me.cuna.paramalegends.DataManager;
import me.cuna.paramalegends.ParamaLegends;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class PlayerShopListener implements Listener {

    public DataManager data;
    private final ParamaLegends plugin;
    private final NamespacedKey key;

    public PlayerShopListener(final ParamaLegends plugin){
        this.plugin = plugin;
        data = plugin.dataManager;
        key = new NamespacedKey(plugin, "shopowner");
    }

    //Creating shop
    @EventHandler
    public void onChangeSign(SignChangeEvent event) {
        Block block = event.getBlock();
        if(block.getBlockData() instanceof WallSign) {
            WallSign sign = (WallSign) block.getBlockData();
            Block attached = block.getRelative(sign.getFacing().getOppositeFace());
            //check if sign is placed on chest
            if (attached.getType().equals(Material.CHEST)) {
                String firstLine = event.getLine(0);
                if (firstLine != null && firstLine.equals("[ParamaShop]")) {
                    Player player = event.getPlayer();
                    String price = event.getLine(1);
                    String amount = event.getLine(3);
                    if (price != null && amount != null) {
                        if(price.endsWith("Lectrum") || price.endsWith("lectrum"))
                            try {
                                Integer.parseInt(price.substring(0, price.indexOf(" ")));
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.RED + "Invalid price.");
                                return;
                            }
                        else {
                            player.sendMessage(ChatColor.RED + "Invalid price.");
                            return;
                        }
                        try {
                            Integer.parseInt(amount);
                        } catch (NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Invalid amount.");
                            return;
                        }
                        block.setMetadata("SHOPNOITEMPARAMA", new FixedMetadataValue(plugin, 1));
                        player.sendMessage(ChatColor.GREEN + "Right click sign with item.");
                    }
                } else {
                    //remove metadata if not player shop
                    block.removeMetadata("SIGNCHESTPARAMA", plugin);
                }
            }
        }
    }

    //interacting with shop
    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if(Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) return;
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(event.getClickedBlock() != null){
                Block block = event.getClickedBlock();
                Player player = event.getPlayer();
                //lock shops
                if(block.getState() instanceof Chest) {
                    Chest chest = (Chest) block.getState();
                    String uuid = chest.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                    if (uuid != null) {
                        if (!player.getUniqueId().toString().equals(uuid)) {
                            player.sendMessage(ChatColor.RED + "You are not the owner of this shop.");
                            event.setCancelled(true);
                        }
                    }
                }
                //shop interaction
                if(block.getBlockData() instanceof WallSign && block.getState() instanceof Sign){
                    Sign sign = (Sign) block.getState();
                    //setting item
                    if(block.hasMetadata("SHOPNOITEMPARAMA")){
                        WallSign wallSign = (WallSign) block.getBlockData();
                        Block attached = block.getRelative(wallSign.getFacing().getOppositeFace());
                        if(attached.getType().equals(Material.CHEST)){
                            if(player.getEquipment() != null){
                                Chest chest = (Chest) attached.getState();
                                ItemStack item = player.getEquipment().getItemInMainHand();
                                chest.getPersistentDataContainer().set(key, PersistentDataType.STRING, player.getUniqueId().toString());
                                chest.update();
                                sign.getPersistentDataContainer().set(key, PersistentDataType.STRING, player.getUniqueId().toString());
                                sign.setLine(0, ChatColor.BOLD+player.getName());
                                sign.setLine(1, ChatColor.GOLD+sign.getLine(1));
                                sign.setLine(2, item.getType().toString());
                                sign.update();
                                block.removeMetadata("SHOPNOITEMPARAMA", plugin);
                            }
                        }
                    }
                    //buying items
                    if(sign.getPersistentDataContainer().has(key, PersistentDataType.STRING)){
                        String uuid = sign.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                        if(uuid != null){
                            if(player.getUniqueId().toString().equals(uuid)) return;
                            int price;
                            int amount;
                            try {
                                price = Integer.parseInt(sign.getLine(1).substring(2, sign.getLine(1).indexOf(" ")));
                                amount = Integer.parseInt(sign.getLine(3));
                            } catch (Exception e) {
                                return;
                            }
                            //check if player has enough lectrum
                            int lectrum = plugin.getPlayerParama(player).getLectrum();
                            if(lectrum < price){
                                player.sendMessage(ChatColor.RED+"Insufficient lectrum.");
                                return;
                            }
                            WallSign wallSign = (WallSign) block.getBlockData();
                            Block attached = block.getRelative(wallSign.getFacing().getOppositeFace());
                            if(attached.getType().equals(Material.CHEST)){
                                Chest chest = (Chest) attached.getState();
                                Material material = Objects.requireNonNull(Material.getMaterial(sign.getLine(2)));
                                Inventory shop = chest.getInventory();
                                if(shop.contains(material, amount)){
                                    for(ItemStack item : shop.getContents()){
                                        if(item == null) continue;
                                        if(item.getType().equals(material)){
                                            if(item.getAmount() >= amount){
                                                int remaining = item.getAmount() - amount;
                                                item.setAmount(amount);
                                                player.getInventory().addItem(item);
                                                item.setAmount(remaining);
                                                amount = 0;
                                            } else {
                                                player.getInventory().addItem(item);
                                                amount -= item.getAmount();
                                                item.setAmount(0);
                                            }
                                            if(amount <= 0)break;
                                        }
                                    }
                                    plugin.getPlayerParama(player).removeLectrum(price);
                                    int tax = 0;

                                    //check tax
                                    String mayorUuidString = data.getConfig().getString("mayoruuid");
                                    if(mayorUuidString != null && !mayorUuidString.isBlank()){
                                        tax = (int) Math.ceil(price/20d);
                                        Player mayor = plugin.getServer().getPlayer(UUID.fromString(mayorUuidString));
                                        if(mayor != null){
                                            plugin.getPlayerParama(mayor).addLectrum(tax);
                                        } else {
                                            int lectrumMayor = data.getConfig().getInt("players."+mayorUuidString+".lectrum");
                                            data.getConfig().set("players."+mayorUuidString+".lectrum", lectrumMayor+tax);
                                        }
                                        plugin.leaderboard.updateNetWorth(mayorUuidString);
                                    }

                                    int lectrumSeller;
                                    Player seller = plugin.getServer().getPlayer(UUID.fromString(uuid));

                                    if(seller != null){
                                        seller.sendMessage(ChatColor.GREEN+player.getName()+" purchased "+sign.getLine(3) + " " + sign.getLine(2) + " from you for " + price + " lectrum.");
                                        plugin.getPlayerParama(seller).addLectrum(price-tax);
                                    } else {
                                        lectrumSeller = data.getConfig().getInt("players."+uuid+".lectrum");
                                        data.getConfig().set("players."+uuid+".lectrum", lectrumSeller+price-tax);
                                        data.saveConfig();
                                    }

                                    player.sendMessage(ChatColor.GREEN+"Purchased " + sign.getLine(3) + " " + sign.getLine(2) + " for " + price + " lectrum.");

                                    plugin.leaderboard.updateNetWorth(uuid);
                                    plugin.leaderboard.updateNetWorth(player.getUniqueId().toString());

                                } else {
                                    player.sendMessage(ChatColor.RED+"Shop out of stock.");
                                }
                            }
                        }
                        }
                }
            }
        }
    }

}
