package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.RarityFireball;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.perms.CustomEnchantPermission;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Tinkerer implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private UInventory tinkerer;
    private ItemStack accept, acceptDupe, divider;
    private List<Player> accepting;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            view((Player) sender);
        }
        return true;
    }

    @Override
    public void load() {
        save("addons", "tinkerer.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "addons", "tinkerer.yml"));

        accepting = new ArrayList<>();
        accept = createItemStack(config, "gui.accept");
        acceptDupe = createItemStack(config, "gui.accept dupe");
        divider = createItemStack(config, "gui.divider");
        tinkerer = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory inv = tinkerer.getInventory();
        for(int i = 0; i < inv.getSize(); i++) {
            switch (i) {
                case 4:
                case 13:
                case 22:
                case 31:
                case 40:
                case 49:
                    inv.setItem(i, divider);
                    break;
                case 0:
                    inv.setItem(i, accept);
                    break;
                case 8:
                    inv.setItem(i, acceptDupe);
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void unload() {
    }

    public void view(@NotNull Player player) {
        if(hasPermission(player, CustomEnchantPermission.VIEW_TINKERER, true)) {
            player.openInventory(Bukkit.createInventory(player, tinkerer.getSize(), tinkerer.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(tinkerer.getInventory().getContents());
            player.updateInventory();
        }
    }
    public boolean isOnTradingSide(int slot) {
        return slot >= 5 && slot <= 7 || slot >= 14 && slot <= 17 || slot >= 23 && slot <= 26 || slot >= 32 && slot <= 35 || slot >= 41 && slot <= 44 || slot >= 50 && slot <= 53;
    }
    public boolean isOnReceivingSide(int slot) {
        return slot >= 1 && slot <= 3 || slot >= 9 && slot <= 12 || slot >= 18 && slot <= 21 || slot >= 27 && slot <= 30 || slot >= 36 && slot <= 39 || slot >= 45 && slot <= 48;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player && event.getView().getTitle().equals(tinkerer.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();
            final int rawslot = event.getRawSlot(), invSize = top.getSize();
            int firstEmpty = top.firstEmpty();
            final ItemStack current = event.getCurrentItem();
            final String click = event.getClick().name(), material = current != null ? current.getType().name() : null;
            if(rawslot < 0 || !click.contains("LEFT") && !click.contains("RIGHT") || material == null || material.equals("AIR")) return;

            final CustomEnchantSpigot customEnchant = current.hasItemMeta() && current.getItemMeta().hasDisplayName() ? valueOfCustomEnchant(current.getItemMeta().getDisplayName()) : null;
            ItemStack item = null;
            if(rawslot >= 4 && rawslot <= 8
                    || rawslot >= 13 && rawslot <= 17
                    || rawslot >= 22 && rawslot <= 26
                    || rawslot >= 31 && rawslot <= 35
                    || rawslot >= 40 && rawslot <= 44
                    || rawslot >= 49 && rawslot <= 53) {
                return;
            } else if(current.equals(accept)) {
                accepting.add(player);
                player.closeInventory();
                return;
            } else if(rawslot < invSize) {
                giveItem(player, current);
                item = new ItemStack(Material.AIR);
                firstEmpty = rawslot;
            } else if(top.firstEmpty() < 0) {
                return;
            } else if(customEnchant != null) {
                final EnchantRarity rarity = valueOfCustomEnchantRarity(customEnchant);
                final RarityFireball fireball = valueOfRarityFireball(List.of(rarity));
                if(fireball != null) {
                    final ItemStack itemstack = fireball.getItem();
                    item = itemstack.clone();
                } else {
                    return;
                }
            } else if(current.getItemMeta().hasEnchants() && (material.endsWith("HELMET") || material.endsWith("CHESTPLATE") || material.endsWith("LEGGINGS") || material.endsWith("BOOTS") || material.endsWith("SWORD") || material.endsWith("AXE") || material.endsWith("SPADE") || material.endsWith("SHOVEL") || material.endsWith("HOE") || material.endsWith("BOW"))) {
                final BigDecimal zero = BigDecimal.ZERO;
                BigDecimal xp = zero;
                for(Enchantment enchant : current.getEnchantments().keySet()) {
                    final String target = getString(config, "enchant values." + enchant.getName().toLowerCase());
                    xp = xp.add(BigDecimal.valueOf(Integer.parseInt(target)));
                }
                if(current.hasItemMeta() && current.getItemMeta().hasLore()) {
                    final HashMap<CustomEnchantSpigot, Integer> enchants = CustomEnchants.getCustomEnchants().getEnchantsOnItem(current);
                    for(CustomEnchantSpigot enchant : enchants.keySet()) {
                        xp = xp.add(enchant.getTinkererValue(enchants.get(enchant)));
                    }
                }
                if(!xp.equals(zero)) {
                    item = GivedpItem.INSTANCE.getXPBottle(xp, "Tinkerer").clone();
                }
            } else {
                sendStringListMessage(player, getStringList(config, "messages.doesnt want item"), null);
                return;
            }
            final int first = top.firstEmpty();
            int slot = first <= 3 || rawslot <= 3 ? 4 : 5;
            top.setItem(firstEmpty+slot, item);
            if(rawslot >= invSize) {
                top.setItem(first, current);
            }
            event.setCurrentItem(new ItemStack(Material.AIR));
            player.updateInventory();
        }
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final Inventory inv = event.getInventory();
        if(inv.getHolder() == player) {
            final String title = event.getView().getTitle();
            final boolean isTinkerering = accepting.contains(player);
            accepting.remove(player);
            if(title.equals(tinkerer.getTitle())) {
                sendStringListMessage(player, getStringList(config, "messages." + (isTinkerering ? "accept" : "cancel") + " trade"), null);
                for(int i = 0; i < inv.getSize(); i++) {
                    final ItemStack item = inv.getItem(i);
                    if(item != null && (isTinkerering && isOnTradingSide(i) || !isTinkerering && isOnReceivingSide(i))) {
                        giveItem(player, item);
                    }
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
