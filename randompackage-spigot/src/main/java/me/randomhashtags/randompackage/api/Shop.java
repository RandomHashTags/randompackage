package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.file.FileShopCategory;
import me.randomhashtags.randompackage.addon.legacy.ShopCategory;
import me.randomhashtags.randompackage.addon.obj.ShopItem;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.event.ShopPurchaseEvent;
import me.randomhashtags.randompackage.event.ShopSellEvent;
import me.randomhashtags.randompackage.perms.ShopPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Shop implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
	public ItemStack back;
	private String defaultShop;
	private HashMap<String, FileShopCategory> titles;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.SHOP_CATEGORY;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null) {
            view(player);
        }
        return true;
    }

	@Override
	public void load() {
	    save("shops", "_settings.yml");
	    final String folder = DATA_FOLDER + SEPARATOR + "shops";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        back = createItemStack(config, "items.back to categories");
        defaultShop = config.getString("settings./shop opens");

        if(!OTHER_YML.getBoolean("saved default shops")) {
            generateDefaultShopCategories();
            OTHER_YML.set("saved default shops", true);
            saveOtherData();
        }

        titles = new HashMap<>();
        for(File f : getFilesInFolder(folder)) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileShopCategory c = new FileShopCategory(f);
                titles.put(c.getTitle(), c);
            }
        }
    }
    @Override
    public void unload() {
    }

    @NotNull
	public BigDecimal getDiscount(@NotNull Player player) {
	    final BigDecimal zero = BigDecimal.ZERO;
	    if(player.hasPermission(ShopPermission.CANCEL_DISCOUNT)) {
	        return zero;
        }
	    BigDecimal value = zero;
        for(int k = 1; k <= 100; k++) {
            if(player.hasPermission(ShopPermission.DISCOUNT_PREFIX + k)) {
                value = BigDecimal.valueOf(k);
            }
        }
        return BigDecimal.valueOf(value.doubleValue()/100);
    }
	public void view(@NotNull Player player) {
	    if(hasPermission(player, ShopPermission.COMMAND, true)) {
            player.closeInventory();
            final UInventory inv = getShopCategory(defaultShop).getInventory();
            player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(inv.getInventory().getContents());
            player.updateInventory();
        }
	}
	public void viewCategory(@NotNull Player player, @NotNull String identifier) {
	    final ShopCategory category = getShopCategory(identifier);
	    if(category != null) {
	        player.closeInventory();
	        final UInventory inv = category.getInventory();
	        player.openInventory(Bukkit.createInventory(player, inv.getSize(), inv.getTitle()));
	        final Inventory top = player.getOpenInventory().getTopInventory();
	        top.setContents(inv.getInventory().getContents());

            final BigDecimal discount = getDiscount(player), one = BigDecimal.ONE;
            final List<String> buyLore = getStringList(config, "lores.purchase"), sellLore = getStringList(config, "lores.sell");
            for(int i = 0; i < top.getSize(); i++) {
                ItemStack item = top.getItem(i);
                if(item != null && !item.equals(back)) {
                    final ShopItem si = category.getItem(i);
                    item = item.clone();
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasLore()) {
                        lore.addAll(itemMeta.getLore());
                    }
                    boolean buy = false, sell = false;
                    BigDecimal buyPrice = si.buyPrice, sellPrice = si.sellPrice;
                    if(buyPrice.doubleValue() > 0.00) {
                        final String price = formatBigDecimal(buyPrice.multiply(discount.doubleValue() > 0 ? discount : one));
                        buy = true;
                        for(String string : buyLore) {
                            lore.add(string.replace("{BUY}", price));
                        }
                    }
                    if(sellPrice.doubleValue() > 0.00) {
                        final String price = formatBigDecimal(sellPrice);
                        sell = true;
                        for(String string : sellLore) {
                            lore.add(string.replace("{SELL}", price));
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
            ItemStack item = is;
            item.setAmount(is.getAmount()*amountPurchased);
            final ShopPurchaseEvent purchaseEvent = new ShopPurchaseEvent(player, shopItem, item, amountPurchased, cost);
            PLUGIN_MANAGER.callEvent(purchaseEvent);
            if(purchaseEvent.isCancelled()) {
                return;
            }
            cost = purchaseEvent.getTotal();
            amountPurchased = purchaseEvent.getAmount();
            item = is;
            item.setAmount(is.getAmount());
            boolean purchased = false;
            if(ECONOMY.withdrawPlayer(player, cost.doubleValue()).transactionSuccess()) {
                purchased = true;
                giveItem(player, item);
                final List<String> commands = shopItem.getExecutedCommands();
                if(commands != null) {
                    final String name = player.getName();
                    for(String command : commands) {
                        SERVER.dispatchCommand(CONSOLE, command.replace("%player%", name));
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
            ItemStack purchasedItem = shopItem.getPurchased();
            final Inventory inv = player.getInventory();
            String msg = "messages.sell" + (!inv.containsAtLeast(purchasedItem, 1) ? " incomplete" : "");
            final BigDecimal price = shopItem.sellPrice;
            int amountSold = clickType.equals("RIGHT") ? purchasedItem.getAmount() : purchasedItem.getMaxStackSize();

            if(!inv.containsAtLeast(purchasedItem, 1)) {
                playSound(config, "sounds.not enough to sell", player, player.getLocation(), false);
            } else {
                final int has = getTotalAmount(inv, UMaterial.match(purchasedItem));
                amountSold = Math.min(has, amountSold);
                if(inv.containsAtLeast(purchasedItem, amountSold)) {
                    BigDecimal profit = price.multiply(BigDecimal.valueOf(amountSold));
                    final ShopSellEvent sellEvent = new ShopSellEvent(player, shopItem, purchasedItem, amountSold, profit);
                    PLUGIN_MANAGER.callEvent(sellEvent);
                    if(sellEvent.isCancelled()) {
                        return;
                    }
                    purchasedItem = sellEvent.getItem();
                    profit = sellEvent.getTotal();
                    amountSold = sellEvent.getAmount();
                    ECONOMY.depositPlayer(player, profit.doubleValue());
                    removeItem(player, purchasedItem, amountSold);
                } else {
                    msg = "messages.sell incomplete";
                }
            }
            final String item = UMaterial.match(purchasedItem).name(), priceString = formatBigDecimal(price), amount = formatInt(amountSold), total = formatBigDecimal(price.multiply(BigDecimal.valueOf(amountSold)));
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player) {
            final FileShopCategory category = titles.getOrDefault(event.getView().getTitle(), null);
            if(category != null) {
                event.setCancelled(true);
                player.updateInventory();
                final int slot = event.getRawSlot();
                final String click = event.getClick().name();
                final ItemStack current = event.getCurrentItem();
                if(slot < 0 || slot >= top.getSize() || !click.contains("LEFT") && !click.contains("RIGHT") || current == null) {
                    return;
                }

                if(current.equals(back)) {
                    view(player);
                } else if(ECONOMY == null) {
                    Bukkit.broadcastMessage("[RandomPackage] An Economy plugin is required to use /shop!");
                    player.closeInventory();
                } else {
                    final ShopItem shopItem = category.getItem(slot);
                    if(shopItem != null) {
                        final String targetCategory = shopItem.opensCategory;
                        if(targetCategory != null) {
                            player.closeInventory();
                            viewCategory(player, targetCategory);
                        } else if(click.endsWith("LEFT")) {
                            tryPurchasing(player, shopItem, click);
                        } else if(click.endsWith("RIGHT")) {
                            trySelling(player, shopItem, click);
                        }
                    }
                }
            }
        }
    }
}