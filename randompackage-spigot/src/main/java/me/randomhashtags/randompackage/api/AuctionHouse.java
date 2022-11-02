package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.obj.AuctionedItem;
import me.randomhashtags.randompackage.perms.AuctionHousePermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toMap;

public enum AuctionHouse implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final boolean isPlayer = player != null;
        final int l = args.length;
        if(l == 0) {
            if(isPlayer) view(player, 1);
        } else {
            final String a = args[0], arg1 = l >= 2 ? args[1] : null;
            switch (a) {
                case "sell":
                    if(arg1 != null) {
                        final BigDecimal price = BigDecimal.valueOf(getRemainingDouble(arg1));
                        final ItemStack is = player.getItemInHand();
                        if(is.getType().equals(Material.AIR)) {
                            sendStringListMessage(player, getStringList(config, "messages.need to be holding item"), null);
                        } else if(price.doubleValue() <= 0.00) {
                            sendStringListMessage(player, getStringList(config, "messages.must enter valid price"), null);
                        } else {
                            confirmAuction(player, is, price);
                        }
                    }
                    break;
                case "collect":
                    viewCollectionBin(player);
                    break;
                case "help":
                    viewHelp(sender);
                    break;
                default:
                    if(isPlayer) view(player, 1);
                    break;
            }
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "auction house.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "auction house.yml"));
        save("_Data", "auctions.yml");
        dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "auctions.yml");
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
        final String[] values = config.getString("auction house.item slots").split("-");
        for(int i = Integer.parseInt(values[0]); i <= Integer.parseInt(values[1]); i++) {
            slots.add(i);
        }

        organization = config.getString("auction house.organization");
        auctionExpiration = config.getLong("auction house.auction expiration")*1000;
        collectionbinExpiration = config.getLong("auction house.collection bin expiration")*1000;
        format = colorizeListString(config.getStringList("auction house.format"));
        clickToBuyStatus = colorizeListString(config.getStringList("auction house.status.click to buy"));
        cancelStatus = colorizeListString(config.getStringList("auction house.status.cancel"));
        categoryView = createItemStack(config, "auction house.category view");
        collectionBin = createItemStack(config, "player collection bin");
        returnToAH = createItemStack(config, "return to ah");
        categoryFormat = colorizeListString(config.getStringList("categories.format"));
        mainCategoryView = createItemStack(config, "category items.main category view");
        collectionBinClaim = colorizeListString(config.getStringList("collection bin.claim"));
        collectionBinInAuction = colorizeListString(config.getStringList("collection bin.in auction"));

        ah = new UInventory(null, config.getInt("auction house.size"), colorize(config.getString("auction house.title")));
        previousPage = createItemStack(config, "auction house.previous page");
        previousPageSlot = config.getInt("auction house.previous page.slot");
        nextPage = createItemStack(config, "auction house.next page");
        nextPageSlot = config.getInt("auction house.next page.slot");
        refresh = createItemStack(config, "refresh");

        final Inventory ahInventory = ah.getInventory();
        setupInventory("auction house", ahInventory, Arrays.asList("title", "size", "item slots", "organization", "auction expiration", "collection bin expiration", "format", "status", "previous page", "next page"), new HashMap<String, ItemStack>() {{
            put("{REFRESH}", refresh);
            put("{COLLECTION_BIN}", collectionBin);
        }});

        ahInventory.setItem(previousPageSlot, previousPage);
        ahInventory.setItem(nextPageSlot, nextPage);

        purchaseItem = new UInventory(null, config.getInt("purchase item.size"), colorize(config.getString("purchase item.title")));
        final Inventory pii = purchaseItem.getInventory();
        final ItemStack confirmPurchase = createItemStack(config, "purchase item.confirm"), cancelPurchase = createItemStack(config, "purchase item.cancel");
        for(String s : config.getConfigurationSection("purchase item").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("confirm") && !s.equals("cancel")) {
                final String i = config.getString("purchase item." + s + ".item").toLowerCase();
                final boolean isConfirm = i.equals("confirm"), isCancel = i.equals("cancel"), isItem = i.equals("{item}");
                final ItemStack item = isConfirm ? confirmPurchase : isCancel ? cancelPurchase : isItem ? air : createItemStack(config, "purchase item." + s);
                final int slot = config.getInt("purchase item." + s + ".slot");
                if(isConfirm) {
                    confirmPurchaseSlots.add(slot);
                } else if(isCancel) {
                    cancelPurchaseSlots.add(slot);
                }
                pii.setItem(slot, item);
            }
        }

        confirmAuction = new UInventory(null, config.getInt("confirm auction.size"), colorize(config.getString("confirm auction.title")));
        final Inventory cai = confirmAuction.getInventory();
        final ItemStack confirmAuctionAccept = createItemStack(config, "confirm auction.accept"), confirmAuctionDecline = createItemStack(config, "confirm auction.decline");
        for(String s : config.getConfigurationSection("confirm auction").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size") && !s.equals("accept") && !s.equals("decline")) {
                final String i = config.getString("confirm auction." + s + ".item");
                final int slot = config.getInt("confirm auction." + s + ".slot");
                final boolean accept = i.equals("accept"), decline = i.equals("decline"), isI = i.equals("{ITEM}");
                final ItemStack item = isI ? air : accept ? confirmAuctionAccept : decline ? confirmAuctionDecline : createItemStack(config, "confirm auction." + s);
                if(accept) {
                    confirmAuctionSlots.add(slot);
                } else if(decline) {
                    cancelAuctionSlots.add(slot);
                }
                cai.setItem(slot, item);
            }
        }

        categories = new UInventory(null, config.getInt("categories.size"), colorize(config.getString("categories.title")));
        setupInventory("categories", categories.getInventory(), Arrays.asList("title", "size", "format", "groups"), new HashMap<String, ItemStack>() {{
            put("{REFRESH}", refresh);
            put("{COLLECTION_BIN}", collectionBin);
            put("{RETURN_TO_AH}", returnToAH);
        }});

        categoryItems = new UInventory(null, config.getInt("category items.size"), colorize(config.getString("category items.title")));
        setupInventory("category items", categoryItems.getInventory(), Arrays.asList("title", "size"), new HashMap<String, ItemStack>() {{
            put("{COLLECTION_BIN}", collectionBin);
        }});

        collectionbin = new UInventory(null, config.getInt("collection bin.size"), colorize(config.getString("collection bin.title")));
        setupInventory("collection bin", collectionbin.getInventory(), Arrays.asList("title", "size", "not enough inventory space", "in auction", "claim"), new HashMap<String, ItemStack>() {{
            put("{REFRESH}", refresh);
            put("{RETURN_TO_AH}", returnToAH);
        }});

        sendConsoleDidLoadFeature("Auction House", started);

        loadAuctions(true);
    }
    private void setupInventory(String identifier, Inventory inventory, List<String> excludedKeys, HashMap<String, ItemStack> specialItems) {
        for(String key : getConfigurationSectionKeys(config, identifier, false)) {
            if(!excludedKeys.contains(key)) {
                final int slot = config.getInt(identifier + "." + key + ".slot");
                final String targetItem = config.getString(identifier + "." + key + ".item");
                final ItemStack item = specialItems.containsKey(targetItem) ? specialItems.get(targetItem): createItemStack(config, identifier + "." + key);
                inventory.setItem(slot, item);
            }
        }
    }

    private void loadAuctions(boolean async) {
        if(async) {
            SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, () -> loadAH(true));
        } else {
            loadAH(false);
        }
    }
    private void loadAH(boolean async) {
        final long started = System.currentTimeMillis();
        if(!isEnabled()) {
            return;
        }
        final ConfigurationSection au = data.getConfigurationSection("auctions");
        int ah = 0, cb = 0, d = 0;
        if(au != null) {
            final long now = System.currentTimeMillis();
            for(String uuid : au.getKeys(false)) {
                final UUID u = UUID.fromString(uuid);
                auctions.put(u, new ArrayList<>());
                final List<AuctionedItem> p = auctions.get(u);
                for(String auctionedTime : getConfigurationSectionKeys(data, "auctions." + uuid, false)) {
                    final long time = Long.parseLong(auctionedTime);
                    final ItemStack i = data.getItemStack("auctions." + uuid + "." + auctionedTime + ".item");
                    final AuctionedItem auction = new AuctionedItem(time, u, i, BigDecimal.valueOf(data.getDouble("auctions." + uuid + "." + auctionedTime + ".price")));
                    auction.claimable = data.getBoolean("auctions." + uuid + "." + auctionedTime + ".claimable");
                    final boolean isClaimable = auction.claimable;
                    boolean deleted = false;
                    if(isClaimable && now >= time+collectionbinExpiration) {
                        auction.claimable = false;
                        deleted = true;
                    } else if(!isClaimable && now >= time+auctionExpiration) {
                        auction.claimable = true;
                    }
                    if(deleted) {
                        d++;
                    } else {
                        p.add(auction);
                        if(auction.claimable) {
                            cb++;
                        } else {
                            auctionHouse.put(time, auction);
                            ah++;
                            addToCategoryView(auction, UMaterial.match(i));
                        }
                    }
                }
            }
        }
        organizeAH();
        final String msg = ah + " Auctioned Items, " + cb + " Collection Bin items, and deleted " + d + " expired items";
        if(async) {
            sendConsoleDidLoadAsyncFeature(msg);
        } else {
            sendConsoleDidLoadFeature(msg, started);
        }
    }
    private void organizeAH() {
        auctionHouse = auctionHouse.entrySet().stream().sorted(organization.equals("OLDEST") ? comparingByKey() : Collections.reverseOrder(comparingByKey())).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
        category = category.entrySet().stream().sorted(comparingByKey()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
    }

    private void save() {
        try {
            data.save(dataF);
            dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "auctions.yml");
            data = YamlConfiguration.loadConfiguration(dataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void backup(boolean async) {
        if(async) {
            SCHEDULER.runTaskAsynchronously(RANDOM_PACKAGE, this::dobackup);
        } else {
            dobackup();
        }
    }
    private void dobackup() {
        data.set("auctions", null);
        for(UUID uuid : auctions.keySet()) {
            final String s = uuid.toString(), key = "auctions." + s + ".";
            for(AuctionedItem auction : auctions.get(uuid)) {
                final long l = auction.auctionTime;
                final String path = key + l + ".";
                data.set(path + "price", auction.price);
                data.set(path + "claimable", auction.claimable);
                data.set(path + "item", auction.item());
            }
        }
        save();
    }

    @Override
    public void unload() {
        backup(false);
        for(Player player : new ArrayList<>(page.keySet())) {
            player.closeInventory();
        }
        for(Player player : new ArrayList<>(viewingCategory.keySet())) {
            player.closeInventory();
        }
        for(AuctionedItem i : task.keySet()) {
            SCHEDULER.cancelTask(task.get(i));
        }
    }

    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, AuctionHousePermission.HELP, true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void updatePage(@NotNull Player player) {
        if(viewing.containsKey(player)) {
            final Inventory top = player.getOpenInventory().getTopInventory();
            final ItemStack air = new ItemStack(Material.AIR);
            for(int i : slots) {
                top.setItem(i, air);
            }
            final int page = this.page.get(player), S = auctionHouse.size()-1, starting = (page-1)*(slots.size()-1);
            final String viewingPage = viewing.get(player);
            ItemStack item = null;
            ItemMeta itemMeta = null;
            final List<String> lore = new ArrayList<>();
            switch (viewingPage) {
                case "CATEGORY_VIEW":
                    int cat = 0, cate = 0;
                    HashMap<UMaterial, HashMap<String, List<AuctionedItem>>> limitedCategories = new HashMap<>();
                    for(UMaterial material : category.keySet()) {
                        final HashMap<String, List<AuctionedItem>> auctionedItems = category.get(material);
                        final HashMap<String, List<AuctionedItem>> map = new HashMap<>();
                        cate += auctionedItems.size();
                        for(String key : auctionedItems.keySet()) {
                            if(cate > starting && slots.contains(cat)) {
                                map.put(key, auctionedItems.get(key));
                                cat++;
                            }
                        }
                        limitedCategories.put(material, map);
                    }
                    limitedCategories = limitedCategories.entrySet().stream().sorted(comparingByKey()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
                    int slot = (int) slots.toArray()[0];
                    for(UMaterial material : limitedCategories.keySet()) {
                        final HashMap<String, List<AuctionedItem>> auctionedItems = limitedCategories.get(material);
                        final Set<String> keys = auctionedItems.keySet();
                        for(String key : keys) {
                            final List<AuctionedItem> items = auctionedItems.get(key);
                            final String listings = Integer.toString(items.size());
                            BigDecimal lowestPrice = BigDecimal.ZERO;
                            for(AuctionedItem ai : items) {
                                final BigDecimal price = ai.price;
                                if(lowestPrice.equals(BigDecimal.ZERO) || price.doubleValue() < lowestPrice.doubleValue()) {
                                    lowestPrice = price;
                                }
                            }
                            final String lowest = formatBigDecimal(lowestPrice);
                            item = material.getItemStack();
                            itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName(key);
                            for(String x : categoryFormat) {
                                lore.add(x.replace("{LISTINGS}", listings).replace("{LOWEST_PRICE}", lowest));
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            top.setItem(slot, item);
                            if(slot+1 < slots.size()) {
                                slot = (int) slots.toArray()[slot+1];
                            }
                        }
                    }
                    setPages(viewingPage, cate, top, air, page);
                    break;
                case "AUCTION_HOUSE":
                    setPages(viewingPage, 0, top, air, page);
                    final UUID uuid = player.getUniqueId();
                    int ahitem = starting+(page == 1 ? 0 : 1);
                    for(int i : slots) {
                        if(ahitem <= S) {
                            final long l = (long) auctionHouse.keySet().toArray()[ahitem];
                            final AuctionedItem auctionedItem = auctionHouse.get(l);
                            final UUID auctioner = auctionedItem.auctioner;
                            final String price = formatBigDecimal(auctionedItem.price), seller = Bukkit.getOfflinePlayer(auctioner).getName();
                            item = auctionedItem.item();
                            itemMeta = item.getItemMeta();
                            if(itemMeta.hasLore()) {
                                lore.addAll(itemMeta.getLore());
                            }
                            for(String s : format) {
                                if(s.equals("{STATUS}")) {
                                    lore.addAll(auctioner.equals(uuid) ? cancelStatus : clickToBuyStatus);
                                } else {
                                    lore.add(s.replace("{PRICE}", price).replace("{SELLER}", seller));
                                }
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            top.setItem(i, item);
                            ahitem++;
                        }
                    }
                    break;
                case "COLLECTION_BIN":
                    final List<AuctionedItem> collectionBin = getCollectionBin(player);
                    slot = (int) slots.toArray()[0];
                    for(AuctionedItem auction : collectionBin) {
                        if(slots.contains(slot)) {
                            final String price = formatBigDecimal(auction.price);
                            item = auction.item();
                            itemMeta = item.getItemMeta();
                            if(itemMeta.hasLore()) {
                                lore.addAll(itemMeta.getLore());
                            }
                            final boolean isClaimable = auction.claimable;
                            final String remainingTime = getRemainingTime(auction.auctionTime+(isClaimable ? collectionbinExpiration : auctionExpiration)-System.currentTimeMillis());
                            for(String s : isClaimable ? collectionBinClaim : collectionBinInAuction) {
                                lore.add(s.replace("{PRICE}", price).replace("{TIME}", remainingTime));
                            }
                            itemMeta.setLore(lore);
                            item.setItemMeta(itemMeta);
                            top.setItem(slot, item);
                            if(slot+1 < slots.size()) {
                                slot = (int) slots.toArray()[slot+1];
                            } else {
                                slot = -1;
                            }
                        }
                    }
                    setPages(viewingPage, collectionBin.size(), top, air, page);
                    break;
                default:
                    break;
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
    public void updatePage(@NotNull Player player, @NotNull Inventory top, @NotNull UMaterial material, @Nullable String name) {
        viewingCategory.put(player, material);
        final UUID u = player.getUniqueId();
        int slot = (int) slots.toArray()[0];
        for(AuctionedItem auction : category.get(material).get(name)) {
            final UUID auctioner = auction.auctioner;
            final String price = formatBigDecimal(auction.price), seller = Bukkit.getOfflinePlayer(auction.auctioner).getName();
            final ItemStack item = auction.item();
            final ItemMeta itemMeta = item.getItemMeta();
            final List<String> lore = new ArrayList<>();
            if(itemMeta.hasLore()) {
                lore.addAll(itemMeta.getLore());
            }
            for(String s : format) {
                if(s.equals("{STATUS}")) {
                    lore.addAll(auctioner.equals(u) ? cancelStatus : clickToBuyStatus);
                } else {
                    lore.add(colorize(s.replace("{PRICE}", price).replace("{SELLER}", seller)));
                }
            }
            itemMeta.setLore(lore); lore.clear();
            item.setItemMeta(itemMeta);
            top.setItem(slot, item);
            if(slot+1 < slots.size()) {
                slot = (int) slots.toArray()[slot+1];
            }
        }
        player.updateInventory();
    }
    private void setPages(String type, int size, Inventory top, ItemStack air, int p) {
        final int maxpage = ((type.equals("AUCTION_HOUSE") ? auctionHouse.size() : size)/(slots.size()+1))+1;
        final String max = Integer.toString(maxpage);
        final ItemStack prev = p <= 1 ? air : previousPage.clone(), next = p < maxpage ? nextPage.clone() : air;
        ItemMeta itemMeta = null;
        final List<String> lore = new ArrayList<>();
        if(prev != air) {
            itemMeta = prev.getItemMeta();
            if(itemMeta != null) {
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{PREV_PAGE}", Integer.toString(p-1)).replace("{MAX_PAGE}", max));
                if(itemMeta.hasLore()) {
                    lore.addAll(itemMeta.getLore());
                }
                itemMeta.setLore(lore);
                lore.clear();
                prev.setItemMeta(itemMeta);
            }
            prev.setAmount(p-1);
        }
        if(next != air) {
            itemMeta = next.getItemMeta();
            if(itemMeta != null) {
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{NEXT_PAGE}", Integer.toString(p+1)).replace("{MAX_PAGE}", max));
                if(itemMeta.hasLore()) {
                    lore.addAll(itemMeta.getLore());
                }
                itemMeta.setLore(lore);
            }
            next.setItemMeta(itemMeta);
            next.setAmount(p+1);
        }
        top.setItem(previousPageSlot, prev);
        top.setItem(nextPageSlot, next);
    }
    public void view(@NotNull Player player, int page) {
        if(hasPermission(player, AuctionHousePermission.VIEW, true)) {
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
    public void viewCategories(@NotNull Player player) {
        if(hasPermission(player, AuctionHousePermission.VIEW_CATEGORIES, true)) {
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
    public void viewCategory(@NotNull Player player, @NotNull UMaterial material, @Nullable String name) {
        if(hasPermission(player, AuctionHousePermission.VIEW_CATEGORY, true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(null, categoryItems.getSize(), categoryItems.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(categoryItems.getInventory().getContents());
            updatePage(player, top, material, name);
        }
    }
    public void viewCollectionBin(@NotNull Player player) {
        if(hasPermission(player, AuctionHousePermission.VIEW_COLLECTION_BIN, true)) {
            player.closeInventory();
            page.put(player, 1);
            viewing.put(player, "COLLECTION_BIN");
            player.openInventory(Bukkit.createInventory(player, collectionbin.getSize(), collectionbin.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(collectionbin.getInventory().getContents());
            updatePage(player);
        }
    }
    public void nextPage(@NotNull Player player) {
        if(viewing.containsKey(player)) {
            page.put(player, page.get(player)+1);
            updatePage(player);
        }
    }
    public void previousPage(@NotNull Player player) {
        if(viewing.containsKey(player)) {
            page.put(player, page.get(player)-1);
            updatePage(player);
        }
    }
    public void tryCancelling(@NotNull Player player, @NotNull AuctionedItem auction) {
        final ItemStack i = auction.item();
        if(auction.claimable) {
            giveItem(player, i);
            auctions.get(player.getUniqueId()).remove(auction);
        } else {
            final UMaterial u = UMaterial.match(i);
            final String name = i.hasItemMeta() && i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : null;
            auction.claimable = true;
            final HashMap<String, List<AuctionedItem>> category = this.category.get(u);
            final List<AuctionedItem> auctioned = category.get(name);
            auctioned.remove(auction);
            if(auctioned.size() == 0) {
                category.remove(name);
            }
            auctionHouse.remove(auction.auctionTime);
            auction.auctionTime = System.currentTimeMillis();
            sendStringListMessage(player, getStringList(config, "messages.cancelled listing"), null);
        }
        updatePage(player);
    }

    public void confirmAuction(@NotNull Player player, @NotNull ItemStack item, @NotNull BigDecimal price) {
        if(hasPermission(player, AuctionHousePermission.VIEW_SELL, true)) {
            final String priceString = formatBigDecimal(price);
            player.closeInventory();

            player.openInventory(Bukkit.createInventory(player, confirmAuction.getSize(), confirmAuction.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(confirmAuction.getInventory().getContents());
            final String i = toMaterial(item.getType().name(), false);

            for(ItemStack is : top.getContents()) {
                if(is != null) {
                    final ItemMeta itemMeta = is.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasLore()) {
                        for(String string : itemMeta.getLore()) {
                            string = string.replace("{PRICE}", priceString).replace("{ITEM}", i);
                            lore.add(string);
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
    public void auction(@NotNull Player player, @NotNull ItemStack item, @NotNull BigDecimal price) {
        if(hasPermission(player, AuctionHousePermission.AUCTION_ITEM, true)) {
            final UUID uuid = player.getUniqueId();
            if(!auctions.containsKey(uuid)) {
                auctions.put(uuid, new ArrayList<>());
            }
            final long l = System.currentTimeMillis();
            final UMaterial um = UMaterial.match(item);
            final AuctionedItem a = new AuctionedItem(l, uuid, item, price);
            auctions.get(uuid).add(a);
            auctionHouse.put(l, a);
            addToCategoryView(a, um);
            organizeAH();
            final String p = formatBigDecimal(price), i = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : toMaterial(um.name(), false);
            final HashMap<String, String> replacements = new HashMap<>();
            replacements.put("{PRICE}", p);
            replacements.put("{ITEM}", i);
            sendStringListMessage(player, getStringList(config, "messages.listed"), replacements);
        }
    }
    private void addToCategoryView(AuctionedItem auction, UMaterial material) {
        final ItemStack is = auction.item();
        if(!category.containsKey(material)) {
            category.put(material, new HashMap<>());
        }
        final String name = is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : null;
        final HashMap<String, List<AuctionedItem>> category = this.category.get(material);
        if(!category.containsKey(name)) {
            category.put(name, new ArrayList<>());
        }
        category.get(name).add(auction);
    }
    public void tryPurchasing(@NotNull Player player, @NotNull AuctionedItem auction) {
        if(hasPermission(player, AuctionHousePermission.BUY_AUCTION, true)) {
            player.closeInventory();

            final ItemStack auctionItem = auction.item();
            final String price = formatBigDecimal(auction.price), material = auctionItem.hasItemMeta() && auctionItem.getItemMeta().hasDisplayName() ? auctionItem.getItemMeta().getDisplayName() : toMaterial(UMaterial.match(auctionItem).name(), false);
            final int size = purchaseItem.getSize();
            player.openInventory(Bukkit.createInventory(player, size, purchaseItem.getTitle().replace("{PRICE}", price)));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(purchaseItem.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                final ItemStack item = top.getItem(i);
                if(item != null) {
                    final ItemMeta itemMeta = item.getItemMeta();
                    final List<String> lore = new ArrayList<>();
                    if(itemMeta.hasLore()) {
                        for(String s : itemMeta.getLore()) {
                            lore.add(s.replace("{PRICE}", price).replace("{ITEM}", material));
                        }
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
            }
            top.setItem(top.firstEmpty(), auctionItem);
            player.updateInventory();
            purchasing.put(player, auction);
        }
    }
    public List<AuctionedItem> getCollectionBin(@NotNull Player player) {
        return auctions.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }
    public ItemStack getPlayerCollectionBin(@NotNull Player player) {
        final String size = Integer.toString(getCollectionBin(player).size());
        final ItemStack item = collectionBin.clone();
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{ITEMS}", size));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    public AuctionedItem valueOf(Player player, int slot, String type) {
        final String originalType = type;
        type = type.toUpperCase();
        final int page = this.page.getOrDefault(player, 0), realPage = (page-1) * slots.size();
        switch (type) {
            case "COLLECTION_BIN":
                final List<AuctionedItem> bin = getCollectionBin(player);
                return bin.size() > realPage+slot ? (AuctionedItem) bin.toArray()[realPage+slot] : null;
            case "AUCTION_HOUSE":
                final Collection<AuctionedItem> ah = auctionHouse.values();
                return ah.size() > realPage+slot ? (AuctionedItem) ah.toArray()[realPage+slot] : null;
            default:
                final UMaterial material = viewingCategory.get(player);
                final String categoryPrefix = "CATEGORY_" + material.name() + "_";
                final String[] values = originalType.split(categoryPrefix);
                final List<AuctionedItem> i = category.get(material).get(values.length == 1 ? null : values[1]);
                return slot < i.size() ? i.get(slot) : null;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String title = event.getView().getTitle();
        final boolean isAH = title.equals(ah.getTitle());
        final boolean isCategories = title.equals(categories.getTitle());
        final boolean isCollectionBin = title.equals(collectionbin.getTitle());
        final boolean isConfirmAuction = title.equals(confirmAuction.getTitle());
        final boolean isPurchasing = purchasing.containsKey(player);
        final boolean isCV = title.equals(categoryItems.getTitle());
        if(isAH || isCategories || isCollectionBin || isConfirmAuction || isPurchasing || isCV) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack current = event.getCurrentItem();
            final int rowSlot = event.getRawSlot();
            if(rowSlot < 0 || rowSlot >= top.getSize() || current == null || current.getType().equals(Material.AIR)) {
                return;
            }

            final UUID uuid = player.getUniqueId();
            final String click = event.getClick().name();
            if(isAH || isCategories || isCollectionBin || isCV) {
                final boolean isNextPage = rowSlot == nextPageSlot, isPreviousSlot = rowSlot == previousPageSlot;
                if(isNextPage || isPreviousSlot) {
                    if(isNextPage) {
                        nextPage(player);
                    } else {
                        previousPage(player);
                    }
                    return;
                } else if(current.equals(getPlayerCollectionBin(player))) {
                    player.closeInventory();
                    viewCollectionBin(player);
                    return;
                } else if(current.equals(refresh)) {
                    updatePage(player);
                    return;
                } else if(current.equals(returnToAH)) {
                    player.closeInventory();
                    view(player, 1);
                    return;
                } else if(current.equals(mainCategoryView)) {
                    player.closeInventory();
                    viewCategories(player);
                    return;
                }
            }

            if(isPurchasing) {
                final AuctionedItem auctionedItem = purchasing.get(player);
                final ItemStack item = auctionedItem != null ? auctionedItem.item() : null;
                final UUID auctionerUUID = auctionedItem != null ? auctionedItem.auctioner : null;
                final OfflinePlayer offlinePlayer = auctionedItem != null ? Bukkit.getOfflinePlayer(auctionerUUID) : null;
                final HashMap<String, String> replacements = new HashMap<>();
                final BigDecimal price = auctionedItem != null ? auctionedItem.price : BigDecimal.ZERO;
                final double priceDouble = price.doubleValue();
                final String p = formatBigDecimal(price);
                replacements.put("{PRICE}", p);
                replacements.put("{ITEM}", item != null ? item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : toMaterial(UMaterial.match(item).name(), false) : "");
                replacements.put("{PURCHASER}", player.getName());
                replacements.put("{SELLER}", auctionedItem != null ? offlinePlayer.getName() : "");
                if(confirmPurchaseSlots.contains(rowSlot)) {
                    purchasing.remove(player);
                    if(auctionedItem == null) {
                        sendStringListMessage(player, getStringList(config, "messages.item no longer exists"), replacements);
                    } else if(auctionerUUID.equals(uuid)) {
                        player.closeInventory();
                        sendStringListMessage(player, getStringList(config, "messages.cannot purchase own item"), replacements);
                        view(player, 1);
                        return;
                    } else if(ECONOMY.withdrawPlayer(player, priceDouble).transactionSuccess()) {
                        sendStringListMessage(player, getStringList(config, "messages.purchased auction"), replacements);
                        giveItem(player, item);
                        auctionHouse.remove(auctionedItem.auctionTime);
                        auctions.get(auctionerUUID).remove(auctionedItem);
                        if(offlinePlayer.isOnline()) {
                            sendStringListMessage(offlinePlayer.getPlayer(), getStringList(config, "messages.sold auction"), replacements);
                        }
                        ECONOMY.depositPlayer(offlinePlayer, priceDouble);
                    } else {
                        sendStringListMessage(player, getStringList(config, "messages.cannot afford"), replacements);
                    }
                } else if(cancelPurchaseSlots.contains(rowSlot)) {
                } else {
                    return;
                }
                player.closeInventory();
            } else if(isAH) {
                if(slots.contains(rowSlot)) {
                    final AuctionedItem target = valueOf(player, rowSlot, "AUCTION_HOUSE");
                    if(target != null) {
                        if(target.auctioner.equals(uuid) && click.equals("SHIFT_RIGHT")) {
                            tryCancelling(player, target);
                            updatePage(player);
                        } else {
                            tryPurchasing(player, target);
                        }
                    }
                } else if(current.equals(categoryView)) {
                    player.closeInventory();
                    viewCategories(player);
                }
            } else if(isCategories) {
                player.closeInventory();
                viewCategory(player, UMaterial.match(current), current.getItemMeta().hasDisplayName() ? current.getItemMeta().getDisplayName() : null);
            } else if(isConfirmAuction) {
                final HashMap<ItemStack, BigDecimal> i = auctioning.get(player);
                final ItemStack it = (ItemStack) i.keySet().toArray()[0];
                final BigDecimal price = i.get(it);
                if(confirmAuctionSlots.contains(rowSlot)) {
                    auction(player, it, price);
                    auctioning.remove(player);
                } else if(!cancelAuctionSlots.contains(rowSlot)) {
                    return;
                }
                player.closeInventory();
                player.updateInventory();
            } else if(isCV) {
                if(slots.contains(rowSlot)) {
                    final UMaterial um = UMaterial.match(current);
                    final AuctionedItem a = valueOf(player, slots.indexOf(rowSlot), "CATEGORY_" + um.name() + (current.getItemMeta().hasDisplayName() ? "_" + current.getItemMeta().getDisplayName() : ""));
                    if(a != null) {
                        tryPurchasing(player, a);
                    }
                }
            } else { // Collection Bin
                if(slots.contains(rowSlot)) {
                    tryCancelling(player, valueOf(player, rowSlot, "COLLECTION_BIN"));
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
            sendStringListMessage(player, getStringList(config, "messages.auction sell cancelled"), null);
            player.updateInventory();
        } else if(purchasing != null && purchasing.containsKey(player)) {
            purchasing.remove(player);
            sendStringListMessage(player, getStringList(config, "messages.auction purchase cancelled"), null);
        }
    }
}
