package me.randomhashtags.randompackage.api.addon;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.addon.MagicDust;
import me.randomhashtags.randompackage.addon.RarityFireball;
import me.randomhashtags.randompackage.addon.file.PathFireball;
import me.randomhashtags.randompackage.addon.file.PathMagicDust;
import me.randomhashtags.randompackage.api.CustomEnchants;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.listener.GivedpItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public enum Fireballs implements RPFeatureSpigot {
    INSTANCE;

    private YamlConfiguration config;
    public ItemStack mysterydust;

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save("addons", "fireballs.yml");
        config = getAddonConfig("fireballs.yml");
        mysterydust = createItemStack(config, "items.mystery dust");
        GivedpItem.INSTANCE.items.put("mysterydust", mysterydust);

        final List<ItemStack> list = new ArrayList<>();
        for(String s : getConfigurationSectionKeys(config, "fireballs", false)) {
            list.add(new PathFireball(s).getItem());
        }
        for(String s : getConfigurationSectionKeys(config, "dusts", false)) {
            new PathMagicDust(s);
        }

        addGivedpCategory(list, UMaterial.FIRE_CHARGE, "Rarity Fireballs", "Givedp: Rarity Fireballs");
        sendConsoleDidLoadFeature(getAll(Feature.RARITY_FIREBALL).size() + " Fireballs & " + getAll(Feature.MAGIC_DUST).size() + " Magic Dust", started);
    }
    @Override
    public void unload() {
        unregister(Feature.RARITY_FIREBALL, Feature.MAGIC_DUST);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        final Player player = event.getPlayer();
        final RarityFireball fireball = valueOfRarityFireball(is);
        if(fireball != null) {
            event.setCancelled(true);
            removeItem(player, is, 1);
            ItemStack reward = fireball.getRevealedItem(true);
            final boolean isMystery = reward == null;
            if(isMystery) {
                reward = mysterydust.clone();
            }
            playParticle(config, "particles.dust.default." + (isMystery ? "mystery" : "regular"), player.getEyeLocation(), 15);
            playSound(config, "sounds.dust.reveal" + (isMystery ? " mystery" : "") + " dust", player, player.getLocation(), false);
            giveItem(player, reward);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack cursor = event.getCursor(), current = event.getCurrentItem();
        if(current != null && !current.getType().equals(Material.AIR) && cursor.hasItemMeta() && cursor.getItemMeta().hasDisplayName() && cursor.getItemMeta().hasLore()) {
            final ItemStack item = current;
            final ItemMeta itemMeta = current.getItemMeta();
            final List<String> lore = new ArrayList<>();
            final CustomEnchantSpigot enchant = valueOfCustomEnchant(current);
            final MagicDust dust = valueOfMagicDust(cursor);
            if(dust != null && enchant != null) {
                final EnchantRarity rarity = valueOfCustomEnchantRarity(enchant);
                if(dust.getAppliesToRarities().contains(rarity)) {
                    final String SUCCESS = rarity.getSuccess();
                    int percent = -1;
                    final List<String> l = dust.getItem().getItemMeta().getLore(), cursorLore = cursor.getItemMeta().getLore();
                    for(int z = 0; z < l.size(); z++) {
                        if(l.get(z).contains("{PERCENT}")) {
                            percent = getRemainingInt(cursorLore.get(z));
                        }
                    }
                    if(percent == -1) {
                        return;
                    }
                    for(String string : itemMeta.getLore()) {
                        int remaining = getRemainingInt(string);
                        if(string.equals(SUCCESS.replace("{PERCENT}", "" + remaining))) {
                            if(remaining >= 100) {
                                return;
                            } else if(remaining+percent > 100) {
                                remaining = 50;
                                percent = 50;
                            }
                            string = SUCCESS.replace("{PERCENT}", "" + (remaining + percent));
                        }
                        lore.add(colorize(string));
                    }
                    itemMeta.setLore(lore);
                    //playSuccess((Player) event.getWhoClicked());
                    item.setItemMeta(itemMeta);
                    event.setCancelled(true);
                    event.setCurrentItem(item);
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
}
