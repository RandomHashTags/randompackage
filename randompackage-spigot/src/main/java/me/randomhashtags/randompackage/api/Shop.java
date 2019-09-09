package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.obj.ShopItem;
import me.randomhashtags.randompackage.event.ShopPurchaseEvent;
import me.randomhashtags.randompackage.event.ShopSellEvent;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.addon.FileShopCategory;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
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
import java.math.BigDecimal;
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

	public String getIdentifier() { return "SHOP"; }
	protected RPFeature getFeature() { return getShop(); }
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

        ShopCategory.shop = this;
        titles = new HashMap<>();
        final File folder = new File(rpd + separator + "shops");
        if(folder.exists()) {
            for(File f : folder.listFiles()) {
                final FileShopCategory c = new FileShopCategory(f);
                titles.put(c.getTitle(), c);
            }
        }
        sendConsoleMessage(ChatColor.translateAlternateColorCodes('&', "&6[RandomPackage] &aLoaded " + (shopcategories != null ? shopcategories.size() : 0) + " shop categories &e(took " + (System.currentTimeMillis()-started) + "ms)"));
    }
    public void unload() {
	    shopcategories = null;
    }

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		final Player player = sender instanceof Player ? (Player) sender : null;
		if(player != null) view(player);
		return true;
	}
	public BigDecimal getDiscount(Player player) {
	    final BigDecimal zero = BigDecimal.ZERO;
	    if(player.hasPermission("RandomPackage.shop.discount.cancel")) return zero;
	    BigDecimal d = zero;
        for(int k = 1; k <= 100; k++)
            if(player.hasPermission("RandomPackage.shop.discount." + k))
                d = BigDecimal.valueOf(k);
        return BigDecimal.valueOf(d.doubleValue()/100);
    }
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
		if(top.getHolder() == player) {
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
                            final BigDecimal discount = getDiscount(player), buy = si.buyPrice, sell = si.sellPrice;
                            if(cl.endsWith("LEFT")) {
                                if(buy.doubleValue() > 0.00) {
                                    BigDecimal cost = buy, dis = discount.multiply(buy);
                                    cost = cost.subtract(dis);
                                    final ItemStack is = si.getPurchased().clone();
                                    int amountPurchased = cl.equals("LEFT") ? 1 : is.getMaxStackSize();
                                    cost = cost.multiply(BigDecimal.valueOf(amountPurchased));
                                    item = is;
                                    item.setAmount(is.getAmount()*amountPurchased);
                                    final ShopPurchaseEvent e = new ShopPurchaseEvent(player, si, item, amountPurchased, cost);
                                    pluginmanager.callEvent(e);
                                    if(e.isCancelled()) return;
                                    cost = e.getTotal();
                                    amountPurchased = e.getAmount();
                                    item = is;
                                    item.setAmount(is.getAmount());
                                    boolean purchased = false;
                                    if(eco.withdrawPlayer(player, cost.doubleValue()).transactionSuccess()) {
                                        purchased = true;
                                        giveItem(player, item);

                                    }
                                    playSound(config, "sounds." + (purchased ? "buy" : "not enough balance"), player, player.getLocation(), false);
                                    final HashMap<String, String> replacements = new HashMap<>();
                                    replacements.put("{PRICE}", formatBigDecimal(buy));
                                    replacements.put("{TOTAL}", formatBigDecimal(cost));
                                    replacements.put("{AMOUNT}", Integer.toString(amountPurchased));
                                    replacements.put("{ITEM}", item.getType().name());
                                    sendStringListMessage(player, config.getStringList("messages.purchase" + (purchased ? "" : " incomplete")), replacements);
                                } else {
                                    playSound(config, "sounds.not buyable", player, player.getLocation(), false);
                                }
                            } else if(cl.endsWith("RIGHT")) {
                                if(sell.doubleValue() > 0.00) {
                                    ItemStack A = si.getPurchased().clone();
                                    final Inventory inv = player.getInventory();
                                    String p = "messages.sell" + (!inv.containsAtLeast(A, 1) ? " incomplete" : "");
                                    final BigDecimal price = si.sellPrice;
                                    int amountSold = cl.equals("RIGHT") ? A.getAmount() : A.getMaxStackSize();

                                    if(!inv.containsAtLeast(A, 1)) {
                                        playSound(config, "sounds.not enough to sell", player, player.getLocation(), false);
                                    } else {
                                        final int has = getTotalAmount(inv, UMaterial.match(A));
                                        amountSold = Math.min(has, amountSold);
                                        if(inv.containsAtLeast(A, amountSold)) {
                                            BigDecimal profit = price.multiply(BigDecimal.valueOf(amountSold));
                                            final ShopSellEvent e = new ShopSellEvent(player, si, A, amountSold, profit);
                                            pluginmanager.callEvent(e);
                                            if(e.isCancelled()) return;
                                            A = e.getItem();
                                            profit = e.getTotal();
                                            amountSold = e.getAmount();
                                            eco.depositPlayer(player, profit.doubleValue());
                                            removeItem(player, A, amountSold);
                                        } else {
                                            p = "messages.sell incomplete";
                                        }
                                    }
                                    final String n = UMaterial.match(A).name(), pr = formatBigDecimal(price), amts = formatInt(amountSold), ttl = formatBigDecimal(price.multiply(BigDecimal.valueOf(amountSold)));
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
            final UInventory inv = getShopCategory(defaultShop).getInventory();
            player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(inv.getInventory().getContents());
            player.updateInventory();
        }
	}
	public void viewCategory(Player player, String identifier) {
	    final ShopCategory s = getShopCategory(identifier);
	    if(player != null && s != null) {
	        player.closeInventory();
	        final UInventory inv = s.getInventory();
	        player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
	        final Inventory top = player.getOpenInventory().getTopInventory();
	        top.setContents(inv.getInventory().getContents());

            final BigDecimal discount = getDiscount(player), one = BigDecimal.ONE;

            final List<String> buylore = config.getStringList("lores.purchase"), selllore = config.getStringList("lores.sell");
            for(int i = 0; i < top.getSize(); i++) {
                item = top.getItem(i);
                if(item != null && !item.equals(back)) {
                    final ShopItem si = s.getItem(i);
                    item = item.clone();
                    itemMeta = item.getItemMeta(); lore.clear();
                    if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                    boolean buy = false, sell = false;
                    BigDecimal buyPrice = si.buyPrice, sellPrice = si.sellPrice;
                    if(buyPrice.doubleValue() > 0.00) {
                        final String b = formatBigDecimal(buyPrice.multiply(discount.doubleValue() > 0 ? discount : one));
                        buy = true;
                        for(String string : buylore) {
                            if(string.contains("{BUY}")) string = string.replace("{BUY}", b);
                            lore.add(ChatColor.translateAlternateColorCodes('&', string));
                        }
                    }
                    if(sellPrice.doubleValue() > 0.00) {
                        final String ss = formatBigDecimal(sellPrice);
                        sell = true;
                        for(String string : selllore) {
                            if(string.contains("{SELL}")) string = string.replace("{SELL}", ss);
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