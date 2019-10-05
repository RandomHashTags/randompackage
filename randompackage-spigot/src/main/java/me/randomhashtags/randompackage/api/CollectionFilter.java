package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.obj.CollectionChest;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CollectionFilter extends RPFeature implements CommandExecutor {
    private static CollectionFilter instance;
    public static CollectionFilter getCollectionFilter() {
        if(instance == null) instance = new CollectionFilter();
        return instance;
    }

    public YamlConfiguration config;
    private ItemStack collectionchest;
    public String defaultType, allType, itemType, filtertypeString;
    public int filtertypeSlot = -1;

    private UInventory collectionchestgui;
    private Material allMaterial;
    private HashMap<Integer, UMaterial> picksup;
    private HashMap<UUID, Location> editingfilter;

    public String getIdentifier() { return "COLLECTION_FILTER"; }
    protected RPFeature getFeature() { return getCollectionFilter(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && hasPermission(sender, "RandomPackage.collectionfilter", true)) {
            if(args.length == 0 || args.length == 1 || args.length == 2 && !args[1].equals("all")) {
                final String a = args.length >= 1 ? args.length == 1 ? args[0] : args[0] + "_" + args[1] : "";
                final ItemStack q = getItemInHand(player);
                if(q.getType().equals(collectionchest.getType()) && q.hasItemMeta() && q.getItemMeta().getDisplayName() != null && q.getItemMeta().getDisplayName().equals(collectionchest.getItemMeta().getDisplayName())) {
                    switch(a) {
                        case "default":
                            setFilter(player, q, defaultType);
                            break;
                        case "all":
                            setFilter(player, q, ChatColor.translateAlternateColorCodes('&', config.getString("collection chests.chest.filter types.all")));
                            break;
                        default:
                            if(args.length == 0) {
                                editFilter(player, null);
                            } else {
                                Material f = Material.getMaterial(a.toUpperCase());
                                if(f != null) {
                                    setFilter(player, q, itemType.replace("{ITEM}", toMaterial(a, false)));
                                } else {
                                    sendStringListMessage(player, config.getStringList("messages.invalid filter type"), null);
                                    editFilter(player, null);
                                }
                            }
                            break;
                    }
                } else {
                    if(args.length == 1) sendStringListMessage(player, config.getStringList("messages.invalid filter type"), null);
                    sendStringListMessage(sender, config.getStringList("messages.need to be holding cc"), null);
                }
            } else if(args.length == 2 && args[1].equals("all") || args.length == 3 && args[2].equals("all")) {

            }
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "collection filter.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "collection filter.yml"));

        picksup = new HashMap<>();
        editingfilter = new HashMap<>();

        collectionchest = d(config, "collection chests.chest");
        allType = ChatColor.translateAlternateColorCodes('&', config.getString("collection chests.chest.filter types.all"));
        defaultType = ChatColor.translateAlternateColorCodes('&', config.getString("collection chests.chest.filter types.default"));
        itemType = ChatColor.translateAlternateColorCodes('&', config.getString("collection chests.chest.filter types.item"));

        collectionchestgui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory cci = collectionchestgui.getInventory();
        final ItemStack background = d(config, "gui.background");
        for(int i = 0; i < collectionchestgui.getSize(); i++) {
            if(config.get("gui." + i) != null) {
                final ItemStack itemstack = d(config, "gui." + i);
                cci.setItem(i, itemstack);
                final String[] j = config.getString("gui." + i + ".picks up").toUpperCase().split(":");
                final UMaterial um = UMaterial.match(j[0], j.length > 1 ? Byte.parseByte(j[1]) : 0);
                if(um == null && j[0].equalsIgnoreCase("all")) allMaterial = itemstack.getType();
                picksup.put(i, um);
            } else cci.setItem(i, background.clone());
        }
        for(int i = 0; i < collectionchest.getItemMeta().getLore().size(); i++) if(collectionchest.getItemMeta().getLore().get(i).contains("{FILTER_TYPE}")) {
            filtertypeSlot = i;
            filtertypeString = collectionchest.getItemMeta().getLore().get(i);
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded CollectionFilter &e(took " + (System.currentTimeMillis()-started) + "ms)");
        scheduler.runTaskAsynchronously(randompackage, () -> {
            final ConfigurationSection cf = otherdata.getConfigurationSection("collection chests");
            if(cf != null) {
                for(String s : cf.getKeys(false)) {
                    final String[] info = otherdata.getString("collection chests." + s + ".info").split(":");
                    new CollectionChest(UUID.fromString(s), info[0], toLocation(info[1]), !info[2].equals("null") ? UMaterial.match(info[2]) : null);
                }
            }
            final HashMap<UUID, CollectionChest> c = CollectionChest.chests;
            sendConsoleMessage("&6[RandomPackage] &aLoaded " + (c != null ? c.size() : 0) + " collection chests &e[async]");
        });
    }
    public void unload() {
        for(UUID u : editingfilter.keySet()) {
            final OfflinePlayer o = Bukkit.getOfflinePlayer(u);
            if(o.isOnline()) o.getPlayer().closeInventory();
        }
        final YamlConfiguration a = otherdata;
        a.set("collection chests", null);
        final HashMap<UUID, CollectionChest> chests = CollectionChest.chests;
        if(chests != null) {
            for(CollectionChest c : chests.values()) {
                c.backup();
            }
        }
        CollectionChest.deleteAll();
    }

    private void viewFilter(Player player, CollectionChest cc) {
        final HashMap<String, String> replacements = new HashMap<>();
        final UMaterial f = cc.getFilter();
        replacements.put("{ITEM}", f != null ? f.name() : "All");
        sendStringListMessage(player, config.getStringList("messages.view filter"), replacements);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
        final Entity e = event.getEntity();
        final HashMap<UUID, CollectionChest> a = CollectionChest.chests;
        if(a != null && config.getStringList("enabled worlds").contains(e.getWorld().getName())) {
            final Chunk c = e.getLocation().getChunk();
            final List<ItemStack> drops = event.getDrops();
            for(CollectionChest cc : a.values()) {
                if(cc.getLocation().getChunk().equals(c)) {
                    final UMaterial f = cc.getFilter();
                    final Inventory i = cc.getInventory();
                    final List<ItemStack> added = new ArrayList<>();
                    for(ItemStack is : drops) {
                        if(f == null || f.equals(UMaterial.match(is))) {
                            i.addItem(is);
                            added.add(is);
                        }
                    }
                    drops.removeAll(added);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Block b = event.getClickedBlock();
        if(b != null && !b.getType().equals(Material.AIR) && b.getType().name().contains("CHEST")) {
            final Player player = event.getPlayer();
            final CollectionChest cc = CollectionChest.valueOf(b);
            if(cc != null) {
                final Action a = event.getAction();
                if(a.equals(Action.RIGHT_CLICK_BLOCK)) {
                    if(player.isSneaking()) {
                        event.setCancelled(true);
                        editFilter(player, b);
                    } else {
                        cc.getInventory();
                    }
                } else if(a.equals(Action.LEFT_CLICK_BLOCK)) {
                    viewFilter(player, cc);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final ItemStack is = event.getItemInHand();
        if(UMaterial.match(is).equals(UMaterial.match(collectionchest)) && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equals(collectionchest.getItemMeta().getDisplayName())) {
            final Player player = event.getPlayer();
            item = is.clone(); item.setAmount(1);
            final UMaterial u = getFiltered(item);
            new CollectionChest(player.getUniqueId().toString(), event.getBlockPlaced().getLocation(), u);
            sendStringListMessage(player, config.getStringList("messages.placed"), null);
            final String f = u == null ? defaultType : toMaterial(u.getMaterial().name(), false);
            for(String s : config.getStringList("messages.set")) {
                if(s.contains("{ITEM}")) s = s.replace("{ITEM}", f);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Block b = event.getBlock();
        if(b.getType().equals(collectionchest.getType())) {
            final CollectionChest cc = CollectionChest.valueOf(b);
            if(cc != null) {
                event.setCancelled(true);
                b.setType(Material.AIR);
                if(config.getBoolean("collection chests.chest.keeps meta")) {
                    final UMaterial u = cc.getFilter();
                    final ItemStack is = collectionchest.clone();
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> lore = itemMeta.getLore();
                    lore.set(filtertypeSlot, filtertypeString.replace("{FILTER_TYPE}", u == null ? allType : itemType.replace("{ITEM}", toMaterial(u.name(), false))));
                    itemMeta.setLore(lore);
                    is.setItemMeta(itemMeta);
                    b.getWorld().dropItemNaturally(b.getLocation(), is);
                }
                cc.destroy();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() != null && top.getHolder().equals(player) && event.getView().getTitle().equals(collectionchestgui.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();

            final int r = event.getRawSlot();
            final ItemStack c = event.getCurrentItem();
            if(c == null || c.getType().equals(Material.AIR) || r >= top.getSize()) return;
            final UUID u = player.getUniqueId();
            final CollectionChest cc = editingfilter.containsKey(u) ? CollectionChest.valueOf(player.getWorld().getBlockAt(editingfilter.get(u))) : null;
            final UMaterial filter = cc != null ? cc.getFilter() : null;
            final Material mat = filter != null ? filter.getMaterial() : null;
            final byte data = filter != null ? filter.getData() : -1;
            if(mat != null && mat.equals(c.getType()) && data == c.getData().getData())
                sendStringListMessage(player, config.getStringList("messages.item already being filtered"), null);
            else {
                if(cc != null && editingfilter.containsKey(u)) {
                    cc.setFilter(picksup.get(r));
                    editingfilter.remove(u);
                    viewFilter(player, cc);
                } else
                    setFilter(player, getItemInHand(player), r);
                player.closeInventory();;
            }
        }
    }
    public void editFilter(Player player, Block clickedblock) {
        if(clickedblock != null) editingfilter.put(player.getUniqueId(), clickedblock.getLocation());
        player.openInventory(Bukkit.createInventory(player, collectionchestgui.getSize(), collectionchestgui.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(collectionchestgui.getInventory().getContents());
        player.updateInventory();
        final String selected = ChatColor.translateAlternateColorCodes('&', config.getString("gui.selected.prefix")), notselected = ChatColor.translateAlternateColorCodes('&', config.getString("gui.not selected.prefix"));
        final boolean selectedEnchanted = config.getBoolean("gui.selected.enchanted"), notselectedEnchanted = config.getBoolean("gui.not selected.enchanted");
        final CollectionChest cc = CollectionChest.valueOf(clickedblock);
        final UMaterial filter = cc != null ? cc.getFilter() : getFiltered(player.getItemInHand());
        final Material mat = filter != null ? filter.getMaterial() : null;
        final byte data = filter != null ? filter.getData() : -1;
        for(int i = 0; i < top.getSize(); i++) {
            if(top.getItem(i) != null && !top.getItem(i).getType().equals(Material.AIR)) {
                final UMaterial u = picksup.get(i);
                final String a = toMaterial(u != null ? u.name() : allMaterial.name(), false);
                item = top.getItem(i); itemMeta = item.getItemMeta();
                final String q = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemMeta.hasEnchants() ? ChatColor.AQUA + a : a;
                boolean sel = filter == null && item.getType().equals(allMaterial) || filter != null && mat.equals(item.getType()) && data == item.getData().getData();
                itemMeta.setDisplayName(sel ? selected : notselected);
                itemMeta.setDisplayName(itemMeta.getDisplayName() + q);
                lore.clear();
                if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
                for(String r : config.getStringList("gui." + (sel ? "selected" : "not selected") + ".added lore")) lore.add(ChatColor.translateAlternateColorCodes('&', r));
                if(sel && selectedEnchanted || !sel && notselectedEnchanted) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                if(sel && selectedEnchanted || !sel && notselectedEnchanted) item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
            }
        }
        player.updateInventory();
    }

    public ItemStack getCollectionChest(String filter) {
        filter = filter.toLowerCase();
        filter = filter.equals("all") ? allType : filter.equals("default") ? defaultType : toMaterial(filter, false);
        item = collectionchest.clone(); itemMeta = item.getItemMeta(); lore.clear();
        if(itemMeta.hasLore()) {
            for(String s : itemMeta.getLore()) {
                if(s.contains("{FILTER_TYPE}")) s = s.replace("{FILTER_TYPE}", filter);
                lore.add(s);
            }
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
        return item;
    }
    public void setFilter(Player player, ItemStack is, int rawslot) {
        itemMeta = is.getItemMeta(); lore.clear();
        final List<String> l = collectionchest.getItemMeta().getLore();
        final String m = toMaterial(picksup.get(rawslot).name(), false);
        for(int i = 0; i < l.size(); i++) {
            if(i == filtertypeSlot) {
                lore.add(ChatColor.translateAlternateColorCodes('&', l.get(i).replace("{FILTER_TYPE}", m)));
            } else {
                lore.add(l.get(i));
            }
        }
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        lore.clear();
        for(String string : config.getStringList("messages.updated cc")) {
            if(string.contains("{AMOUNT}")) string = string.replace("{AMOUNT}", Integer.toString(is.getAmount()));
            if(string.contains("{ITEM}")) string = string.replace("{ITEM}", m);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
        }
        if(player != null) player.updateInventory();
    }
    public void setFilter(Player player, ItemStack is, String filter) {
        itemMeta = is.getItemMeta(); lore.clear(); lore.addAll(itemMeta.getLore());
        lore.set(filtertypeSlot, collectionchest.getItemMeta().getLore().get(filtertypeSlot).replace("{FILTER_TYPE}", filter));
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        lore.clear();
        for(String string : config.getStringList("messages.set")) {
            if(string.contains("{ITEM}")) string = string.replace("{ITEM}", filter);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
        }
    }

    public UMaterial getFiltered(ItemStack is) {
        final String u = collectionchest.clone().getItemMeta().getLore().get(filtertypeSlot).replace("{FILTER_TYPE}", itemType), a = ChatColor.stripColor(is.getItemMeta().getLore().get(filtertypeSlot).toUpperCase());
        for(UMaterial s : picksup.values()) {
            if(s != null && a.equals(ChatColor.stripColor(u.replace("{ITEM}", s.name().replace("_", " "))))) {
                return s;
            }
        }
        return null;
    }

}
