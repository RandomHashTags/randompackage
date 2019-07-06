package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.EnchantRarity;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileEnchantRarity extends EnchantRarity {
    private List<String> revealedEnchantMsg, loreFormat;
    private ItemStack revealItem, revealedItem;
    private Firework firework;
    protected List<CustomEnchant> enchants;

    public FileEnchantRarity(File folder, File f) {
        load(f);
        enchants = new ArrayList<>();
        addEnchantRarity(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String[] getRevealedEnchantRarities() { return yml.getString("reveals enchant rarities").split(";"); }
    public List<String> getRevealedEnchantMsg() {
        if(revealedEnchantMsg == null) revealedEnchantMsg = api.colorizeListString(yml.getStringList("reveal enchant msg"));
        return revealedEnchantMsg;
    }
    public ItemStack getRevealItem() {
        if(revealItem == null) revealItem = api.d(yml, "reveal item");
        return revealItem != null ? revealItem.clone() : null;
    }
    public ItemStack getRevealedItem() {
        if(revealedItem == null) revealedItem = api.d(yml, "revealed item");
        return revealedItem != null ? revealedItem.clone() : null;
    }
    public String getNameColors() { return ChatColor.translateAlternateColorCodes('&', yml.getString("revealed item.name colors")); }
    public String getApplyColors() { return ChatColor.translateAlternateColorCodes('&', yml.getString("revealed item.apply colors")); }
    public boolean percentsAddUpto100() { return yml.getBoolean("settings.success+destroy=100"); }
    public String getSuccess() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.success")); }
    public String getDestroy() { return ChatColor.translateAlternateColorCodes('&', yml.getString("settings.destroy")); }
    public List<String> getLoreFormat() {
        if(loreFormat == null) loreFormat = api.colorizeListString(yml.getStringList("settings.lore format"));
        return loreFormat;
    }
    public int getSuccessSlot() { return getLoreFormat().indexOf("{SUCCESS}"); }
    public int getDestroySlot() { return getLoreFormat().indexOf("{DESTROY}"); }
    public Firework getFirework() {
        if(firework == null) {
            final String[] a = yml.getString("revealed item.firework").split(":");
            firework = api.createFirework(FireworkEffect.Type.valueOf(a[0].toUpperCase()), api.getColor(a[1]), api.getColor(a[2]), Integer.parseInt(a[3]));
        }
        return firework;
    }
    public List<CustomEnchant> getEnchants() { return enchants; }

}
