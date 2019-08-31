package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addons.objects.AuctionedItem;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public class AuctionHouse extends RPFeature implements CommandExecutor {
    private static AuctionHouse instance;
    public static AuctionHouse getAuctionHouse() {
        if(instance == null) instance = new AuctionHouse();
        return instance;
    }
    public YamlConfiguration config;

    private File dataF;
    private YamlConfiguration data;

    private long collectionbinExpiration, auctionExpiration;
    public UInventory ah, categories, collectionbin, confirmAuction, purchaseItem, categoryItems;
    private ItemStack previousPage, nextPage, refresh, categoryView, collectionBin, returnToAH, mainCategoryView;
    private int previousPageSlot, nextPageSlot;
    private List<String> clickToBuyStatus, cancelStatus, format, categoryFormat, collectionBinInAuction, collectionBinClaim;
    private String organization;

    private HashMap<Player, AuctionedItem> purchasing;
    private List<Integer> confirmAuctionSlots, cancelAuctionSlots, confirmPurchaseSlots, cancelPurchaseSlots, slots;

    private HashMap<Player, HashMap<ItemStack, BigDecimal>> auctioning;

    public HashMap<UUID, List<AuctionedItem>> auctions;
    public HashMap<Long, AuctionedItem> auctionHouse;
    public HashMap<UMaterial, HashMap<String, List<AuctionedItem>>> category;
    public HashMap<Player, Integer> page;
    public HashMap<Player, String> viewing;
    public HashMap<Player, UMaterial> viewingCategory;

    private HashMap<AuctionedItem, Integer> task;

    public String getIdentifier() { return "AUCTION_HOUSE"; }
    protected RPFeature getFeature() { return getAuctionHouse(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final boolean i = player != null;
        final int l = args.length;
        if(l == 0) {
            if(i) view(player, 1);
        } else if(l == 1) {
            final String a = args[0];
            if(a.equals("sell")) {
            } else if(a.equals("collect")) {
                viewCollectionBin(player);
            }
        } else {
            final String a = args[0], b = args[1];
            if(a.equals("sell")) {
                final BigDecimal price = BigDecimal.valueOf(getRemainingDouble(b));
                final ItemStack is = player.getItemInHand();
                if(is == null || is.getType().equals(Material.AIR)) {
                    sendStringListMessage(player, config.getStringList("messages.need to be holding item"), null);
                } else if(price.doubleValue() <= 0.00) {
                    sendStringListMessage(player, config.getStringList("messages.must enter valid price"), null);
                } else {
                    confirmAuction(player, is, price);
                }
            } else if(a.equals("collect")) {
            }
        }
        return true;
    }

    public void load() {
        save(null, "auction house.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "auction house.yml"));
        save("_Data", "auctions.yml");
        dataF = new File(rpd + separator + "_Data", "auctions.yml");
        data = YamlConfiguration.loadConfiguration(dataF);

        purchasing = new HashMap<>();
        confirmAuctionSlots = new ArrayList<>();
        cancelAuctionSlots = new ArrayList<>();
        confirmPurchaseSlots = new ArrayList<>();
        cancelPurchaseSlots = new ArrayList<>();
        slots = new ArrayList<>();
        auctioning = new HashMap<>();
        auctions = new HashMap<>();
        auctionHouse = new HashMap<>();
        category = new HashMap<>();
        page = new HashMap<>();
        viewing = new HashMap<>();
        viewingCategory = new HashMap<>();
        task = new HashMap<>();

        final ItemStack air = new ItemStack(Material.AIR);
        final String[] itemslots = config.getString("auction house.item slots").split("-");
        for(int i = Integer.parseInt(itemslots[0]); i <= Integer.parseInt(itemslots[1]); i++) {
            slots.add(i);
        }

        organization = config.getString("auction house.organization");
        auctionExpiration = config.getLong("auction house.auction expiration")*1000;
        collectionbinExpiration = config.getLong("auction house.collection bin expiration")*1000;
        format = config.getStringList("auction house.format");
        clickToBuyStatus = colorizeListString(config.getStringList("auction house.status.click to buy"));
        cancelStatus = colorizeListString(config.getStringList("auction house.status.cancel"));
        categoryView = d(config, "auction house.category view");
        collectionBin = d(config, "player collection bin");
        returnToAH = d(config, "return to ah");
        categoryFormat = colorizeListString(config.getStringList("categories.format"));
        mainCategoryView = d(config, "category items.main category view");
        collectionBinClaim = colorizeListString(config.getStringList("collection bin.claim"));
        collectionBinInAuction = colorizeListString(config.getStringList("collection bin.in auction"));

        ah = new UInventory(null, config.getInt("auction house.size"), ChatColor.translateAlternateColorCodes('&', config.getString("auction house.title")));
        previousPage = d(config, "auction house.previous page");
        previousPageSlot = config.getInt("auction house.previous page.slot");
        nextPage = d(config, "auction house.next page");
        nextPageSlot = config.getInt("auction house.next page.slot");
        refresh = d(config, "refresh");

        final Inventory ahi = ah.getInventory();
        for(String s : config.getConfigurationSection("auction house").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("item slots") && !s.equals("organization") && !s.equals("auction expiration") && !s.equals("collection bin expiration") && !s.equals("format") && !s.equals("status") && !s.equals("previous page") && !s.equals("next page")) {
                final String i = config.getString("auction house." + s + ".item");
                item = i.equals("{REFRESH}") ? refresh : i.equals("{COLLECTION_BIN}") ? collectionBin : d(config, "auction house." + s);
                ahi.setItem(config.getInt("auction house." + s + ".slot"), item);
            }
        }

        ahi.setItem(previousPageSlot, previousPage);
        ahi.setItem(nextPageSlot, nextPage);

        purchaseItem = new UInventory(null, config.getInt("purchase item.size"), ChatColor.translateAlternateColorCodes('&', config.getString("purchase item.title")));
        final Inventory pii = purchaseItem.getInventory();
        final ItemStack confirmPurchase = d(config, "purchase item.confirm"), cancelPurchase = d(config, "purchase item.cancel");
        for(String s : config.getConfigurationSection("purchase item").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("confirm") && !s.equals("cancel")) {
                final String i = config.getString("purchase item." + s + ".item").toLowerCase();
                final boolean isC = i.equals("confirm"), isCa = i.equals("cancel"), isI = i.equals("{item}");
                item = isC ? confirmPurchase : isCa ? cancelPurchase : isI ? air : d(config, "purchase item." + s);
                final int slot = config.getInt("purchase item." + s + ".slot");
                if(isC) confirmPurchaseSlots.add(slot);
                else if(isCa) cancelPurchaseSlots.add(slot);
                pii.setItem(slot, item);
            }
        }

        confirmAuction = new UInventory(null, config.getInt("confirm auction.size"), ChatColor.translateAlternateColorCodes('&', config.getString("confirm auction.title")));
        final Inventory cai = confirmAuction.getInventory();
        final ItemStack confirmAuctionAccept = d(config, "confirm auction.accept"), confirmAuctionDecline = d(config, "confirm auction.decline");
        for(String s : config.getConfigurationSection("confirm auction").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("accept") && !s.equals("decline")) {
                final String i = config.getString("confirm auction." + s + ".item");
                final int slot = config.getInt("confirm auction." + s + ".slot");
                final boolean accept = i.equals("accept"), decline = i.equals("decline"), isI = i.equals("{ITEM}");
                item = isI ? air : accept ? confirmAuctionAccept : decline ? confirmAuctionDecline : d(config, "confirm auction." + s);
                if(accept) confirmAuctionSlots.add(slot);
                else if(decline) cancelAuctionSlots.add(slot);
                cai.setItem(slot, item);
            }
        }

        categories = new UInventory(null, config.getInt("categories.size"), ChatColor.translateAlternateColorCodes('&', config.getString("categories.title")));
        final Inventory ci = categories.getInventory();
        for(String s : config.getConfigurationSection("categories").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("format") && !s.equals("groups")) {
                final int slot = config.getInt("categories." + s + ".slot");
                final String t = config.getString("categories." + s + ".item").toLowerCase();
                item = t.equals("{refresh}") ? refresh : t.equals("{collection_bin}") ? collectionBin : t.equals("{return_to_ah}") ? returnToAH : d(config, "categories." + s);
                ci.setItem(slot, item);
            }
        }

        categoryItems = new UInventory(null, config.getInt("category items.size"), ChatColor.translateAlternateColorCodes('&', config.getString("category items.title")));
        final Inventory cii = categoryItems.getInventory();
        for(String s : config.getConfigurationSection("category items").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size")) {
                final int slot = config.getInt("category items." + s + ".slot");
                final String i = config.getString("category items." + s + ".item").toLowerCase();
                item = i.equals("{collection_bin}") ? collectionBin : d(config, "category items." + s);
                cii.setItem(slot, item);
            }
        }

        collectionbin = new UInventory(null, config.getInt("collection bin.size"), ChatColor.translateAlternateColorCodes('&', config.getString("collection bin.title")));
        final Inventory cbi = collectionbin.getInventory();
        for(String s : config.getConfigurationSection("collection bin").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("not enough inventory space") && !s.equals("in auction") && !s.equals("claim")) {
                final int slot = config.getInt("collection bin." + s + ".slot");
                final String i = config.getString("collection bin." + s + ".item").toLowerCase();
                item = i.equals("{refresh}") ? refresh : i.equals("{return_to_ah}") ? returnToAH : d(config, "collection bin." + s);
                cbi.setItem(slot, item);
            }
        }
        loadAuctions(true);
    }

    private void loadAuctions(boolean async) {
        if(async) scheduler.runTaskAsynchronously(randompackage, () -> loadAH(true));
        else loadAH(false);
    }
    private void loadAH(boolean async) {
        if(!isEnabled()) return;
        final ConfigurationSection au = data.getConfigurationSection("auctions");
        int ah = 0, cb = 0, d = 0;
        if(au != null) {
            final long now = System.currentTimeMillis();
            for(String uuid : au.getKeys(false)) {
                final UUID u = UUID.fromString(uuid);
                auctions.put(u, new ArrayList<>());
                final List<AuctionedItem> p = auctions.get(u);
                for(String a : data.getConfigurationSection("auctions." + uuid).getKeys(false)) {
                    final long l = Long.parseLong(a);
                    final ItemStack i = d(data, "auctions." + uuid + "." + a);
                    final AuctionedItem ai = new AuctionedItem(l, u, i, BigDecimal.valueOf(data.getDouble("auctions." + uuid + "." + a + ".price")));
                    ai.claimable = data.getBoolean("auctions." + uuid + "." + a + ".claimable");
                    final boolean c = ai.claimable;
                    boolean deleted = false;
                    if(c && now >= l+collectionbinExpiration) {
                        ai.claimable = false;
                        deleted = true;
                    } else if(!c && now >= l+auctionExpiration) {
                        ai.claimable = true;
                    }
                    if(deleted) {
                        d++;
                    } else {
                        p.add(ai);
                        if(ai.claimable) {
                            cb++;
                        } else {
                            auctionHouse.put(l, ai);
                            ah++;
                            addToCategoryView(ai, UMaterial.match(i));
                        }
                    }
                }
            }
        }
        organizeAH();
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + ah + " Auctioned Items, " + cb + " Collection Bin items, and deleted " + d + " expired items " + (async ? "&e[async]" : ""));
    }
    private void organizeAH() {
        auctionHouse = auctionHouse.entrySet().stream().sorted(organization.equals("OLDEST") ? comparingByKey() : Collections.reverseOrder(comparingByKey())).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        category = category.entrySet().stream().sorted(comparingByKey()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void save() {
        try {
            data.save(dataF);
            data = YamlConfiguration.loadConfiguration(dataF);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void backup(boolean async) {
        if(async) scheduler.runTaskAsynchronously(randompackage, this::dobackup);
        else dobackup();
    }
    private void dobackup() {
        if(!isEnabled()) return;
        data.set("auctions", null);
        for(UUID u : auctions.keySet()) {
            final String s = u.toString(), p = "auctions." + s + ".";
            for(AuctionedItem a : auctions.get(u)) {
                final long l = a.auctionTime;
                final ItemStack i = a.item();
                final int amount = i.getAmount();
                final ItemMeta im = i.getItemMeta();
                data.set(p + l + ".price", a.price);
                data.set(p + l + ".claimable", a.claimable);
                data.set(p + l + ".item", UMaterial.match(i).name());
                if(amount != 1) data.set(p + l + ".amount", amount);
                if(im != null) {
                    if(im.hasDisplayName()) data.set(p + l + ".name", im.getDisplayName());
                    final List<String> lo = new ArrayList<>();
                    if(im.hasEnchants()) {
                        StringBuilder en = new StringBuilder();
                        final Map<Enchantment, Integer> enchants = im.getEnchants();
                        for(Enchantment e : enchants.keySet()) {
                            en.append(e.getName()).append(enchants.get(e)).append(";");
                        }
                        lo.add("VEnchants{" + en.toString().substring(0, en.length()-1) + "}");
                    }
                    if(im.hasLore()) lo.addAll(im.getLore());
                    if(!lo.isEmpty()) data.set(p + l + ".lore", lo);
                }

            }
        }
        save();
    }

    public void unload() {
        backup(false);
        for(Player p : page.keySet()) p.closeInventory();
        for(Player p : viewingCategory.keySet()) p.closeInventory();
        for(AuctionedItem i : task.keySet()) scheduler.cancelTask(task.get(i));
    }


    public void updatePage(Player player) {
        if(viewing.containsKey(player)) {
            final Inventory top = player.getOpenInventory().getTopInventory();
            final ItemStack air = new ItemStack(Material.AIR);
            for(int i : slots) top.setItem(i, air);
            final int p = page.get(player), S = auctionHouse.size()-1, starting = (p-1)*(slots.size()-1);
            final String v = viewing.get(player);
            if(v.equals("CATEGORY_VIEW")) {
                int cat = 0, cate = 0;
                HashMap<UMaterial, HashMap<String, List<AuctionedItem>>> y = new HashMap<>();
                for(UMaterial u : category.keySet()) {
                    y.put(u, new HashMap<>());
                    cate += category.get(u).keySet().size();
                    for(String s : category.get(u).keySet()) {
                        if(cate > starting && slots.contains(cat)) {
                            y.get(u).put(s, category.get(u).get(s));
                            cat++;
                        }
                    }
                }
                y = y.entrySet().stream().sorted(comparingByKey()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
                int slot = (int) slots.toArray()[0];
                for(UMaterial u : y.keySet()) {
                    for(String s : y.get(u).keySet()) {
                        final String listings = Integer.toString(y.get(u).get(s).size());
                        BigDecimal lowestPrice = BigDecimal.ZERO;
                        for(AuctionedItem ai : y.get(u).get(s)) {
                            final BigDecimal price = ai.price;
                            if(lowestPrice.equals(BigDecimal.ZERO) || price.doubleValue() < lowestPrice.doubleValue()) {
                                lowestPrice = price;
                            }
                        }
                        final String lowest = formatBigDecimal(lowestPrice);
                        item = u.getItemStack(); itemMeta = item.getItemMeta(); lore.clear();
                        itemMeta.setDisplayName(s);
                        for(String x : categoryFormat) {
                            lore.add(x.replace("{LISTINGS}", listings).replace("{LOWEST_PRICE}", lowest));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        top.setItem(slot, item);
                        if(slot+1 < slots.size()) slot = (int) slots.toArray()[slot+1];
                    }
                }
                setPages(v, cate, top, air, p);
            } else if(v.equals("AUCTION_HOUSE")) {
                setPages(v, 0, top, air, p);
                final UUID u = player.getUniqueId();
                int ahitem = starting+(p == 1 ? 0 : 1);
                for(int i : slots) {
                    if(ahitem <= S) {
                        final long l = (long) auctionHouse.keySet().toArray()[ahitem];
                        final AuctionedItem a = auctionHouse.get(l);
                        final UUID auctioner = a.auctioner;
                        final String pr = formatBigDecimal(a.price), seller = Bukkit.getOfflinePlayer(auctioner).getName();
                        item = a.item(); itemMeta = item.getItemMeta();
                        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                        for(String s : format) {
                            if(s.equals("{STATUS}")) {
                                lore.addAll(auctioner.equals(u) ? cancelStatus : clickToBuyStatus);
                            } else {
                                lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{PRICE}", pr).replace("{SELLER}", seller)));
                            }
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        top.setItem(i, item);
                        ahitem++;
                    }
                }
            } else if(v.equals("COLLECTION_BIN")) {
                final List<AuctionedItem> cb = getCollectionBin(player);
                int slot = (int) slots.toArray()[0];
                for(AuctionedItem a : cb) {
                    if(slots.contains(slot)) {
                        final String price = formatBigDecimal(a.price);
                        item = a.item(); itemMeta = item.getItemMeta(); lore.clear();
                        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                        final boolean c = a.claimable;
                        final List<String> type = c ? collectionBinClaim : collectionBinInAuction;
                        final String t = getRemainingTime(a.auctionTime+(c ? collectionbinExpiration : auctionExpiration)-System.currentTimeMillis());
                        for(String s : type) {
                            lore.add(s.replace("{PRICE}", price).replace("{TIME}", t));
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        top.setItem(slot, item);
                        if(slot+1 < slots.size()) slot = (int) slots.toArray()[slot+1];
                        else slot = -1;
                    }
                }
                setPages(v, cb.size(), top, air, p);
            }
            for(int i = 0; i < top.getSize(); i++) {
                if(!slots.contains(i)) {
                    item = top.getItem(i);
                    if(item != null && item.equals(collectionBin)) {
                        top.setItem(i, getPlayerCollectionBin(player));
                    }
                }
            }
            player.updateInventory();
        }
    }
    public void updatePage(Player player, Inventory top, UMaterial material, String name) {
        viewingCategory.put(player, material);
        final UUID u = player.getUniqueId();
        int slot = (int) slots.toArray()[0];
        for(AuctionedItem a : category.get(material).get(name)) {
            final UUID auctioner = a.auctioner;
            final String price = formatBigDecimal(a.price), seller = Bukkit.getOfflinePlayer(a.auctioner).getName();
            item = a.item(); itemMeta = item.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            for(String s : format) {
                if(s.equals("{STATUS}")) {
                    lore.addAll(auctioner.equals(u) ? cancelStatus : clickToBuyStatus);
                } else {
                    lore.add(ChatColor.translateAlternateColorCodes('&', s.replace("{PRICE}", price).replace("{SELLER}", seller)));
                }
            }
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            top.setItem(slot, item);
            if(slot+1 < slots.size()) slot = (int) slots.toArray()[slot+1];
        }
        player.updateInventory();
    }

    private void setPages(String type, int size, Inventory top, ItemStack air, int p) {
        final int maxpage = ((type.equals("AUCTION_HOUSE") ? auctionHouse.size() : size)/(slots.size()+1))+1;
        final String max = Integer.toString(maxpage);
        final ItemStack prev = p <= 1 ? air : previousPage.clone(), next = p < maxpage ? nextPage.clone() : air;
        if(prev != air) {
            itemMeta = prev.getItemMeta(); lore.clear();
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PREV_PAGE}", Integer.toString(p-1)).replace("{MAX_PAGE}", max));
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            itemMeta.setLore(lore); lore.clear();
            prev.setItemMeta(itemMeta);
            prev.setAmount(p-1);
        }
        if(next != air) {
            itemMeta = next.getItemMeta(); lore.clear();
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NEXT_PAGE}", Integer.toString(p+1)).replace("{MAX_PAGE}", max));
            if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
            itemMeta.setLore(lore); lore.clear();
            next.setItemMeta(itemMeta);
            next.setAmount(p+1);
        }
        top.setItem(previousPageSlot, prev);
        top.setItem(nextPageSlot, next);
    }

    public void view(Player player, int page) {
        if(hasPermission(player, "RandomPackage.ah.view", true)) {
            player.closeInventory();
            this.page.put(player, page);
            viewing.put(player, "AUCTION_HOUSE");
            final int size = ah.getSize();
            player.openInventory(Bukkit.createInventory(player, size, ah.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(ah.getInventory().getContents());
            updatePage(player);
        }
    }
    public void viewCategories(Player player) {
        if(hasPermission(player, "RandomPackage.ah.view.categories", true)) {
            player.closeInventory();
            page.put(player, 1);
            viewing.put(player, "CATEGORY_VIEW");
            final int size = categories.getSize();
            player.openInventory(Bukkit.createInventory(player, size, categories.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(categories.getInventory().getContents());
            updatePage(player);
        }
    }
    public void viewCategory(Player player, UMaterial material, String name) {
        if(hasPermission(player, "RandomPackage.ah.view.category", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(null, categoryItems.getSize(), categoryItems.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(categoryItems.getInventory().getContents());
            updatePage(player, top, material, name);
        }
    }
    public void viewCollectionBin(Player player) {
        if(hasPermission(player, "RandomPackage.ah.view.collectionbin", true)) {
            player.closeInventory();
            page.put(player, 1);
            viewing.put(player, "COLLECTION_BIN");
            player.openInventory(Bukkit.createInventory(player, collectionbin.getSize(), collectionbin.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(collectionbin.getInventory().getContents());
            updatePage(player);
        }
    }
    public void nextPage(Player player) {
        if(viewing.containsKey(player)) {
            page.put(player, page.get(player)+1);
            updatePage(player);
        }
    }
    public void previousPage(Player player) {
        if(viewing.containsKey(player)) {
            page.put(player, page.get(player)-1);
            updatePage(player);
        }
    }
    public void expire(Player player, AuctionedItem a) {
        final ItemStack i = a.item();
        if(a.claimable) {
            giveItem(player, i);
            auctions.get(player.getUniqueId()).remove(a);
        } else {
            a.claimable = true;
            auctionHouse.remove(a.auctionTime);
            category.get(UMaterial.match(i)).get(i.getItemMeta().getDisplayName()).remove(a);
            a.auctionTime = System.currentTimeMillis();
        }
        updatePage(player);
    }

    public void confirmAuction(Player player, ItemStack item, BigDecimal price) {
        if(hasPermission(player, "RandomPackage.ah.sell", true)) {
            final String p = formatBigDecimal(price);
            player.closeInventory();

            player.openInventory(Bukkit.createInventory(player, confirmAuction.getSize(), confirmAuction.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(confirmAuction.getInventory().getContents());
            final String i = toMaterial(item.getType().name(), false);

            for(ItemStack is : top.getContents()) {
                if(is != null) {
                    itemMeta = is.getItemMeta(); lore.clear();
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            s = s.replace("{PRICE}", p).replace("{ITEM}", i);
                            lore.add(s);
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    is.setItemMeta(itemMeta);
                }
            }
            top.setItem(top.firstEmpty(), item);
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
            player.updateInventory();
            auctioning.put(player, new HashMap<>());
            auctioning.get(player).put(item, price);
        }
    }
    public void auction(Player player, ItemStack item, BigDecimal price) {
        if(hasPermission(player, "RandomPackage.ah.auction", true)) {
            final UUID u = player.getUniqueId();
            if(!auctions.containsKey(u)) auctions.put(u, new ArrayList<>());
            final long l = System.currentTimeMillis();
            final UMaterial um = UMaterial.match(item);
            final AuctionedItem a = new AuctionedItem(l, u, item, price);
            auctions.get(u).add(a);
            auctionHouse.put(l, a);
            addToCategoryView(a, um);
            organizeAH();
            final String p = formatBigDecimal(price), i = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : toMaterial(um.name(), false);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{PRICE}", p);
            replacements.put("{ITEM}", i);
            sendStringListMessage(player, config.getStringList("messages.listed"), replacements);
        }
    }
    private void addToCategoryView(AuctionedItem ai, UMaterial um) {
        final ItemStack i = ai.item();
        if(!category.containsKey(um)) category.put(um, new HashMap<>());
        final String dn = i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : null;
        final HashMap<String, List<AuctionedItem>> m = category.get(um);
        if(!m.containsKey(dn)) m.put(dn, new ArrayList<>());
        m.get(dn).add(ai);
    }
    public void cancelAuction(Player player, AuctionedItem a) {
        auctionHouse.remove(a.auctionTime);
        a.claimable = true;
        sendStringListMessage(player, config.getStringList("messages.cancelled listing"), null);
    }
    public void tryPurchasing(Player player, AuctionedItem a) {
        if(a != null && hasPermission(player, "RandomPackage.ah.buy", true)) {
            player.closeInventory();

            final ItemStack its = a.item();
            final String p = formatBigDecimal(a.price), it = its.hasItemMeta() && its.getItemMeta().hasDisplayName() ? its.getItemMeta().getDisplayName() : toMaterial(UMaterial.match(its).name(), false);
            final int size = purchaseItem.getSize();
            player.openInventory(Bukkit.createInventory(player, size, purchaseItem.getTitle().replace("{PRICE}", p)));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(purchaseItem.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
                if(item != null) {
                    itemMeta = item.getItemMeta(); lore.clear();
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{PRICE}", p).replace("{ITEM}", it));
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
            }
            top.setItem(top.firstEmpty(), its);
            player.updateInventory();
            purchasing.put(player, a);
        }
    }

    public List<AuctionedItem> getCollectionBin(Player player) {
        return auctions.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    public ItemStack getPlayerCollectionBin(Player player) {
        final String size = Integer.toString(getCollectionBin(player).size());
        item = collectionBin.clone(); itemMeta = item.getItemMeta();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{ITEMS}", size));
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    public AuctionedItem valueOf(Player player, int slot, String type) {
        final String T = type;
        type = type.toUpperCase();
        final int page = this.page.getOrDefault(player, 0), p = (page-1)*slots.size();
        if(type.equals("COLLECTION_BIN")) {
            final List<AuctionedItem> bin = getCollectionBin(player);
            return bin.size() > p+slot ? (AuctionedItem) bin.toArray()[p+slot] : null;
        } else if(type.equals("AUCTION_HOUSE")) {
            final Collection<AuctionedItem> ah = auctionHouse.values();
            return ah.size() > p+slot ? (AuctionedItem) ah.toArray()[p+slot] : null;
        } else if(type.startsWith("CATEGORY")) {
            final UMaterial u = viewingCategory.get(player);
            final String a = "CATEGORY_" + u.name() + "_";
            final String[] b = T.split(a);
            String s = b.length == 1 ? null : b[1];
            final List<AuctionedItem> i = category.get(u).get(s);
            return slot < i.size() ? i.get(slot) : null;
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String t = event.getView().getTitle();
        final boolean isAH = t.equals(ah.getTitle()), isC = t.equals(categories.getTitle()), isCB = t.equals(collectionbin.getTitle()), isCA = t.equals(confirmAuction.getTitle()), isP = purchasing.containsKey(player), isCV = t.equals(categoryItems.getTitle());
        if(isAH || isC || isCB || isCA || isP || isCV) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;

            final UUID u = player.getUniqueId();
            final String click = event.getClick().name();

            if(isAH || isC || isCB || isCV) {
                final boolean n = r == nextPageSlot, p = r == previousPageSlot;
                if(n || p) {
                    if(n) nextPage(player);
                    else previousPage(player);
                    return;
                } else if(c.equals(getPlayerCollectionBin(player))) {
                    player.closeInventory();
                    viewCollectionBin(player);
                    return;
                } else if(c.equals(refresh)) {
                    updatePage(player);
                    return;
                } else if(c.equals(returnToAH)) {
                    player.closeInventory();
                    view(player, 1);
                    return;
                } else if(c.equals(mainCategoryView)) {
                    player.closeInventory();
                    viewCategories(player);
                    return;
                }
            }

            if(isP) {
                final AuctionedItem ai = purchasing.get(player);
                final ItemStack z = ai != null ? ai.item() : null;
                final UUID OPU = ai != null ? ai.auctioner : null;
                final OfflinePlayer OP = ai != null ? Bukkit.getOfflinePlayer(OPU) : null;
                final HashMap<String, String> replacements = new HashMap<>();
                final BigDecimal price = ai != null ? ai.price : BigDecimal.ZERO;
                final double priceDouble = price.doubleValue();
                final String p = formatBigDecimal(price);
                replacements.put("{PRICE}", p);
                replacements.put("{ITEM}", z != null ? z.hasItemMeta() && z.getItemMeta().hasDisplayName() ? z.getItemMeta().getDisplayName() : toMaterial(UMaterial.match(z).name(), false) : "");
                replacements.put("{PURCHASER}", player.getName());
                replacements.put("{SELLER}", ai != null ? OP.getName() : "");
                if(confirmPurchaseSlots.contains(r)) {
                    purchasing.remove(player);
                    if(ai == null) {
                        sendStringListMessage(player, config.getStringList("messages.item no longer exists"), replacements);
                    } else if(OPU.equals(u)) {
                        player.closeInventory();
                        sendStringListMessage(player, config.getStringList("messages.cannot purchase own item"), replacements);
                        view(player, 1);
                        return;
                    } else if(eco.withdrawPlayer(player, priceDouble).transactionSuccess()) {
                        sendStringListMessage(player, config.getStringList("messages.purchased auction"), replacements);
                        giveItem(player, z);
                        auctionHouse.remove(ai.auctionTime);
                        auctions.get(OPU).remove(ai);
                        if(OP.isOnline()) {
                            sendStringListMessage(OP.getPlayer(), config.getStringList("messages.sold auction"), replacements);
                        }
                        eco.depositPlayer(OP, priceDouble);
                    } else {
                        sendStringListMessage(player, config.getStringList("messages.cannot afford"), replacements);
                    }
                } else if(cancelPurchaseSlots.contains(r)) {
                } else return;
                player.closeInventory();
            } else if(isAH) {
                if(slots.contains(r)) {
                    final AuctionedItem target = valueOf(player, r, "AUCTION_HOUSE");
                    if(target != null) {
                        if(target.auctioner.equals(u) && click.equals("SHIFT_RIGHT")) {
                            cancelAuction(player, target);
                            updatePage(player);
                        } else {
                            tryPurchasing(player, target);
                        }
                    }
                } else if(c.equals(categoryView)) {
                    player.closeInventory();
                    viewCategories(player);
                }
            } else if(isC) {
                player.closeInventory();
                viewCategory(player, UMaterial.match(c), c.getItemMeta().hasDisplayName() ? c.getItemMeta().getDisplayName() : null);
            } else if(isCA) {
                final HashMap<ItemStack, BigDecimal> i = auctioning.get(player);
                final ItemStack it = (ItemStack) i.keySet().toArray()[0];
                final BigDecimal price = i.get(it);
                if(confirmAuctionSlots.contains(r)) {
                    auction(player, it, price);
                    auctioning.remove(player);
                } else if(!cancelAuctionSlots.contains(r)) {
                    return;
                }
                player.closeInventory();
                player.updateInventory();
            } else if(isCV) {
                if(slots.contains(r)) {
                    final UMaterial um = UMaterial.match(c);
                    final AuctionedItem a = valueOf(player, slots.indexOf(r), "CATEGORY_" + um.name() + (c.getItemMeta().hasDisplayName() ? "_" + c.getItemMeta().getDisplayName() : ""));
                    if(a != null) {
                        tryPurchasing(player, a);
                    }
                }
            } else { // Collection Bin
                if(slots.contains(r)) {
                    expire(player, valueOf(player, r, "COLLECTION_BIN"));
                }
            }
        }
    }

    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        viewing.remove(player);
        viewingCategory.remove(player);
        page.remove(player);
        if(auctioning != null && auctioning.containsKey(player)) {
            giveItem(player, (ItemStack) auctioning.get(player).keySet().toArray()[0]);
            auctioning.remove(player);
            sendStringListMessage(player, config.getStringList("messages.auction sell cancelled"), null);
            player.updateInventory();
        } else if(purchasing != null && purchasing.containsKey(player)) {
            purchasing.remove(player);
            sendStringListMessage(player, config.getStringList("messages.auction purchase cancelled"), null);
        }
    }
}
