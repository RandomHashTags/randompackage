package me.randomhashtags.randompackage.api.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.RarityFireball;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
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

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class Tinkerer extends RPFeature implements CommandExecutor {
    private static Tinkerer instance;
    public static Tinkerer getTinkerer() {
        if(instance == null) instance = new Tinkerer();
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            view((Player) sender);
        }
        return true;
    }

    public YamlConfiguration config;
    private UInventory tinkerer;
    private ItemStack accept, acceptDupe, divider;
    private List<Player> accepting;

    public String getIdentifier() { return "TINKERER"; }
    public void load() {
        final long started = System.currentTimeMillis();
        save("addons", "tinkerer.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "addons", "tinkerer.yml"));

        accepting = new ArrayList<>();
        accept = d(config, "gui.accept");
        acceptDupe = d(config, "gui.accept dupe");
        divider = d(config, "gui.divider");
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
        sendConsoleMessage("&6[RandomPackage] &aLoaded Tinkerer &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void view(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.tinkerer", true)) {
            player.openInventory(Bukkit.createInventory(player, tinkerer.getSize(), tinkerer.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(tinkerer.getInventory().getContents());
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player && event.getView().getTitle().equals(tinkerer.getTitle())) {
            event.setCancelled(true);
            player.updateInventory();
            final int rawslot = event.getRawSlot(), size = top.getSize();
            int SLOT = top.firstEmpty();
            final ItemStack current = event.getCurrentItem();
            final String clickType = event.getClick().name(), material = current != null ? current.getType().name() : null;
            if(rawslot < 0 || !clickType.contains("LEFT") && !clickType.contains("RIGHT") || material == null || material.equals("AIR")) return;

            final CustomEnchant e = current.hasItemMeta() && current.getItemMeta().hasDisplayName() ? valueOfCustomEnchant(current.getItemMeta().getDisplayName()) : null;
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
            } else if(rawslot < size) {
                giveItem(player, current);
                item = new ItemStack(Material.AIR);
                SLOT = rawslot;
            } else if(top.firstEmpty() < 0) {
                return;
            } else if(e != null) {
                final EnchantRarity R = valueOfCustomEnchantRarity(e);
                final RarityFireball f = valueOfRarityFireball(Arrays.asList(R));
                if(f != null) {
                    final ItemStack itemstack = f.getItem();
                    if(itemstack == null) return;
                    item = itemstack.clone();
                } else {
                    return;
                }
            } else if(current.getItemMeta().hasEnchants() && (material.endsWith("HELMET") || material.endsWith("CHESTPLATE") || material.endsWith("LEGGINGS") || material.endsWith("BOOTS") || material.endsWith("SWORD") || material.endsWith("AXE") || material.endsWith("SPADE") || material.endsWith("SHOVEL") || material.endsWith("HOE") || material.endsWith("BOW"))) {
                final BigDecimal zero = BigDecimal.ZERO;
                BigDecimal xp = BigDecimal.ZERO;
                for(Enchantment enchant : current.getEnchantments().keySet()) {
                    xp = xp.add(BigDecimal.valueOf(Integer.parseInt(config.getString("enchant values." + enchant.getName().toLowerCase()))));
                }
                if(current.hasItemMeta() && current.getItemMeta().hasLore()) {
                    final HashMap<CustomEnchant, Integer> enchants = CustomEnchants.getCustomEnchants().getEnchantsOnItem(current);
                    for(CustomEnchant enchant : enchants.keySet()) {
                        xp = xp.add(enchant.getTinkererValue(enchants.get(enchant)));
                    }
                }
                if(!xp.equals(zero)) {
                    item = givedpitem.getXPBottle(xp, "Tinkerer").clone();
                }
            } else {
                sendStringListMessage(player, getStringList(config, "messages.doesnt want item"), null);
                return;
            }
            final int first = top.firstEmpty();
            int slot = first <= 3 || rawslot <= 3 ? 4 : 5;
            top.setItem(SLOT+slot, item);
            if(rawslot >= size) top.setItem(first, current);
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
            final boolean contains = accepting.contains(player);
            accepting.remove(player);
            if(title.equals(tinkerer.getTitle())) {
                sendStringListMessage(player, getStringList(config, "messages." + (contains ? "accept" : "cancel") + " trade"), null);
                for(int i = 0; i < inv.getSize(); i++) {
                    item = inv.getItem(i);
                    if(item != null && (contains && (i >= 5 && i <= 7 || i >= 14 && i <= 17 || i >= 23 && i <= 26 || i >= 32 && i <= 35 || i >= 41 && i <= 44 || i >= 50 && i <= 53) || !contains && (i >= 1 && i <= 3 || i >= 9 && i <= 12 || i >= 18 && i <= 21 || i >= 27 && i <= 30 || i >= 36 && i <= 39 || i >= 45 && i <= 48))) {
                        giveItem(player, item);
                    }
                }
            } else { return; }
            if(player.isOnline()) player.updateInventory();
        }
    }
}
