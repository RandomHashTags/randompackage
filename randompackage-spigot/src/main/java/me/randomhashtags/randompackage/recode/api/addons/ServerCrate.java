package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import me.randomhashtags.randompackage.recode.utils.ServerCrateFlare;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class ServerCrate extends RPAddon {
    public abstract int getRedeemableItems();
    public abstract String getDisplayRarity();
    public abstract List<Integer> getSelectableSlots();
    public abstract UInventory getInventory();
    public abstract List<String> getFormat();
    public abstract LinkedHashMap<String, Integer> getRevealChances();
    public abstract ItemStack getItem();
    public abstract ItemStack getDisplay();
    public abstract ItemStack getOpenGui();
    public abstract ItemStack getSelected();
    public abstract ItemStack getRevealSlotRarity();
    public abstract HashMap<String, List<String>> getRewards();
    public abstract ItemStack getBackground();
    public abstract ItemStack getBackground2();
    public abstract ServerCrateFlare getFlare();

    public ServerCrate getRandomRarity(boolean useChances) {
        String rarity = null;
        final Collection<String> key = getRewards().keySet();
        if(!useChances) {
            rarity = (String) key.toArray()[random.nextInt(key.size())];
        } else {
            final LinkedHashMap<String, Integer> r = getRevealChances();
            for(String s : key) if(random.nextInt(100) <= r.get(s)) rarity = s;
            if(rarity == null) rarity = (String) r.keySet().toArray()[r.keySet().size()-1];
        }
        return servercrates.getOrDefault(rarity, null);
    }
    public ItemStack getRandomReward(String rarity) {
        final List<String> r = getRewards().get(rarity);
        final String reward = r.get(random.nextInt(r.size()));
        return api.d(null, reward);
    }

    public static ServerCrate valueOf(ItemStack item) {
        if(servercrates != null) {
            for(ServerCrate crate : servercrates.values()) {
                if(crate.getItem().isSimilar(item)) {
                    return crate;
                }
            }
        }
        return null;
    }
    public static ServerCrate valueOfFlare(ItemStack flare) {
        if(servercrates != null) {
            for(ServerCrate s : servercrates.values()) {
                final ServerCrateFlare f = s.getFlare();
                if(f != null && f.getItem().isSimilar(flare)) {
                    return s;
                }
            }
        }
        return null;
    }
}
