package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.event.EnchanterPurchaseEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
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

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Enchanter extends RPFeature implements CommandExecutor {
    private static Enchanter instance;
    public static Enchanter getEnchanter() {
        if(instance == null) instance = new Enchanter();
        return instance;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            view((Player) sender);
        }
        return true;
    }
    
    public YamlConfiguration config;
    private UInventory enchanter;
    private String currency;
    private HashMap<Integer, Long> costs;
    private HashMap<Integer, ItemStack> purchased;
    
    public String getIdentifier() {
        return "ENCHANTER";
    }
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
                item = createItemStack(config, "gui." + i);
                itemMeta = item.getItemMeta(); lore.clear();
                if(itemMeta.hasLore()) {
                    for(String string : itemMeta.getLore()) {
                        if(string.contains("{COST}")) string = string.replace("{COST}", formatLong(cost));
                        lore.add(string);
                    }
                    itemMeta.setLore(lore); lore.clear();
                    item.setItemMeta(itemMeta);
                }
                ei.setItem(i, item);
            }
        }
        sendConsoleDidLoadFeature("Enchanter", started);
    }
    public void unload() {
    }
    
    public void view(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.enchanter", true)) {
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
            final int r = event.getRawSlot();
            event.setCancelled(true);
            player.updateInventory();
            if(costs.containsKey(r)) {
                long cost = costs.get(r);
                item = purchased.get(r).clone();
                List<String> message = null;
                final int totalxp = getTotalExperience(player);
                final double bal = eco != null ? eco.getBalance(player) : 0.00;
                final boolean give, isCreative = player.getGameMode().equals(GameMode.CREATIVE), exp = currency.equals("EXP");
                give = isCreative || exp && totalxp >= cost || bal >= cost;

                if(give) {
                    final EnchanterPurchaseEvent e = new EnchanterPurchaseEvent(player, item, currency, cost);
                    PLUGIN_MANAGER.callEvent(e);
                    if(e.isCancelled()) {
                        return;
                    }
                    boolean bought = true;
                    cost = e.cost;
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
                                eco.withdrawPlayer(player, cost);
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
