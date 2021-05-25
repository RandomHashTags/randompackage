package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.data.DisguiseData;
import me.randomhashtags.randompackage.data.FileRPPlayer;
import me.randomhashtags.randompackage.data.RPPlayer;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Disguises implements RPFeature, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory inventory, subDisguises;
    private ItemStack disguiseItem;
    private HashMap<Integer, Disguise> slots;
    private HashMap<Player, Disguise> subDisguise;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "DISGUISES";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "disguises.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "disguises.yml"));

        disguiseItem = createItemStack(config, "item");
        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        subDisguises = new UInventory(null, config.getInt("sub disguises.size"), colorize(config.getString("sub disguises.title")));
        final Inventory inv = inventory.getInventory();
        slots = new HashMap<>();

        for(String s : getConfigurationSectionKeys(config, "entity types", false)) {
            final String path = "entity types." + s + ".";
            final int slot = config.getInt(path + "slot");
            final Disguise disguise = new PathDisguise(s, slot, colorize(config.getString(path + "name")));
            slots.put(slot, getDisguise(s));
            inv.setItem(slot, getDisguiseItem(disguise));
        }

        subDisguise = new HashMap<>();
        sendConsoleDidLoadFeature(getAll(Feature.DISGUISE).size() + " Disguises", started);
    }
    @Override
    public void unload() {
        for(Player player : new ArrayList<>(subDisguise.keySet())) {
            player.closeInventory();
        }
        unregister(Feature.DISGUISE);
    }

    public ItemStack getDisguiseItem(@NotNull Disguise disguise) {
        final String entityType = disguise.getEntityType();
        if(entityType != null) {
            final ItemStack is = disguiseItem.clone();
            final String name = disguise.getName();
            final ItemMeta itemMeta = is.getItemMeta();
            if(itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TYPE}", name));
            }
            if(itemMeta.hasLore()) {
                final List<String> lore = new ArrayList<>();
                for(String s : itemMeta.getLore()) {
                    lore.add(s.replace("{TYPE}", name));
                }
                itemMeta.setLore(lore);
            }
            is.setItemMeta(itemMeta);
            return is;
        }
        return null;
    }
    public void viewMenu(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.disguise", true)) {
            viewInventory(player, null);
            final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
            final DisguiseData stats = pdata.getDisguiseData();
        }
    }
    public void viewSubDisguises(@NotNull Player player, @NotNull Disguise disguise) {
        if(hasPermission(player, "RandomPackage.disguise.subdisguise", true)) {
            viewInventory(player, disguise);
            subDisguise.put(player, disguise);
        }
    }
    private void viewInventory(Player player, Disguise sub) {
        player.closeInventory();
        final UInventory u = sub != null ? subDisguises : inventory;
        final Inventory type = u.getInventory();
        player.openInventory(Bukkit.createInventory(player, type.getSize(), u.getTitle()));
        final Inventory top = player.getOpenInventory().getTopInventory();
        top.setContents(type.getContents());
        player.updateInventory();
    }
    public void disguise(@NotNull Player player, @Nullable Disguise disguise) {
        setDisguise(player, disguise);
    }
    public void undisguise(@NotNull Player player) {
        setDisguise(player, null);
    }
    private void setDisguise(@NotNull Player player, Disguise disguise) {
        final FileRPPlayer pdata = FileRPPlayer.get(player.getUniqueId());
        final DisguiseData stats = pdata.getDisguiseData();
        stats.setActive(disguise);
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(player.equals(top.getHolder())) {
            final String title = event.getView().getTitle();
            final boolean menu = title.equals(inventory.getTitle()), sub = subDisguise.containsKey(player);
            if(!menu && !sub) {
                return;
            }
            final int slot = event.getRawSlot();
            event.setCancelled(true);
            player.updateInventory();
            if(menu && slots.containsKey(slot)) {
                viewSubDisguises(player, slots.get(slot));
            } else if(sub) {
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        subDisguise.remove(event.getPlayer());
    }
    @EventHandler
    private void playerJoinEvent(PlayerJoinEvent event) {
        final FileRPPlayer pdata = FileRPPlayer.get(event.getPlayer().getUniqueId());
        final DisguiseData stats = pdata.getDisguiseData();
        final EntityType disguise = EntityType.valueOf(stats.getActive());
        if(disguise != null) {
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityDamageEvent(EntityDamageEvent event) {
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerMoveEvent(PlayerMoveEvent event) {
    }
}
