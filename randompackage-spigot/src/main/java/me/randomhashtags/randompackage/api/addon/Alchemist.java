package me.randomhashtags.randompackage.api.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.MagicDust;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.event.AlchemistExchangeEvent;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Alchemist extends RPFeature implements CommandExecutor {
    private static Alchemist instance;
    public static Alchemist getAlchemist() {
        if(instance == null) instance = new Alchemist();
        return instance;
    }

    public String getIdentifier() { return "ALCHEMIST"; }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            view((Player) sender);
        }
        return true;
    }

    public YamlConfiguration config;
    private UInventory alchemist;
    private String currency;
    private int costSlot;
    private ItemStack accept, exchange, preview, background;
    private List<Player> accepting;

    public void load() {
        final long started = System.currentTimeMillis();
        save("addons", "alchemist.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "addons", "alchemist.yml"));

        accepting = new ArrayList<>();
        currency = config.getString("settings.currency", "EXP");
        accept = d(config, "items.accept");
        exchange = d(config, "items.exchange");
        preview = d(config, "items.preview");
        background = d(config, "items.background");
        alchemist = new UInventory(null, 27, colorize(config.getString("gui.title")));
        final Inventory inv = alchemist.getInventory();
        for(int i = 0; i < inv.getSize(); i++) {
            switch (i) {
                case 3:
                case 5: break;
                case 13:
                    inv.setItem(i, preview);
                    break;
                case 22:
                    inv.setItem(i, exchange);
                    break;
                default:
                    inv.setItem(i, background);
                    break;
            }
        }
        int X = 0;
        for(String s : accept.getItemMeta().getLore()) {
            if(s.contains("{COST}")) costSlot = X;
            X++;
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded Alchemist &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void view(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.alchemist", true)) {
            player.openInventory(Bukkit.createInventory(player, alchemist.getSize(), alchemist.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(alchemist.getInventory().getContents());
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final String title = event.getView().getTitle();
        final ItemStack current = event.getCurrentItem();
        if(title.equals(alchemist.getTitle())) {
            final Inventory top = player.getOpenInventory().getTopInventory();
            final int r = event.getRawSlot(), size = top.getSize();
            event.setCancelled(true);
            player.updateInventory();
            if(r < size) {
                if(r == 3 || r == 5) {
                    giveItem(player, current);
                    top.setItem(r, new ItemStack(Material.AIR));

                    item = preview.clone();
                    if(!top.getItem(13).equals(item)) top.setItem(13, item);
                    item = exchange.clone();
                    if(!top.getItem(22).equals(item)) top.setItem(22, item);

                } else if(r == 22 && top.getItem(3) != null && top.getItem(5) != null && !top.getItem(13).equals(preview)) {
                    final int cost = getRemainingInt(top.getItem(22).getItemMeta().getLore().get(costSlot));
                    final AlchemistExchangeEvent e = new AlchemistExchangeEvent(player, top.getItem(3), top.getItem(5), currency, cost,top.getItem(13));
                    PLUGIN_MANAGER.callEvent(e);
                    if(!e.isCancelled()) {
                        final Location l = player.getLocation();
                        if(!player.getGameMode().equals(GameMode.CREATIVE)) {
                            boolean notenough = false;
                            if(currency.equals("EXP")) {
                                final int totalxp = getTotalExperience(player);
                                if(totalxp < cost) {
                                    notenough = true;
                                    sendStringListMessage(player, getStringList(config, "messages.not enough xp"), null);
                                } else {
                                    setTotalExperience(player, totalxp-cost);
                                }
                                playSound(config, "sounds." + (notenough ? "need more xp" : "upgrade via xp"), player, l, false);
                            } else if(eco != null) {
                                if(!eco.withdrawPlayer(player, cost).transactionSuccess()) {
                                    notenough = true;
                                    sendStringListMessage(player, getStringList(config, "messages.not enough cash"), null);
                                }
                                playSound(config, "sounds." + (notenough ? "need more cash" : "upgrade via cash"), player, l, false);
                            } else {
                                return;
                            }
                            if(notenough) {
                                player.closeInventory();
                                player.updateInventory();
                                return;
                            }
                        } else {
                            playSound(config, "sounds.upgrade creative", player, l, false);
                        }
                        item = top.getItem(13).clone(); itemMeta = item.getItemMeta();
                        itemMeta.removeEnchant(Enchantment.ARROW_DAMAGE);
                        itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item.setItemMeta(itemMeta);
                        giveItem(player, item);
                        accepting.add(player);
                        player.closeInventory();
                    }
                }
            } else if(current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().hasLore()) {
                final ItemMeta cm = current.getItemMeta();
                final CustomEnchant enchant = valueOfCustomEnchant(cm.getDisplayName());
                final MagicDust dust = enchant == null ? valueOfMagicDust(event.getCurrentItem()) : null;
                final String suCCess = enchant != null ? "enchant" : dust != null ? "dust" : null, d = cm.getDisplayName();
                final int F = top.firstEmpty();
                if(suCCess != null) {
                    boolean upgrade = false;
                    final BigDecimal zero = BigDecimal.ZERO;
                    BigDecimal cost = zero;
                    if(F == 5 && !top.getItem(3).getItemMeta().getDisplayName().equals(d)
                            || F == 3 && top.getItem(5) != null && !top.getItem(5).getItemMeta().getDisplayName().equals(d)
                            || F < 0
                    ) {
                        return;
                    } else if(F == 3 && top.getItem(5) == null
                            || F == 5 && top.getItem(3) == null) {
                        // This is meant to be here :)
                        if(dust != null && dust.getUpgradeCost().equals(zero)) return;
                    } else {
                        final int slot = F == 3 ? 5 : 3;
                        if(suCCess.equals("dust")) {
                            final MagicDust u = dust.getUpgradesTo();
                            if(u != null) {
                                item = top.getItem(slot).clone(); itemMeta = item.getItemMeta(); lore.clear();
                                cost = dust.getUpgradeCost();
                                boolean did = false;
                                if(cost.equals(zero)) return;
                                for(int i = 0; i < itemMeta.getLore().size(); i++) {
                                    if(getRemainingInt(itemMeta.getLore().get(i)) != -1 && !did) {
                                        did = true;
                                        int percent = ((getRemainingInt(itemMeta.getLore().get(i)) + getRemainingInt(cm.getLore().get(i))) / 2);
                                        item = u.getItem();
                                        if(item == null) {
                                            return;
                                        }
                                        item = item.clone(); itemMeta = item.getItemMeta();
                                        for(String s : itemMeta.getLore()) {
                                            if(s.contains("{PERCENT}")) s = s.replace("{PERCENT}", "" + percent);
                                            lore.add(s);
                                        }
                                        itemMeta.setLore(lore); lore.clear();
                                        item.setItemMeta(itemMeta);
                                    }
                                }
                            }
                        } else {
                            final CustomEnchants enchants = CustomEnchants.getCustomEnchants();
                            final EnchantRarity rar = valueOfCustomEnchantRarity(enchant);
                            final String SUCCESS = rar.getSuccess(), DESTROY = rar.getDestroy();
                            final int level = enchants.getEnchantmentLevel(cm.getDisplayName());
                            if(level >= enchant.getMaxLevel()) {
                                return;
                            } else {
                                cost = enchant.getAlchemistUpgradeCost(level);
                            }
                            final ItemStack is = top.getItem(slot);
                            item = UMaterial.match(is).getItemStack();
                            itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName("randomhashtags was here");
                            int success = 0, destroy = 0, higherDestroy = -1;
                            final List<String> l = is.getItemMeta().getLore(), cml = cm.getLore();
                            for(int i = 0; i <= 100; i++) {
                                if(l.contains(SUCCESS.replace("{PERCENT}", "" + i)) || cml.contains(SUCCESS.replace("{PERCENT}", "" + i))) {
                                    success = success + (i/4);
                                }
                                if(l.contains(DESTROY.replace("{PERCENT}", "" + i)) || cml.contains(DESTROY.replace("{PERCENT}", "" + i))) {
                                    if(i > higherDestroy) higherDestroy = i;
                                    destroy = destroy + i;
                                }
                            }
                            destroy = higherDestroy + (destroy/4);
                            if(destroy > 100) destroy = 100;
                            item = enchants.getRevealedItem(enchant, level+1, success, destroy, true, true).clone();
                            itemMeta = item.getItemMeta();
                        }
                        upgrade = true;
                    }
                    if(upgrade) {
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item.setItemMeta(itemMeta);
                        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                        top.setItem(13, item);
                        item = accept.clone();
                        itemMeta = item.getItemMeta();
                        lore.clear();
                        for(String string : itemMeta.getLore()) {
                            if(string.contains("{COST}")) string = string.replace("{COST}", formatBigDecimal(cost));
                            lore.add(string);
                        }
                        itemMeta.setLore(lore); lore.clear();
                        item.setItemMeta(itemMeta);
                        top.setItem(22, item);
                    }
                    top.setItem(top.firstEmpty(), event.getCurrentItem());
                    event.setCurrentItem(new ItemStack(Material.AIR));
                }
            }
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final Inventory inv = event.getInventory();
        if(inv.getHolder() == player) {
            final String title = event.getView().getTitle();
            final boolean contains = accepting.contains(player);
            accepting.remove(player);
            if(title.equals(alchemist.getTitle())) {
                if(contains) {
                    sendStringListMessage(player, getStringList(config, "messages.exchange"), null);
                } else {
                    giveItem(player, inv.getItem(3));
                    giveItem(player, inv.getItem(5));
                }
            } else { return; }
            if(player.isOnline()) {
                player.updateInventory();
            }
        }
    }
}