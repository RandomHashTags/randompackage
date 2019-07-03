package me.randomhashtags.randompackage.recode.api.addons.usingFile;

import me.randomhashtags.randompackage.api.events.FactionUpgradeLevelupEvent;
import me.randomhashtags.randompackage.recode.api.addons.FactionUpgrade;
import me.randomhashtags.randompackage.utils.classes.FactionUpgradeType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFactionUpgrade extends FactionUpgrade {
    private ItemStack item;

    public FileFactionUpgrade(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addFactionUpgrade(getYamlName(), this); }

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
    public FactionUpgradeType getType() { return FactionUpgradeType.types.getOrDefault(yml.getString("settings.type"), null); }
    public int getSlot() { return yml.getInt("settings.slot"); }
    public int getMaxTier() { return yml.getInt("settings.max tier"); }
    public boolean itemAmountEqualsTier() { return yml.getBoolean("settings.item amount=tier"); }
    public List<String> getPerks() { return yml.getStringList("perks"); }
    public List<String> getRequirements() { return yml.getStringList("requirements"); }

    public void didLevelup(FactionUpgradeLevelupEvent event) {
        if(event.isCancelled()) return;
    }
}
