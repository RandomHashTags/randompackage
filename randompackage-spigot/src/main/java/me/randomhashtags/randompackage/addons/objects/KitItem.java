package me.randomhashtags.randompackage.addons.objects;

import me.randomhashtags.randompackage.addons.CustomKit;
import me.randomhashtags.randompackage.addons.CustomKitItem;
import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class KitItem extends RPFeature implements CustomKitItem {
    private CustomKit kit;
    private String path, item, amount, name;
    private int chance, requiredLevel;
    private List<String> lore;

    public String getIdentifier() { return path; }
    protected RPFeature getFeature() { return null; }
    public void load() {}
    public void unload() {}

    public KitItem(CustomKit kit, String path, String item, String amount, String name, List<String> lore) {
        this(kit, path, item, amount, name, lore, 100, 0);
    }
    public KitItem(CustomKit kit, String path, String item, String amount, String name, List<String> lore, int chance) {
        this(kit, path, item, amount, name, lore, chance, 0);
    }
    public KitItem(CustomKit kit, String path, String item, String amount, String name, List<String> lore, int chance, int requiredLevel) {
        this.kit = kit;
        this.path = path;
        this.item = item;
        this.amount = amount == null || amount.isEmpty() ? "1" : amount;
        this.name = name;
        this.lore = lore;
        this.chance = chance;
        this.requiredLevel = requiredLevel;
    }

    public CustomKit getKit() { return kit; }
    public String getItem() { return item; }
    public String getAmount() { return amount; }
    public String getName() { return name; }
    public List<String> getLore() { return lore; }
    public int getChance() { return chance; }
    public int getRequiredLevel() { return requiredLevel; }

    public ItemStack getItemStack(String player, int level, float enchantMultiplier) {
        ItemStack i = null;
        if(item != null && level >= getRequiredLevel() && (chance >= 100 || random.nextInt(100) < chance)) {
            i = d(null, item, level, enchantMultiplier);
            if(i != null) {
                final String lvl = Integer.toString(level), max = Integer.toString(kit.getMaxLevel());
                final ItemMeta m = i.getItemMeta();
                if(name != null) {
                    m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name.replace("{PLAYER}", player).replace("{LEVEL}", lvl).replace("{TIER}", lvl).replace("{MAX_TIER}", max)));
                    i.setItemMeta(m);
                }
                if(lore != null) {
                    updateLore(i, lore, level, enchantMultiplier, true, max);
                }
                if(amount != null) {
                    final boolean range = amount.contains("-");
                    final String[] r = range ? amount.split("-") : null;
                    final int min = r != null ? Integer.parseInt(r[0]) : -1;
                    i.setAmount(range ? min+random.nextInt(Integer.parseInt(r[1])-min+1) : Integer.parseInt(amount));
                }
            }
        }
        return i != null ? i.clone() : null;
    }
}
