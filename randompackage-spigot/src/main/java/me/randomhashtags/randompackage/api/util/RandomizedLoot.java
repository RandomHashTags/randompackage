package me.randomhashtags.randompackage.api.util;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;

import static me.randomhashtags.randompackage.util.listener.GivedpItem.givedpitem;

public class RandomizedLoot extends RPFeature {
    private static RandomizedLoot instance;
    public static RandomizedLoot getRandomizedLoot() {
        if(instance == null) instance = new RandomizedLoot();
        return instance;
    }

    public YamlConfiguration config;
    public HashMap<String, ItemStack> items;

    public String getIdentifier() { return "RANDOMIZED_LOOT"; }
    protected RPFeature getFeature() { return getRandomizedLoot(); }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "randomized loot.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "randomized loot.yml"));

        items = new HashMap<>();
        for(String key : config.getConfigurationSection("").getKeys(false)) {
            final String rewardSize = config.getString(key + ".reward size");
            item = d(config, key);
            itemMeta = item.getItemMeta(); lore.clear();
            if(itemMeta.hasLore()) {
                for(String s : itemMeta.getLore()) {
                    if(s.contains("{ITEM}")) {
                        for(String reward : config.getStringList(key + ".rewards")) {
                            final ItemStack is = givedpitem.valueOf(reward);
                            final ItemMeta m = is.getItemMeta();
                            lore.add(s.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", m.hasDisplayName() ? m.getDisplayName() : "UNKNOWN"));
                        }
                    } else {
                        lore.add(s.replace("{REWARD_SIZE}", rewardSize));
                    }
                }
                itemMeta.setLore(lore); lore.clear();
            }
            item.setItemMeta(itemMeta);
            items.put(key, item);
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + items.size() + " Randomized Loot &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        if(is != null && items.containsValue(is)) {
            final Player player = event.getPlayer();
            event.setCancelled(true);
            player.updateInventory();
        }
    }
}
