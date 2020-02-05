package me.randomhashtags.randompackage.dev;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.randomhashtags.randompackage.addon.stats.DisguiseStats;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Disguises extends RPFeature {
    private static Disguises instance;
    public static Disguises getDisguises() {
        if(instance == null) instance = new Disguises();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory inventory, subDisguises;
    private ItemStack disguiseItem;
    private HashMap<Integer, Disguise> slots;
    private HashMap<Player, Disguise> subDisguise;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }

    public String getIdentifier() {
        return "DISGUISES";
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "disguises.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "disguises.yml"));

        disguiseItem = d(config, "item");
        inventory = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        subDisguises = new UInventory(null, config.getInt("sub disguises.size"), colorize(config.getString("sub disguises.title")));
        final Inventory inv = inventory.getInventory();
        slots = new HashMap<>();

        for(String s : getConfigurationSectionKeys(config, "entity types", false)) {
            final String path = "entity types." + s + ".";
            final int slot = config.getInt(path + "slot");
            new PathDisguise(s, slot, colorize(config.getString(path + "name")));
            slots.put(slot, getDisguise(s));
            inv.setItem(slot, item);
        }

        subDisguise = new HashMap<>();
        sendConsoleDidLoadFeature(getAll(Feature.DISGUISE).size() + " Disguises", started);
    }
    public void unload() {
        for(Player player : new ArrayList<>(subDisguise.keySet())) {
            player.closeInventory();
        }
        unregister(Feature.DISGUISE);
    }

    public ItemStack getDisguise(@NotNull Disguise disguise) {
        final String entitytype = disguise.getEntityType();
        if(entitytype != null) {
            final ItemStack is = disguiseItem.clone();
            final String name = disguise.getName();
            itemMeta = is.getItemMeta(); lore.clear();
            if(itemMeta.hasDisplayName()) {
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TYPE}", name));
            }
            if(itemMeta.hasLore()) {
                for(String s : itemMeta.getLore()) {
                    lore.add(s.replace("{TYPE}", name));
                }
                itemMeta.setLore(lore); lore.clear();
            }
            is.setItemMeta(itemMeta);
            return is;
        }
        return null;
    }
    public void viewMenu(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.disguise", true)) {
            viewInventory(player, null);
            final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
            final DisguiseStats stats = pdata.getDisguiseStats();
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
        setDisguise(player, EntityType.valueOf(disguise.getEntityType()));
    }
    public void undisguise(@NotNull Player player) {
        setDisguise(player, null);
    }
    private void setDisguise(@NotNull Player player, EntityType disguise) {
        final RPPlayer pdata = RPPlayer.get(player.getUniqueId());
        final DisguiseStats stats = pdata.getDisguiseStats();
        stats.setDisguise(disguise);
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
        final RPPlayer pdata = RPPlayer.get(event.getPlayer().getUniqueId());
        final DisguiseStats stats = pdata.getDisguiseStats();
        final EntityType disguise = stats.getDisguise();
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
