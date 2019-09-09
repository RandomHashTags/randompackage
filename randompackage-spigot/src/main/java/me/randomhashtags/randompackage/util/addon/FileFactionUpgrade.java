package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeType;
import me.randomhashtags.randompackage.event.FactionUpgradeLevelupEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFactionUpgrade extends RPAddon implements FactionUpgrade {
    private ItemStack item;

    public FileFactionUpgrade(File f) {
        load(f);
        addFactionUpgrade(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "item");
            final List<String> lore = item.getItemMeta().getLore(), format = getType().getFormat(), l = new ArrayList<>();
            final ItemMeta m = item.getItemMeta();
            for(String s : format) {
                if(s.equals("{LORE}")) {
                    if(lore != null) l.addAll(lore);
                } else {
                    l.add(s);
                }
            }
            m.setLore(l);
            item.setItemMeta(m);
        }
        return item.clone();
    }
    public FactionUpgradeType getType() { return getFactionUpgradeType(yml.getString("settings.type")); }
    public int getSlot() { return yml.getInt("settings.slot"); }
    public int getMaxTier() { return yml.getInt("settings.max tier"); }
    public boolean itemAmountEqualsTier() { return yml.getBoolean("settings.item amount=tier"); }
    public List<String> getPerks() { return yml.getStringList("perks"); }
    public List<String> getRequirements() { return yml.getStringList("requirements"); }

    public void didLevelup(FactionUpgradeLevelupEvent event) {
        if(event.isCancelled()) return;
    }
}
