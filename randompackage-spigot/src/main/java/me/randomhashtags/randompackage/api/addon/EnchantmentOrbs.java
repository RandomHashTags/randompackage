package me.randomhashtags.randompackage.api.addon;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.EnchantmentOrb;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.addon.file.PathEnchantmentOrb;
import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentOrbs extends RPFeature {
    private static EnchantmentOrbs instance;
    public static EnchantmentOrbs getEnchantmentOrbs() {
        if(instance == null) instance = new EnchantmentOrbs();
        return instance;
    }

    public String getIdentifier() { return "ENCHANTMENT_ORBS"; }
    public void load() {
        final long started = System.currentTimeMillis();
        final List<ItemStack > orbs = new ArrayList<>();
        save("addons", "enchantment orbs.yml");
        final YamlConfiguration config = getAddonConfig("enchantment orbs.yml");
        for(String key : config.getConfigurationSection("enchantment orbs").getKeys(false)) {
            final String path = "enchantment orbs." + key;
            item = d(config, path);
            final List<String> appliesto = new ArrayList<>();
            for(String s : config.getString(path + ".applies to").split(";")) {
                appliesto.add(s.toUpperCase());
            }
            final int starting = config.getInt(path + ".starting max slots"), increment = config.getInt(path + ".upgrade increment");
            int increm = increment;
            for(int k = starting; k <= config.getInt(path + ".final max slots"); k += increment) {
                if(k != starting) {
                    increm += increment;
                }
                final String slots = Integer.toString(k), increments = Integer.toString(increm), appliedlore = colorize(config.getString(path + ".apply").replace("{SLOTS}", slots).replace("{ADD_SLOTS}", increments));
                final ItemStack i = item.clone(); itemMeta = i.getItemMeta(); lore.clear();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOTS}", slots));
                if(itemMeta.hasLore()) {
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{SLOTS}", slots).replace("{INCREMENT}", increments));
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                i.setItemMeta(itemMeta);
                new PathEnchantmentOrb(key, i, appliedlore, appliesto, k, increm);
                orbs.add(i);
            }
        }
        addGivedpCategory(orbs, UMaterial.ENDER_EYE, "Enchantment Orbs", "Givedp: Enchantment Orbs");
        sendConsoleDidLoadFeature(getAll(Feature.ENCHANTMENT_ORB).size() + " Enchantment Orbs", started);
    }
    public void unload() {
        unregister(Feature.ENCHANTMENT_ORB);
    }

    public void applyEnchantmentOrb(@NotNull Player player, @NotNull ItemStack is, @NotNull ItemStack enchantmentorb, @NotNull EnchantmentOrb orb) {
        EnchantmentOrb appliedOrb = null;
        final int percent = getRemainingInt(enchantmentorb.getItemMeta().getLore().get(orb.getPercentLoreSlot()));
        itemMeta = is.getItemMeta();
        lore.clear();
        if(itemMeta.hasLore()) {
            lore.addAll(itemMeta.getLore());
        }
        for(String s : lore) {
            EnchantmentOrb eo = valueOfEnchantmentOrb(s);
            if(eo != null) {
                appliedOrb = eo;
            }
        }
        if(RANDOM.nextInt(100) < percent) {
            final String applied = orb.getApplied();
            if(appliedOrb == null) {
                lore.add(applied);
            } else {
                final String prev = appliedOrb.getApplied();
                boolean did = false;
                for(int i = 0; i < lore.size(); i++) {
                    if(lore.get(i).equals(prev)) {
                        did = true;
                        lore.set(i, applied);
                        break;
                    }
                }
                if(!did) {
                    return;
                }
            }
            //playSuccess(player);
        } else {
            //playDestroy(player);
            return;
        }
        itemMeta.setLore(lore); lore.clear();
        is.setItemMeta(itemMeta);
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null && !i.getType().equals(Material.AIR)) {
            final EnchantmentOrb eo = valueOfEnchantmentOrb(i);
            if(eo != null) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(current != null && !current.getType().equals(Material.AIR) && cursor != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            final EnchantmentOrb orb = valueOfEnchantmentOrb(cursor);
            if(orb != null && orb.canBeApplied(current)) {
                applyEnchantmentOrb(player, current, cursor, orb);
                //playSuccess((Player) event.getWhoClicked());
                event.setCancelled(true);
                event.setCurrentItem(current);
                final int amount = cursor.getAmount();
                if(amount == 1) {
                    event.setCursor(new ItemStack(Material.AIR));
                } else {
                    cursor.setAmount(amount-1);
                }
                player.updateInventory();
            }
        }
    }
}
