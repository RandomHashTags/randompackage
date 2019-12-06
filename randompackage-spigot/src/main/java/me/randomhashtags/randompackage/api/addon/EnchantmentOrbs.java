package me.randomhashtags.randompackage.api.addon;

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
        for(String A : config.getConfigurationSection("enchantment orbs").getKeys(false)) {
            item = d(config, "enchantment orbs." + A);
            final List<String> appliesto = new ArrayList<>();
            for(String s : config.getString("enchantment orbs." + A + ".applies to").split(";")) appliesto.add(s.toUpperCase());
            final int starting = config.getInt("enchantment orbs." + A + ".starting max slots"), increment = config.getInt("enchantment orbs." + A + ".upgrade increment");
            int increm = increment;
            for(int k = starting; k <= config.getInt("enchantment orbs." + A + ".final max slots"); k += increment) {
                if(k != starting) increm += increment;
                final String slots = Integer.toString(k), increments = Integer.toString(increm), appliedlore = colorize(config.getString("enchantment orbs." + A + ".apply").replace("{SLOTS}", slots).replace("{ADD_SLOTS}", increments));
                final ItemStack i = item.clone(); itemMeta = i.getItemMeta(); lore.clear();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOTS}", slots));
                if(itemMeta.hasLore()) {
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{SLOTS}", slots).replace("{INCREMENT}", increments));
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                i.setItemMeta(itemMeta);
                new PathEnchantmentOrb(A, i, appliedlore, appliesto, k, increm);
                orbs.add(i);
            }
        }
        addGivedpCategory(orbs, UMaterial.ENDER_EYE, "Enchantment Orbs", "Givedp: Enchantment Orbs");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.ENCHANTMENT_ORB).size()+ " Enchantment Orbs &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unregister(Feature.ENCHANTMENT_ORB);
    }

    public void applyEnchantmentOrb(Player player, ItemStack is, ItemStack enchantmentorb, EnchantmentOrb orb) {
        EnchantmentOrb prevOrb = null;
        final int percent = getRemainingInt(enchantmentorb.getItemMeta().getLore().get(orb.getPercentLoreSlot()));
        item = is; itemMeta = item.getItemMeta(); lore.clear();
        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
        for(String s : lore) {
            EnchantmentOrb q = valueOfEnchantmentOrb(s);
            if(q != null) prevOrb = q;
        }
        if(RANDOM.nextInt(100) < percent) {
            final String a = orb.getApplied();
            if(prevOrb == null) {
                lore.add(a);
            } else {
                final String prev = prevOrb.getApplied();
                boolean did = false;
                for(int i = 0; i < lore.size(); i++) {
                    if(!did && lore.get(i).equals(prev)) {
                        did = true;
                        lore.set(i, a);
                    }
                }
                if(!did) return;
            }
            //playSuccess(player);
        } else {
            //playDestroy(player);
            return;
        }
        itemMeta.setLore(lore); lore.clear();
        item.setItemMeta(itemMeta);
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
                item.setItemMeta(itemMeta);
                event.setCancelled(true);
                event.setCurrentItem(item);
                final int a = cursor.getAmount();
                if(a == 1) event.setCursor(new ItemStack(Material.AIR));
                else       cursor.setAmount(a-1);
                player.updateInventory();
            }
        }
    }
}
