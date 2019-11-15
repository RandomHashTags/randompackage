package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.MagicDust;
import me.randomhashtags.randompackage.addon.RarityFireball;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.addon.PathFireball;
import me.randomhashtags.randompackage.util.addon.PathMagicDust;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class Fireballs extends CustomEnchants {
    private static Fireballs instance;
    public static Fireballs getFireballs() {
        if(instance == null) instance = new Fireballs();
        return instance;
    }

    public ItemStack mysterydust;

    @Override public String getIdentifier() { return "FIREBALLS"; }
    @Override
    public void load() {
        long started = System.currentTimeMillis();
        save("addons", "fireballs.yml");
        config = getAddonConfig("fireballs.yml");
        mysterydust = d(config, "items.mystery dust");
        givedpitem.items.put("mysterydust", mysterydust);

        ConfigurationSection cs = config.getConfigurationSection("fireballs");
        final List<ItemStack> z = new ArrayList<>();
        if(cs != null) {
            for(String s : cs.getKeys(false)) {
                z.add(new PathFireball(s).getItem());
            }
        }
        addGivedpCategory(z, UMaterial.FIRE_CHARGE, "Rarity Fireballs", "Givedp: Rarity Fireballs");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (fireballs != null ? fireballs.size() : 0) + " Fireballs &e(took " + (System.currentTimeMillis()-started) + "ms)");

        started = System.currentTimeMillis();
        cs = config.getConfigurationSection("dusts");
        if(cs != null) {
            for(String s : cs.getKeys(false)) {
                new PathMagicDust(s);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (dusts != null ? dusts.size() : 0) + " Magic Dust &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        fireballs = null;
        dusts = null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack I = event.getItem();
        final Player player = event.getPlayer();
        final RarityFireball fireball = valueOfFireball(I);
        if(fireball != null) {
            event.setCancelled(true);
            removeItem(player, I, 1);
            ItemStack reward = fireball.getRevealedItem(true);
            if(reward == null) {
                reward = mysterydust.clone();
                playParticle(config, "particles.dust.default.mystery", player.getEyeLocation(), 15);
                playSound(config, "sounds.dust.reveal dust", player, player.getLocation(), false);
            }
            giveItem(player, reward);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(current != null && !current.getType().equals(Material.AIR) && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            item = current; itemMeta = current.getItemMeta(); lore.clear();
            final CustomEnchant enchant = valueOfCustomEnchant(current);
            final MagicDust dust = valueOfMagicDust(cursor);
            if(dust != null && enchant != null) {
                final EnchantRarity ra = valueOfEnchantRarity(enchant);
                if(dust.getAppliesToRarities().contains(ra)) {
                    final String SUCCESS = ra.getSuccess();
                    int percent = -1;
                    final List<String> l = dust.getItem().getItemMeta().getLore();
                    for(int z = 0; z < l.size(); z++)
                        if(l.get(z).contains("{PERCENT}"))
                            percent = getRemainingInt(cursor.getItemMeta().getLore().get(z));
                    if(percent == -1) return;
                    for(String string : itemMeta.getLore()) {
                        int r = getRemainingInt(string);
                        if(string.equals(SUCCESS.replace("{PERCENT}", "" + r))) {
                            if(r >= 100) return;
                            else if(r + percent > 100) { r = 50; percent = 50; }
                            string = SUCCESS.replace("{PERCENT}", "" + (r + percent));
                        }
                        lore.add(ChatColor.translateAlternateColorCodes('&', string));
                    }
                    itemMeta.setLore(lore); lore.clear();
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
}
