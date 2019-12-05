package me.randomhashtags.randompackage.api;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.FilterCategory;
import me.randomhashtags.randompackage.dev.Feature;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import me.randomhashtags.randompackage.addon.file.FileFilterCategory;
import me.randomhashtags.randompackage.util.universal.UInventory;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ItemFilter extends RPFeature implements CommandExecutor, Listener {
    private static ItemFilter instance;
    public static ItemFilter getItemFilter() {
        if(instance == null) instance = new ItemFilter();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;
    private String enablePrefix, disabledPrefix;
    private List<String> enable, disable, addedLore;
    private HashMap<Integer, String> categorySlots;
    private HashMap<String, FileFilterCategory> categories, categoryTitles;

    public String getIdentifier() { return "ITEM_FILTER"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewHelp(player);
        } else {
            switch (args[0]) {
                case "toggle":
                    toggleFilter(player);
                    break;
                case "edit":
                    viewCategories(player);
                    break;
                default:
                    break;
            }
        }
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save("filter categories", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder + separator + "filter categories", "_settings.yml"));

        categorySlots = new HashMap<>();
        categories = new HashMap<>();
        categoryTitles = new HashMap<>();

        addedLore = colorizeListString(config.getStringList("settings.categories added lore"));
        enablePrefix = colorize(config.getString("settings.enabled prefix"));
        enable = colorizeListString(config.getStringList("settings.enabled lore"));
        disabledPrefix = colorize(config.getString("settings.disabled prefix"));
        disable = colorizeListString(config.getStringList("settings.disabled lore"));

        gui = new UInventory(null, config.getInt("categories.size"), colorize(config.getString("categories.title")));
        final Inventory gi = gui.getInventory();
        for(String s : config.getConfigurationSection("categories").getKeys(false)) {
            if(!s.equals("title") && !s.equals("size")) {
                final String p = "categories." + s + ".", opens = config.getString(p + "opens");
                final int slot = config.getInt(p + "slot");
                item = d(config, "categories." + s); itemMeta = item.getItemMeta();
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
                itemMeta.setLore(addedLore);
                item.setItemMeta(itemMeta);
                gi.setItem(slot, item);
                categorySlots.put(slot, opens);
            }
        }

        if(!otherdata.getBoolean("saved default filter categories")) {
            final String[] f = new String[] {"EQUIPMENT", "FOOD", "ORES", "OTHER", "POTION_SUPPLIES", "RAIDING", "SPECIALTY"};
            for(String s : f) save("filter categories", s + ".yml");
            otherdata.set("saved default filter categories", true);
            saveOtherData();
        }
        for(File f : new File(dataFolder + separator + "filter categories").listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileFilterCategory fc = new FileFilterCategory(f);
                categories.put(f.getName(), fc);
                categoryTitles.put(fc.getTitle(), fc);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.FILTER_CATEGORY).size() + " Item Filter categories &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unregister(Feature.FILTER_CATEGORY);
    }

    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, "RandomPackage.filter", true)) {
            sendStringListMessage(sender, config.getStringList("messages.help"), null);
        }
    }
    public void viewCategories(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.filter.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            player.updateInventory();
        }
    }
    private ItemStack getStatus(List<UMaterial> filtered, ItemStack is) {
        itemMeta = is.getItemMeta(); lore.clear();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        final UMaterial u = UMaterial.match(is);
        final boolean isFiltered = filtered.contains(u);
        itemMeta.setDisplayName((isFiltered ? enablePrefix : disabledPrefix) + ChatColor.stripColor(itemMeta.getDisplayName()));
        itemMeta.setLore(isFiltered ? enable : disable);
        is.setItemMeta(itemMeta);
        if(isFiltered) is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        else is.removeEnchantment(Enchantment.ARROW_DAMAGE);
        return is;
    }
    public void toggleFilter(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.filter.toggle", true)) {
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final boolean status = !pdata.hasActiveFilter();
            pdata.setActiveFilter(status);
            sendStringListMessage(player, config.getStringList("messages." + (status ? "en" : "dis") + "able"), null);
        }
    }
    public void viewCategory(@NotNull Player player, @NotNull FilterCategory category) {
        if(hasPermission(player, "RandomPackage.filter.view." + category.getIdentifier(), true)) {
            player.closeInventory();
            final List<UMaterial> filtered = RPPlayer.get(player.getUniqueId()).getFilteredItems();
            final UInventory target = category.getInventory();
            final int size = target.getSize();
            player.openInventory(Bukkit.createInventory(player, size, target.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(target.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
                if(item != null) {
                    top.setItem(i, getStatus(filtered, item.clone()));
                }
            }
            player.updateInventory();
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String t = event.getView().getTitle();
        final FileFilterCategory category = categoryTitles.getOrDefault(t, null);
        if(t.equals(gui.getTitle()) || category != null) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack c = event.getCurrentItem();
            final int r = event.getRawSlot();
            if(r < 0 || r >= top.getSize() || c == null || c.getType().equals(Material.AIR)) return;

            if(category != null) {
                final List<UMaterial> filtered = RPPlayer.get(player.getUniqueId()).getFilteredItems();
                final UMaterial target = UMaterial.match(c);
                if(filtered.contains(target)) {
                    filtered.remove(target);
                } else {
                    filtered.add(target);
                }
                top.setItem(r, getStatus(filtered, c));
                player.updateInventory();
            } else if(categorySlots.containsKey(r)) {
                final FilterCategory fc = getFilterCategory(categorySlots.get(r));
                if(fc != null) {
                    player.closeInventory();
                    viewCategory(player, fc);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        final RPPlayer pdata = RPPlayer.get(event.getPlayer().getUniqueId());
        if(pdata.hasActiveFilter() && !pdata.getFilteredItems().contains(UMaterial.match(event.getItem().getItemStack()))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final FileFilterCategory c = categoryTitles.getOrDefault(event.getView().getTitle(), null);
        if(c != null) {
            scheduler.scheduleSyncDelayedTask(randompackage, () -> viewCategories(player), 0);
        }
    }
}
