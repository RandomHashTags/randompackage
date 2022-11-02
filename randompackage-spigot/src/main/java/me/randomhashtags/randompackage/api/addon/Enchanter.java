package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.event.EnchanterPurchaseEvent;
import me.randomhashtags.randompackage.perms.CustomEnchantPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Enchanter implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory enchanter;
    private String currency;
    private HashMap<Integer, Long> costs;
    private HashMap<Integer, ItemStack> purchased;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if(sender instanceof Player) {
            view((Player) sender);
        }
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("addons", "enchanter.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "addons", "enchanter.yml"));

        enchanter = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        currency = config.getString("enchanter.currency", "EXP");
        costs = new HashMap<>();
        purchased = new HashMap<>();

        final Inventory ei = enchanter.getInventory();
        for(int i = 0; i < enchanter.getSize(); i++) {
            if(config.get("gui." + i) != null) {
                final long cost = config.getLong("gui." + i + ".cost");
                costs.put(i, cost);
                purchased.put(i, createItemStack(null, config.getString("gui." + i + ".purchase")));
                final ItemStack item = createItemStack(config, "gui." + i);
                final ItemMeta itemMeta = item.getItemMeta();
                if(itemMeta.hasLore()) {
                    final List<String> lore = new ArrayList<>();
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{COST}")) string = string.replace("{COST}", formatLong(cost));
                        lore.add(string);
                    }
                    itemMeta.setLore(lore);
                    item.setItemMeta(itemMeta);
                }
                ei.setItem(i, item);
            }
        }
        sendConsoleDidLoadFeature("Enchanter", started);
    }
    @Override
    public void unload() {
    }
    
    public void view(@NotNull Player player) {
        if(hasPermission(player, CustomEnchantPermission.VIEW_ENCHANTER, true)) {
            player.openInventory(Bukkit.createInventory(player, enchanter.getSize(), enchanter.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(enchanter.getInventory().getContents());
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final String title = event.getView().getTitle();
        if(title.equals(enchanter.getTitle())) {
            final int rawSlot = event.getRawSlot();
            event.setCancelled(true);
            player.updateInventory();
            if(costs.containsKey(rawSlot)) {
                long cost = costs.get(rawSlot);
                final ItemStack item = purchased.get(rawSlot).clone();
                List<String> message = null;
                final int totalxp = getTotalExperience(player);
                final double bal = ECONOMY.getBalance(player);
                final boolean give, isCreative = player.getGameMode().equals(GameMode.CREATIVE), exp = currency.equals("EXP");
                give = isCreative || exp && totalxp >= cost || bal >= cost;

                if(give) {
                    final EnchanterPurchaseEvent purchaseEvent = new EnchanterPurchaseEvent(player, item, currency, cost);
                    PLUGIN_MANAGER.callEvent(purchaseEvent);
                    if(purchaseEvent.isCancelled()) {
                        return;
                    }
                    boolean bought = true;
                    cost = purchaseEvent.cost;
                    if(!isCreative) {
                        if(exp) {
                            if(totalxp >= cost) {
                                setTotalExperience(player, (int) (totalxp-cost));
                            } else {
                                bought = false;
                            }
                            message = getStringList(config, "messages." + (bought ? "xp purchase" : "need more xp"));
                        } else {
                            if(bal >= cost) {
                                ECONOMY.withdrawPlayer(player, cost);
                            } else {
                                bought = false;
                            }
                            message = getStringList(config, "messages." + (bought ? "cash purchase" : "need more cash"));
                        }
                    }
                    if(bought) {
                        giveItem(player, item);
                    }
                } else {
                    message = getStringList(config, "messages.need more " + (exp ? "xp" : "cash"));
                }
                final HashMap<String, String> replacements = new HashMap<>();
                replacements.put("{AMOUNT}", formatLong(cost));
                sendStringListMessage(player, message, replacements);
                player.updateInventory();
            }
        }
    }
}
