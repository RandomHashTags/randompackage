package me.randomhashtags.randompackage.api.addons;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import me.randomhashtags.randompackage.addons.RarityFireball;
import me.randomhashtags.randompackage.addons.MagicDust;
import me.randomhashtags.randompackage.addons.usingpath.PathFireball;
import me.randomhashtags.randompackage.addons.usingpath.PathMagicDust;
import me.randomhashtags.randompackage.utils.CustomEnchantUtils;
import me.randomhashtags.randompackage.utils.objects.Feature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.utils.GivedpItem.givedpitem;

public class Fireballs extends CustomEnchantUtils {
    private static Fireballs instance;
    public static Fireballs getFireballs() {
        if(instance == null) instance = new Fireballs();
        return instance;
    }

    public ItemStack mysterydust;

    public String getIdentifier() { return "FIREBALLS"; }
    public void load() {
        loadUtils();
        long started = System.currentTimeMillis();
        save("addons", "fireballs.yml");
        final YamlConfiguration config = getAddonConfig("fireballs.yml");
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
    public void unload() {
        instance = null;
        mysterydust = null;
        deleteAll(Feature.FIREBALLS_AND_DUST);
        unloadUtils();
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
                playParticle(config, "dust.particles.default.mystery", player.getEyeLocation(), 15);
                playSound(config, "dust.sounds.reveal dust", player, player.getLocation(), false);
            }
            giveItem(player, reward);
        }
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(!event.isCancelled() && current != null && !current.getType().equals(Material.AIR) && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            item = current; itemMeta = current.getItemMeta(); lore.clear();
            final CustomEnchant enchant = CustomEnchant.valueOf(current);
            final MagicDust dust = MagicDust.valueOf(cursor);
            if(dust != null && enchant != null) {
                final EnchantRarity ra = valueOfEnchantRarity(enchant);
                if(dust.getAppliesTo().contains(ra)) {
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
