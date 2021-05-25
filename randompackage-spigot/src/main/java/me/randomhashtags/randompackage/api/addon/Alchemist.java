package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.MagicDust;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.event.AlchemistExchangeEvent;
import me.randomhashtags.randompackage.perms.CustomEnchantPermission;
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

public enum Alchemist implements RPFeature, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory alchemist;
    private String currency;
    private int costSlot;
    private ItemStack accept, exchange, preview, background;
    private List<Player> accepting;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            view((Player) sender);
        }
        return true;
    }

    @Override
    public String getIdentifier() {
        return "ALCHEMIST";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("addons", "alchemist.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "addons", "alchemist.yml"));

        accepting = new ArrayList<>();
        currency = config.getString("settings.currency", "EXP");
        accept = createItemStack(config, "items.accept");
        exchange = createItemStack(config, "items.exchange");
        preview = createItemStack(config, "items.preview");
        background = createItemStack(config, "items.background");
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
            if(s.contains("{COST}")) {
                costSlot = X;
            }
            X++;
        }
        sendConsoleDidLoadFeature("Alchemist", started);
    }
    @Override
    public void unload() {
    }

    public void view(@NotNull Player player) {
        if(hasPermission(player, CustomEnchantPermission.VIEW_ALCHEMIST, true)) {
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
                    ItemStack item = preview.clone();
                    if(!top.getItem(13).equals(item)) {
                        top.setItem(13, item);
                    }
                    item = exchange.clone();
                    if(!top.getItem(22).equals(item)) {
                        top.setItem(22, item);
                    }

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
                            } else if(ECONOMY != null) {
                                if(!ECONOMY.withdrawPlayer(player, cost).transactionSuccess()) {
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
                        final ItemStack item = top.getItem(13).clone();
                        final ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.removeEnchant(Enchantment.ARROW_DAMAGE);
                        itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item.setItemMeta(itemMeta);
                        giveItem(player, item);
                        accepting.add(player);
                        player.closeInventory();
                    }
                }
            } else if(current.hasItemMeta() && current.getItemMeta().hasDisplayName() && current.getItemMeta().hasLore()) {
                final ItemMeta currentMeta = current.getItemMeta();
                final CustomEnchant enchant = valueOfCustomEnchant(currentMeta.getDisplayName());
                final MagicDust dust = enchant == null ? valueOfMagicDust(event.getCurrentItem()) : null;
                final String suCCess = enchant != null ? "enchant" : dust != null ? "dust" : null, currentName = currentMeta.getDisplayName();
                final int firstEmpty = top.firstEmpty();
                if(suCCess != null) {
                    boolean upgrade = false;
                    final BigDecimal zero = BigDecimal.ZERO;
                    BigDecimal cost = zero;
                    ItemStack item = null;
                    ItemMeta itemMeta = null;
                    if(firstEmpty == 5 && !top.getItem(3).getItemMeta().getDisplayName().equals(currentName)
                            || firstEmpty == 3 && top.getItem(5) != null && !top.getItem(5).getItemMeta().getDisplayName().equals(currentName)
                            || firstEmpty < 0
                    ) {
                        return;
                    } else if(firstEmpty == 3 && top.getItem(5) == null || firstEmpty == 5 && top.getItem(3) == null) {
                        // This is meant to be here :)
                        if(dust != null && dust.getUpgradeCost().equals(zero)) {
                            return;
                        }
                    } else {
                        final int slot = firstEmpty == 3 ? 5 : 3;
                        if(suCCess.equals("dust")) {
                            final MagicDust upgradedDust = dust.getUpgradesTo();
                            if(upgradedDust != null) {
                                item = top.getItem(slot).clone();
                                itemMeta = item.getItemMeta();
                                cost = dust.getUpgradeCost();
                                boolean did = false;
                                if(cost.equals(zero)) {
                                    return;
                                }
                                final List<String> targetLore = itemMeta.getLore(), currentLore = currentMeta.getLore();
                                for(int i = 0; i < targetLore.size(); i++) {
                                    final String target = targetLore.get(i);
                                    if(getRemainingInt(target) != -1 && !did) {
                                        did = true;
                                        int percent = ((getRemainingInt(target) + getRemainingInt(currentLore.get(i))) / 2);
                                        item = upgradedDust.getItem();
                                        if(item == null) {
                                            return;
                                        }
                                        item = item.clone();
                                        itemMeta = item.getItemMeta();
                                        final List<String> lore = new ArrayList<>();
                                        for(String s : itemMeta.getLore()) {
                                            if(s.contains("{PERCENT}")) {
                                                s = s.replace("{PERCENT}", "" + percent);
                                            }
                                            lore.add(s);
                                        }
                                        itemMeta.setLore(lore);
                                        item.setItemMeta(itemMeta);
                                    }
                                }
                            }
                        } else {
                            final CustomEnchants enchants = CustomEnchants.getCustomEnchants();
                            final EnchantRarity rarity = valueOfCustomEnchantRarity(enchant);
                            final String successString = rarity.getSuccess(), destroyString = rarity.getDestroy();
                            final int level = enchants.getEnchantmentLevel(currentMeta.getDisplayName());
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
                            final List<String> l = is.getItemMeta().getLore(), currentLore = currentMeta.getLore();
                            for(int i = 0; i <= 100; i++) {
                                final String targetSuccess = successString.replace("{PERCENT}", "" + i);
                                final String targetDestroy = destroyString.replace("{PERCENT}", "" + i);
                                if(l.contains(targetSuccess) || currentLore.contains(targetSuccess)) {
                                    success = success + (i/4);
                                }
                                if(l.contains(targetDestroy) || currentLore.contains(targetDestroy)) {
                                    if(i > higherDestroy) {
                                        higherDestroy = i;
                                    }
                                    destroy = destroy + i;
                                }
                            }
                            destroy = higherDestroy + (destroy/4);
                            if(destroy > 100) {
                                destroy = 100;
                            }
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
                        final List<String> lore = new ArrayList<>();
                        for(String string : itemMeta.getLore()) {
                            if(string.contains("{COST}")) string = string.replace("{COST}", formatBigDecimal(cost));
                            lore.add(string);
                        }
                        itemMeta.setLore(lore);
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
            } else {
                return;
            }
            if(player.isOnline()) {
                player.updateInventory();
            }
        }
    }
}
