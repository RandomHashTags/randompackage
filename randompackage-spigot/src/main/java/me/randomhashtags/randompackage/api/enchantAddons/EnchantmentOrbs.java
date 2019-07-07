package me.randomhashtags.randompackage.api.enchantAddons;

import me.randomhashtags.randompackage.addons.objects.EnchantmentOrb;
import me.randomhashtags.randompackage.utils.Feature;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
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

    public YamlConfiguration config;
    public void load() {
        final long started = System.currentTimeMillis();
        final List<ItemStack > orbs = new ArrayList<>();
        for(String A : config.getConfigurationSection("orbs").getKeys(false)) {
            final ItemStack iii = d(config, "orbs." + A);
            final java.util.List<String> appliesto = new ArrayList<>();
            for(String s : config.getString("orbs." + A + ".applies to").split(";")) appliesto.add(s.toUpperCase());
            final int starting = config.getInt("orbs." + A + ".starting max slots");
            final int increment = config.getInt("orbs." + A + ".upgrade increment");
            int increm = increment;
            for(int k = starting; k <= config.getInt("orbs." + A + ".final max slots"); k += increment) {
                if(k != starting) increm += increment;
                final String slots = Integer.toString(k), increments = Integer.toString(increm), appliedlore = ChatColor.translateAlternateColorCodes('&', config.getString("orbs." + A + ".apply").replace("{SLOTS}", slots).replace("{ADD_SLOTS}", increments));
                item = iii.clone(); itemMeta = item.getItemMeta(); lore.clear();
                itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{SLOTS}", slots));
                if(itemMeta.hasLore()) {
                    for(String s : itemMeta.getLore()) {
                        lore.add(s.replace("{SLOTS}", slots).replace("{INCREMENT}", increments));
                    }
                }
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                new EnchantmentOrb(A, item, appliedlore, appliesto, k, increm);
                item = item.clone(); itemMeta = item.getItemMeta(); lore.clear();
                for(String s: itemMeta.getLore()) lore.add(s.replace("{PERCENT}", "100"));
                itemMeta.setLore(lore); lore.clear();
                item.setItemMeta(itemMeta);
                orbs.add(item);
            }
        }
        addGivedpCategory(orbs, UMaterial.ENDER_EYE, "Enchantment Orbs", "Givedp: Enchantment Orbs");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (enchantmentorbs != null ? enchantmentorbs.size() : 0) + " Enchantment Orbs &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        config = null;
        deleteAll(Feature.ENCHANTMENT_ORBS);
    }


    public void applyEnchantmentOrb(Player player, ItemStack is, ItemStack enchantmentorb, EnchantmentOrb orb) {
        EnchantmentOrb prevOrb = null;
        final int percent = getRemainingInt(enchantmentorb.getItemMeta().getLore().get(orb.getPercentLoreSlot()));
        item = is; itemMeta = item.getItemMeta(); lore.clear();
        if(itemMeta.hasLore()) lore.addAll(itemMeta.getLore());
        for(String s : lore) {
            EnchantmentOrb q = EnchantmentOrb.valueOf(s);
            if(q != null) prevOrb = q;
        }
        if(random.nextInt(100) <= percent) {
            final String a = orb.getApplyLore();
            if(prevOrb == null) {
                lore.add(a);
            } else {
                final String prev = prevOrb.getApplyLore();
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
            final EnchantmentOrb eo = EnchantmentOrb.valueOf(i);
            if(eo != null) {
                event.setCancelled(true);
                event.getPlayer().updateInventory();
            }
        }
    }
    @EventHandler
    private void givedpClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(!event.isCancelled() && current != null && !current.getType().equals(Material.AIR) && cursor != null && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            final EnchantmentOrb orb = EnchantmentOrb.valueOf(cursor);
            if(orb != null) {
                final String c = current.getType().name().toLowerCase();
                item = current; itemMeta = current.getItemMeta(); lore.clear();
                boolean did = false;
                final EnchantmentOrb o = EnchantmentOrb.getOrb(current);
                for(String s : orb.getAppliesTo()) {
                    if((o == null || !o.equals(orb) && o.getIncrement() < orb.getIncrement()) && c.endsWith(s.toLowerCase())) {
                        did = true;
                        applyEnchantmentOrb(player, current, cursor, orb);
                    }
                }
                if(!did) return;
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
