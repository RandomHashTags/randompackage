package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.api.events.ShopPrePurchaseEvent;
import me.randomhashtags.randompackage.api.events.ShopPreSellEvent;
import me.randomhashtags.randompackage.api.events.ShopPurchaseEvent;
import me.randomhashtags.randompackage.api.events.ShopSellEvent;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.addons.usingfile.FileShopCategory;
import me.randomhashtags.randompackage.addons.objects.ShopItem;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Shop extends RPFeature implements CommandExecutor {
	private static Shop instance;
	public static Shop getShop() {
	    if(instance == null) instance = new Shop();
	    return instance;
	}

	public YamlConfiguration config;
	public ItemStack back;
	private String defaultShop;
	private HashMap<String, FileShopCategory> titles;

	public void load() {
	    final long started = System.currentTimeMillis();
	    save(null, "shop.yml");

        config = YamlConfiguration.loadConfiguration(new File(rpd, "shop.yml"));
        back = d(config, "items.back to categories");
        defaultShop = config.getString("settings./shop opens");

        if(!otherdata.getBoolean("saved default shops")) {
            final String[] h = new String[] {"BASE_GRIND", "BREWING", "BUILDING_BLOCKS", "CLAY", "FLOWERS", "FOOD_AND_FARMING", "GLASS", "MENU", "MOB_DROPS", "ORES_AND_GEMS", "POTIONS", "RAID", "SPAWNERS", "SPECIALTY", "WOOL"};
            for(String s : h) save("shops", s + ".yml");
            otherdata.set("saved default shops", true);
            saveOtherData();
        }

        titles = new HashMap<>();
        final File folder = new File(rpd + separator + "shops");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                final FileShopCategory c = new FileShopCategory(f);
                titles.put(c.getInventoryTitle(), c);
            }
        }
        final HashMap<String, FileShopCategory> S = FileShopCategory.categories;
        sendConsoleMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &aLoaded " + (S != null ? S.size() : 0) + " shop categories &e(took " + (System.currentTimeMillis()-started) + "ms)"));
    }
    public void unload() {
	    config = null;
	    back = null;
	    FileShopCategory.deleteAll();
    }

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		if(player != null) view(player);
		return true;
	}
	public double getDiscount(Player player) {
	    if(player.hasPermission("RandomPackage.shop.discount.cancel")) return 0.00;
	    double d = 0;
        for(int k = 1; k <= 100; k++)
            if(player.hasPermission("RandomPackage.shop.discount." + k))
                d = k;
            return d/100;
    }
	@EventHandler(priority = EventPriority.HIGHEST)
	private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
		if(!event.isCancelled() && top.getHolder() == player) {
			final FileShopCategory s = titles.getOrDefault(event.getView().getTitle(), null);
			if(s != null) {
                event.setCancelled(true);
                player.updateInventory();
                final int r = event.getRawSlot();
                final String cl = event.getClick().name();
                final ItemStack c = event.getCurrentItem();
                if(r < 0 || r >= top.getSize() || !cl.contains("LEFT") && !cl.contains("RIGHT") || c == null) return;

                if(c.equals(back)) {
                    view(player);
                } else if(eco == null) {
                    Bukkit.broadcastMessage("[RandomPackage] An Economy plugin is required to use /shop!");
                    player.closeInventory();
                } else {
                    final ShopItem si = s.getItem(r);
                    if(si != null) {
                        final String o = si.opensCategory;
                        if(o != null) {
                            player.closeInventory();
                            viewCategory(player, o);
                        } else {
                            final double discount = getDiscount(player), buy = si.buyPrice, sell = si.sellPrice;
                            if(cl.endsWith("LEFT")) {
                                if(buy > 0.00) {
                                    double cost = buy, dis = discount*buy;
                                    cost -= dis;
                                    final ItemStack is = si.getPurchased().clone();
                                    int amountPurchased = cl.equals("LEFT") ? 1 : is.getMaxStackSize();
                                    cost *= amountPurchased;
                                    final ShopPrePurchaseEvent e = new ShopPrePurchaseEvent(player, si, amountPurchased, cost);
                                    pluginmanager.callEvent(e);
                                    if(e.isCancelled()) return;
                                    cost = e.getCost();
                                    amountPurchased = e.getAmount();
                                    item = is;
                                    item.setAmount(is.getAmount()*amountPurchased);
                                    boolean purchased = false;
                                    if(eco.withdrawPlayer(player, cost).transactionSuccess()) {
                                        purchased = true;
                                        giveItem(player, item);
                                        final ShopPurchaseEvent ev = new ShopPurchaseEvent(player, si, item, amountPurchased, cost);
                                        pluginmanager.callEvent(ev);
                                    }
                                    playSound(config, "sounds." + (purchased ? "buy" : "not enough balance"), player, player.getLocation(), false);
                                    final HashMap<String, String> replacements = new HashMap<>();
                                    replacements.put("{PRICE}", formatDouble(buy));
                                    replacements.put("{TOTAL}", formatDouble(cost));
                                    replacements.put("{AMOUNT}", Integer.toString(amountPurchased));
                                    replacements.put("{ITEM}", item.getType().name());
                                    sendStringListMessage(player, config.getStringList("messages.purchase" + (purchased ? "" : " incomplete")), replacements);
                                } else {
                                    playSound(config, "sounds.not buyable", player, player.getLocation(), false);
                                }
                            } else if(cl.endsWith("RIGHT")) {
                                if(sell > 0.00) {
                                    final ItemStack A = si.getPurchased().clone();
                                    final Inventory inv = player.getInventory();
                                    String p = "messages.sell" + (!inv.containsAtLeast(A, 1) ? " incomplete" : "");
                                    final double price = si.sellPrice;
                                    int amountSold = cl.equals("RIGHT") ? A.getAmount() : A.getMaxStackSize();

                                    if(!inv.containsAtLeast(A, 1)) {
                                        playSound(config, "sounds.not enough to sell", player, player.getLocation(), false);
                                    } else {
                                        final int has = getTotalAmount(inv, UMaterial.match(A));
                                        amountSold = amountSold > has ? has : amountSold;
                                        if(inv.containsAtLeast(A, amountSold)) {
                                            double profit = price * amountSold;
                                            final ShopPreSellEvent e = new ShopPreSellEvent(player, si, amountSold, profit);
                                            pluginmanager.callEvent(e);
                                            if(e.isCancelled()) return;
                                            profit = e.getProfit();
                                            amountSold = e.getAmount();
                                            eco.depositPlayer(player, profit);
                                            removeItem(player, A, amountSold);
                                            final ShopSellEvent ee = new ShopSellEvent(player, si, A, amountSold, profit);
                                            pluginmanager.callEvent(ee);
                                        } else {
                                            p = "messages.sell incomplete";
                                        }
                                    }
                                    final String n = UMaterial.match(A).name(), pr = formatDouble(round(price, 2)), amts = formatInt(amountSold), ttl = formatDouble(round(amountSold*price, 2));
                                    for(String string : config.getStringList(p)) {
                                        if(string.contains("{TOTAL}")) string = string.replace("{TOTAL}", ttl);
                                        if(string.contains("{AMOUNT}")) string = string.replace("{AMOUNT}", amts);
                                        if(string.contains("{PRICE}")) string = string.replace("{PRICE}", pr);
                                        if(string.contains("{ITEM}")) string = string.replace("{ITEM}", n);
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
                                    }
                                } else {
                                    playSound(config, "sounds.not sellable", player, player.getLocation(), false);
                                }
                            }
                        }
                    }
                }
            }
        }
	}
	public void view(Player player) {
	    if(hasPermission(player, "RandomPackage.shop", true)) {
            player.closeInventory();
            final UInventory inv = FileShopCategory.categories.get(defaultShop).getInventory();
            player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(inv.getInventory().getContents());
            player.updateInventory();
        }
	}
	public void viewCategory(Player player, String yml) {
	    final FileShopCategory s = FileShopCategory.categories.getOrDefault(yml, null);
	    if(player != null && s != null) {
	        player.closeInventory();
	        final UInventory inv = s.getInventory();
	        player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
	        final Inventory top = player.getOpenInventory().getTopInventory();
	        top.setContents(inv.getInventory().getContents());

            final double discount = getDiscount(player);

            final List<String> buylore = config.getStringList("lores.purchase"), selllore = config.getStringList("lores.sell");
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null && !item.equals(back)) {
                    final ShopItem si = s.getItem(i);
                    item = item.clone();
                    itemMeta = item.getItemMeta(); lore.clear();
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                    boolean buy = false, sell = false;
                    double buyPrice = si.buyPrice, sellPrice = si.sellPrice;
                    if(buyPrice > 0.00) {
                        buyPrice -= buyPrice*discount;
                        final String b = formatDouble(round(buyPrice, 2));
                        buy = true;
                        for(String string : buylore) {
                            if(string.contains("{BUY}")) string = string.replace("{BUY}", b);
                            lore.add(ChatColor.translateAlternateColorCodes('&', string));
                        }
                    }
                    if(sellPrice > 0.00) {
                        sell = true;
                        for(String string : selllore) {
                            if(string.contains("{SELL}")) string = string.replace("{SELL}", formatDouble(sellPrice));
                            lore.add(ChatColor.translateAlternateColorCodes('&', string));
                        }
                    }
                    final String single = Integer.toString(item.getAmount()), shift = Integer.toString(item.getMaxStackSize());
                    for(String q : new String[]{"LEFT", "RIGHT"}) {
                        if(q.equals("LEFT") && buy || q.equals("RIGHT") && sell) {
                            for(String string : config.getStringList("lores." + q.toLowerCase() + " clicks")) {
                                string = string.replace("{" + q + "_CLICK}", single).replace("{SHIFT_" + q + "_CLICK}", shift);
                                lore.add(ChatColor.translateAlternateColorCodes('&', string));
                            }
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                    top.setItem(i, item);
                }
            }
            player.updateInventory();
        }
    }
}