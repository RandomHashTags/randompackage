package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.file.FileShopCategory;
import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.obj.ShopItem;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.ShopPurchaseEvent;
import me.randomhashtags.randompackage.event.ShopSellEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
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
	public void load() {
	    final long started = System.currentTimeMillis();
	    save("shops", "_settings.yml");
	    final String folder = DATA_FOLDER + SEPARATOR + "shops";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        back = d(config, "items.back to categories");
        defaultShop = config.getString("settings./shop opens");

        if(!otherdata.getBoolean("saved default shops")) {
            generateDefaultShopCategories();
            otherdata.set("saved default shops", true);
            saveOtherData();
        }

        ShopCategory.shop = this;
        titles = new HashMap<>();
        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileShopCategory c = new FileShopCategory(f);
                titles.put(c.getTitle(), c);
            }
        }
        sendConsoleMessage(colorize("&6[RandomPackage] &aLoaded " + getAll(Feature.SHOP_CATEGORY).size() + " Shop Categories &e(took " + (System.currentTimeMillis()-started) + "ms)"));
    }
    public void unload() {
	    unregister(Feature.SHOP_CATEGORY);
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
        for(int k = 1; k <= 100; k++) {
            if(player.hasPermission("RandomPackage.shop.discount." + k)) {
                d = BigDecimal.valueOf(k);
            }
        }
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
                final String clickType = event.getClick().name();
                final ItemStack c = event.getCurrentItem();
                if(r < 0 || r >= top.getSize() || !clickType.contains("LEFT") && !clickType.contains("RIGHT") || c == null) return;

                if(c.equals(back)) {
                    view(player);
                } else if(eco == null) {
                    Bukkit.broadcastMessage("[RandomPackage] An Economy plugin is required to use /shop!");
                    player.closeInventory();
                } else {
                    final ShopItem shopItem = s.getItem(r);
                    if(shopItem != null) {
                        final String o = shopItem.opensCategory;
                        if(o != null) {
                            player.closeInventory();
                            viewCategory(player, o);
                        } else if(clickType.endsWith("LEFT")) {
                            tryPurchasing(player, shopItem, clickType);
                        } else if(clickType.endsWith("RIGHT")) {
                            trySelling(player, shopItem, clickType);
                        }
                    }
                }
            }
        }
	}

	public void view(@NotNull Player player) {
	    if(hasPermission(player, "RandomPackage.shop", true)) {
            player.closeInventory();
            final UInventory inv = getShopCategory(defaultShop).getInventory();
            player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(inv.getInventory().getContents());
            player.updateInventory();
        }
	}
	public void viewCategory(@NotNull Player player, @NotNull String identifier) {
	    final ShopCategory s = getShopCategory(identifier);
	    if(player != null && s != null) {
	        player.closeInventory();
	        final UInventory inv = s.getInventory();
	        player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
	        final Inventory top = player.getOpenInventory().getTopInventory();
	        top.setContents(inv.getInventory().getContents());

            final BigDecimal discount = getDiscount(player), one = BigDecimal.ONE;

            final List<String> buylore = getStringList(config, "lores.purchase"), selllore = getStringList(config, "lores.sell");
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
                            lore.add(string);
                        }
                    }
                    if(sellPrice.doubleValue() > 0.00) {
                        final String ss = formatBigDecimal(sellPrice);
                        sell = true;
                        for(String string : selllore) {
                            if(string.contains("{SELL}")) string = string.replace("{SELL}", ss);
                            lore.add(string);
                        }
                    }
                    final String single = Integer.toString(item.getAmount()), shift = Integer.toString(item.getMaxStackSize());
                    for(String type : new String[]{"LEFT", "RIGHT"}) {
                        if(type.equals("LEFT") && buy || type.equals("RIGHT") && sell) {
                            for(String string : getStringList(config, "lores." + type.toLowerCase() + " clicks")) {
                                string = string.replace("{" + type + "_CLICK}", single).replace("{SHIFT_" + type + "_CLICK}", shift);
                                lore.add(string);
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

    public void tryPurchasing(@NotNull Player player, @NotNull ShopItem shopItem, @NotNull String clickType) {
        final BigDecimal buy = shopItem.buyPrice, discount = getDiscount(player);
        if(buy.doubleValue() > 0.00) {
            BigDecimal cost = buy, dis = discount.multiply(buy);
            cost = cost.subtract(dis);
            final ItemStack is = shopItem.getPurchased();
            int amountPurchased = clickType.equals("LEFT") ? 1 : is.getMaxStackSize();
            cost = cost.multiply(BigDecimal.valueOf(amountPurchased));
            item = is;
            item.setAmount(is.getAmount()*amountPurchased);
            final ShopPurchaseEvent e = new ShopPurchaseEvent(player, shopItem, item, amountPurchased, cost);
            PLUGIN_MANAGER.callEvent(e);
            if(e.isCancelled()) return;
            cost = e.getTotal();
            amountPurchased = e.getAmount();
            item = is;
            item.setAmount(is.getAmount());
            boolean purchased = false;
            if(eco.withdrawPlayer(player, cost.doubleValue()).transactionSuccess()) {
                purchased = true;
                giveItem(player, item);
                final List<String> commands = shopItem.getExecutedCommands();
                if(commands != null) {
                    final String name = player.getName();
                    for(String s : commands) {
                        SERVER.dispatchCommand(CONSOLE, s.replace("%player%", name));
                    }
                }
            }
            playSound(config, "sounds." + (purchased ? "buy" : "not enough balance"), player, player.getLocation(), false);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{PRICE}", formatBigDecimal(buy));
            replacements.put("{TOTAL}", formatBigDecimal(cost));
            replacements.put("{AMOUNT}", Integer.toString(amountPurchased));
            replacements.put("{ITEM}", item.getType().name());
            sendStringListMessage(player, getStringList(config, "messages.purchase" + (purchased ? "" : " incomplete")), replacements);
        } else {
            playSound(config, "sounds.not buyable", player, player.getLocation(), false);
        }
    }
    public void trySelling(@NotNull Player player, @NotNull ShopItem shopItem, @NotNull String clickType) {
        final BigDecimal sell = shopItem.sellPrice;
        if(sell.doubleValue() > 0.00) {
            ItemStack A = shopItem.getPurchased();
            final Inventory inv = player.getInventory();
            String msg = "messages.sell" + (!inv.containsAtLeast(A, 1) ? " incomplete" : "");
            final BigDecimal price = shopItem.sellPrice;
            int amountSold = clickType.equals("RIGHT") ? A.getAmount() : A.getMaxStackSize();

            if(!inv.containsAtLeast(A, 1)) {
                playSound(config, "sounds.not enough to sell", player, player.getLocation(), false);
            } else {
                final int has = getTotalAmount(inv, UMaterial.match(A));
                amountSold = Math.min(has, amountSold);
                if(inv.containsAtLeast(A, amountSold)) {
                    BigDecimal profit = price.multiply(BigDecimal.valueOf(amountSold));
                    final ShopSellEvent e = new ShopSellEvent(player, shopItem, A, amountSold, profit);
                    PLUGIN_MANAGER.callEvent(e);
                    if(e.isCancelled()) return;
                    A = e.getItem();
                    profit = e.getTotal();
                    amountSold = e.getAmount();
                    eco.depositPlayer(player, profit.doubleValue());
                    removeItem(player, A, amountSold);
                } else {
                    msg = "messages.sell incomplete";
                }
            }
            final String item = UMaterial.match(A).name(), priceString = formatBigDecimal(price), amount = formatInt(amountSold), total = formatBigDecimal(price.multiply(BigDecimal.valueOf(amountSold)));
            final HashMap<String, String> replacements = new HashMap<String, String>() {{
                put("{TOTAL", total);
                put("{AMOUNT}", amount);
                put("{PRICE}", priceString);
                put("{ITEM}", item);
            }};
            sendStringListMessage(player, config.getStringList(msg), replacements);
        } else {
            playSound(config, "sounds.not sellable", player, player.getLocation(), false);
        }
    }
}