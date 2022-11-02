package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.FilterCategory;
import me.randomhashtags.randompackage.addon.file.FileFilterCategory;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.ItemFilterData;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.perms.ItemFilterPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public enum ItemFilter implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory gui;
    private String enablePrefix, disabledPrefix;
    private List<String> enable, disable, addedLore;
    private HashMap<Integer, String> categorySlots;
    private HashMap<String, FileFilterCategory> categories, categoryTitles;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            return true;
        }
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

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("filter categories", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "filter categories", "_settings.yml"));

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
        for(String s : getConfigurationSectionKeys(config, "categories", false)) {
            if(!s.equals("title") && !s.equals("size")) {
                final String p = "categories." + s + ".", opens = config.getString(p + "opens");
                final int slot = config.getInt(p + "slot");
                final ItemStack item = createItemStack(config, "categories." + s);
                final ItemMeta itemMeta = item.getItemMeta();
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
                itemMeta.setLore(addedLore);
                item.setItemMeta(itemMeta);
                gi.setItem(slot, item);
                categorySlots.put(slot, opens);
            }
        }

        if(!OTHER_YML.getBoolean("saved default filter categories")) {
            generateDefaultFilterCategories();
            OTHER_YML.set("saved default filter categories", true);
            saveOtherData();
        }
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "filter categories")) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                final FileFilterCategory fc = new FileFilterCategory(f);
                categories.put(f.getName(), fc);
                categoryTitles.put(fc.getTitle(), fc);
            }
        }
        sendConsoleDidLoadFeature(getAll(Feature.FILTER_CATEGORY).size() + " Item Filter categories", started);
    }
    @Override
    public void unload() {
        unregister(Feature.FILTER_CATEGORY);
    }

    public void viewHelp(@NotNull CommandSender sender) {
        if(hasPermission(sender, ItemFilterPermission.VIEW_HELP, true)) {
            sendStringListMessage(sender, getStringList(config, "messages.help"), null);
        }
    }
    public void viewCategories(@NotNull Player player) {
        if(hasPermission(player, ItemFilterPermission.VIEW_CATEGORIES, true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
            player.getOpenInventory().getTopInventory().setContents(gui.getInventory().getContents());
            player.updateInventory();
        }
    }
    private ItemStack getStatus(List<UMaterial> filtered, ItemStack is) {
        final ItemMeta itemMeta = is.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        final UMaterial u = UMaterial.match(is);
        final boolean isFiltered = filtered.contains(u);
        itemMeta.setDisplayName((isFiltered ? enablePrefix : disabledPrefix) + ChatColor.stripColor(itemMeta.getDisplayName()));
        itemMeta.setLore(isFiltered ? enable : disable);
        is.setItemMeta(itemMeta);
        if(isFiltered) {
            is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        } else {
            is.removeEnchantment(Enchantment.ARROW_DAMAGE);
        }
        return is;
    }
    public void toggleFilter(@NotNull Player player) {
        if(hasPermission(player, ItemFilterPermission.TOGGLE, true)) {
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final ItemFilterData data = pdata.getItemFilterData();
            final boolean status = !data.isActive();
            data.setActive(status);
            sendStringListMessage(player, getStringList(config, "messages." + (status ? "en" : "dis") + "able"), null);
        }
    }
    public void viewCategory(@NotNull Player player, @NotNull FilterCategory category) {
        player.closeInventory();
        final List<UMaterial> filtered = FileRPPlayer.get(player.getUniqueId()).getItemFilterData().getFilteredItems();
        final UInventory target = category.getInventory();
        final int size = target.getSize();
        player.openInventory(Bukkit.createInventory(player, size, target.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(target.getInventory().getContents());
        for(int i = 0; i < size; i++) {
            final ItemStack item = top.getItem(i);
            if(item != null) {
                top.setItem(i, getStatus(filtered, item.clone()));
            }
        }
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        final String title = event.getView().getTitle();
        final FileFilterCategory category = categoryTitles.getOrDefault(title, null);
        if(title.equals(gui.getTitle()) || category != null) {
            event.setCancelled(true);
            player.updateInventory();
            final ItemStack current = event.getCurrentItem();
            final int slot = event.getRawSlot();
            if(slot < 0 || slot >= top.getSize() || current == null || current.getType().equals(Material.AIR)) {
                return;
            }

            if(category != null) {
                final List<UMaterial> filtered = FileRPPlayer.get(player.getUniqueId()).getItemFilterData().getFilteredItems();
                final UMaterial target = UMaterial.match(current);
                if(filtered.contains(target)) {
                    filtered.remove(target);
                } else {
                    filtered.add(target);
                }
                top.setItem(slot, getStatus(filtered, current));
                player.updateInventory();
            } else if(categorySlots.containsKey(slot)) {
                final FilterCategory fc = getFilterCategory(categorySlots.get(slot));
                if(fc != null) {
                    player.closeInventory();
                    viewCategory(player, fc);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void playerPickupItemEvent(PlayerPickupItemEvent event) {
        final ItemFilterData data = FileRPPlayer.get(event.getPlayer().getUniqueId()).getItemFilterData();
        if(data != null && data.isActive() && !data.getFilteredItems().contains(UMaterial.match(event.getItem().getItemStack()))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final FileFilterCategory c = categoryTitles.getOrDefault(event.getView().getTitle(), null);
        if(c != null) {
            SCHEDULER.scheduleSyncDelayedTask(RANDOM_PACKAGE, () -> viewCategories(player), 0);
        }
    }
}
