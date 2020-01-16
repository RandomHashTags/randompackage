package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.obj.RandomizedLootItem;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RandomizedLoot extends RPFeature {
    private static RandomizedLoot instance;
    public static RandomizedLoot getRandomizedLoot() {
        if(instance == null) instance = new RandomizedLoot();
        return instance;
    }

    public YamlConfiguration config;
    public HashMap<String, RandomizedLootItem> items;

    public String getIdentifier() { return "RANDOMIZED_LOOT"; }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "randomized loot.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "randomized loot.yml"));

        final List<ItemStack> values = new ArrayList<>();
        items = new HashMap<>();
        for(String key : config.getConfigurationSection("").getKeys(false)) {
            final String rewardSize = config.getString(key + ".reward size", "1");
            final ItemStack item = d(config, key);
            if(item != null) {
                final ItemMeta itemMeta = item.getItemMeta();
                lore.clear();
                final List<String> rewards = new ArrayList<>();
                if(itemMeta.hasLore()) {
                    final List<String> rewardList = config.getStringList(key + ".rewards");
                    for(String s : itemMeta.getLore()) {
                        if(s.contains("{ITEM}")) {
                            for(String reward : rewardList) {
                                final ItemStack is = d(null, reward);
                                if(is != null) {
                                    rewards.add(reward);
                                    final ItemMeta m = is.getItemMeta();
                                    lore.add(s.replace("{AMOUNT}", Integer.toString(is.getAmount())).replace("{ITEM}", m != null && m.hasDisplayName() ? m.getDisplayName() : "UNKNOWN"));
                                }
                            }
                        } else {
                            lore.add(s.replace("{REWARD_SIZE}", rewardSize));
                        }
                    }
                    itemMeta.setLore(lore); lore.clear();
                }
                item.setItemMeta(itemMeta);
                final RandomizedLootItem i = new RandomizedLootItem(key, item, rewardSize, rewards);
                values.add(item);
                items.put(key, i);
            }
        }
        addGivedpCategory(values, UMaterial.GLISTERING_MELON_SLICE, "Randomized Loot", "Givedp Item: Randomized Loot");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + items.size() + " Randomized Loot &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public RandomizedLootItem valueOfRandomizedLootItem(ItemStack is) {
        if(!items.isEmpty() && is != null) {
            for(RandomizedLootItem i : items.values()) {
                if(i.getItem().isSimilar(is)) {
                    return i;
                }
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack is = event.getItem();
        final RandomizedLootItem i = valueOfRandomizedLootItem(is);
        if(i != null) {
            final Player player = event.getPlayer();
            event.setCancelled(true);
            removeItem(player, is, 1);
            for(String s : i.getRandomRewards(false)) {
                giveItem(player, d(null, s));
            }
            player.updateInventory();
        }
    }
}
