package me.randomhashtags.randompackage.api.dev;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.Nullable;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum FactionPoints implements RPFeatureSpigot, RPItemStack {
    INSTANCE;

    private File dataF;
    private YamlConfiguration config, data;
    private UInventory gui;
    private BigInteger costPerPoint, dailyLimit;
    private ItemStack interactable, display;
    private HashMap<String, BigInteger> points, dailyBought;
    private HashMap<Integer, BigInteger> purchaseAmounts;

    @Override
    public String getIdentifier() {
        return "FACTION_POINTS";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "faction points.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "faction points.yml"));

        costPerPoint = BigInteger.valueOf(config.getLong("settings.cost per point"));
        dailyLimit = BigInteger.valueOf(config.getLong("settings.daily limit"));
        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        interactable = createItemStack(config, "item");
        display = createItemStack(config, "gui.display");

        points = new HashMap<>();
        purchaseAmounts = new HashMap<>();
        dailyBought = new HashMap<>();

        final Inventory inv = gui.getInventory();
        for(String key : getConfigurationSectionKeys(config, "gui", false, "title", "size", "display")) {
            final String path = "gui." + key;
            final int slot = config.getInt(path + ".slot");
            final long targetPoints = config.getLong(path + ".points");
            if(targetPoints != 0) {
                final BigInteger points = BigInteger.valueOf(targetPoints);
                purchaseAmounts.put(slot, points);
                inv.setItem(slot, getDisplayedFactionPoints(points));
            } else {
                inv.setItem(slot, createItemStack(config, path));
            }
        }

        dataF = new File(DATA_FOLDER + SEPARATOR + "_Data", "faction points.yml");
        data = YamlConfiguration.loadConfiguration(dataF);
        loadBackup();

        sendConsoleDidLoadFeature("Faction Points", started);
    }
    @Override
    public void unload() {
        backup();
    }

    private void save() {
        try {
            data.save(dataF);
            data = YamlConfiguration.loadConfiguration(dataF);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadBackup() {
        points.clear();
        for(String s : getConfigurationSectionKeys(data, "points", false)) {
            final BigInteger points = BigInteger.valueOf(data.getLong("points" + s));
            this.points.put(s, points);
        }
    }
    public void backup() {
        for(String s : points.keySet()) {
            data.set("points." + s, points.get(s));
        }
        save();
    }

    public ItemStack getDisplayedFactionPoints(@NotNull BigInteger points) {
        final ItemStack target = getClone(display);
        final String price = formatInt(costPerPoint.multiply(points).intValue()), pointsString = Integer.toString(points.intValue());
        final ItemMeta itemMeta = target.getItemMeta();
        final List<String> lore = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{PRICE}", price).replace("{POINTS}", pointsString));
        }
        itemMeta.setLore(lore);
        target.setItemMeta(itemMeta);
        return target;
    }
    public ItemStack getItem(@NotNull int points) {
        final ItemStack target = getClone(interactable);
        final String pointsString = formatInt(points);
        final ItemMeta itemMeta = target.getItemMeta();
        final List<String> lore = new ArrayList<>();
        for(String s : itemMeta.getLore()) {
            lore.add(s.replace("{POINTS}", pointsString));
        }
        itemMeta.setLore(lore);
        target.setItemMeta(itemMeta);
        addRPItemStackValue(target, "AddFactionPoints", Integer.toString(points));
        return target;
    }
    public boolean isFactionPointItem(@Nullable ItemStack is) {
        return getRPItemStackValue(is, "AddFactionPoints") != null;
    }
    public BigInteger getPoints(String identifier) {
        return points.getOrDefault(identifier, BigInteger.ZERO);
    }
    public void addPoints(String identifier, BigInteger points) {
        setPoints(identifier, getPoints(identifier).add(points));
    }
    public void setPoints(String identifier, BigInteger points) {
        this.points.put(identifier, points);
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(isFactionPointItem(is)) {
            final Player player = event.getPlayer();
            event.setCancelled(true);
            player.updateInventory();

            final int amount = Integer.parseInt(getRPItemStackValue(is, "AddFactionPoints"));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(player.equals(top.getHolder())) {
            final String title = event.getView().getTitle();
            if(title.equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final int slot = event.getRawSlot();
                final ItemStack current = event.getCurrentItem();
                if(slot >= top.getSize() || current == null || current.getType().equals(Material.AIR)) {
                    return;
                }

                if(purchaseAmounts.containsKey(slot)) {
                    final BigInteger amount = purchaseAmounts.get(slot);
                    final BigInteger price = amount.multiply(costPerPoint);
                    final int intAmount = price.intValue();
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{POINTS}", formatInt(intAmount));
                    replacements.put("{DAILY_LIMIT}", formatInt(dailyLimit.intValue()));
                    if(ECONOMY.withdrawPlayer(player, intAmount).transactionSuccess()) {
                        sendStringListMessage(player, getStringList(config, "messages.purchased faction points"), replacements);
                    } else {
                        sendStringListMessage(player, getStringList(config, "messages.cannot afford faction points"), replacements);
                    }
                }
            }
        }
    }
}
