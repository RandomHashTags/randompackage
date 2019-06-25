package me.randomhashtags.randompackage.utils.classes.customenchants;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnchantRarity {
    public static HashMap<String, EnchantRarity> rarities;
    private static RandomPackageAPI api;

    private YamlConfiguration settingsYaml;
    private String[] revealedEnchantRarities;
    private List<String> revealedEnchantMsg, loreFormat;
    private ItemStack revealItem, revealedItem;
    private boolean successDestroy100;
    private String name, nameColors, applyColors, success, destroy;
    private Firework firework;
    private int successSlot, destroySlot;
    protected List<CustomEnchant> enchants;
    public EnchantRarity(File folder, File f) {
        if(rarities == null) {
            rarities = new HashMap<>();
            api = RandomPackageAPI.getAPI();
        }
        settingsYaml = YamlConfiguration.loadConfiguration(f);
        name = folder.getName().split("\\.yml")[0];
        successDestroy100 = settingsYaml.getBoolean("settings.success+destroy=100");
        successSlot = -1;
        destroySlot = -1;
        enchants = new ArrayList<>();
        rarities.put(name, this);
    }

    public YamlConfiguration getSettingsYaml() { return settingsYaml; }
    public String getName() { return name; }
    public String[] getRevealedEnchantRarities() {
        if(revealedEnchantRarities == null) revealedEnchantRarities = settingsYaml.getString("reveals enchant rarities").split(";");
        return revealedEnchantRarities;
    }
    public List<String> getRevealedEnchantMsg() {
        if(revealedEnchantMsg == null) revealedEnchantMsg = api.colorizeListString(settingsYaml.getStringList("reveal enchant msg"));
        return revealedEnchantMsg;
    }
    public ItemStack getRevealItem() {
        if(revealItem == null) revealItem = api.d(settingsYaml, "reveal item");
        return revealItem != null ? revealItem.clone() : null;
    }
    public ItemStack getRevealedItem() {
        if(revealedItem == null) revealedItem = api.d(settingsYaml, "revealed item");
        return revealedItem != null ? revealedItem.clone() : null;
    }
    public String getNameColors() {
        if(nameColors == null) nameColors = ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("revealed item.name colors"));
        return nameColors;
    }
    public String getApplyColors() {
        if(applyColors == null) applyColors = ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("revealed item.apply colors"));
        return applyColors;
    }
    public boolean percentsAddUpto100() { return successDestroy100; }
    public String getSuccess() {
        if(success == null) success = ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("settings.success"));
        return success;
    }
    public String getDestroy() {
        if(destroy == null) destroy = ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("settings.destroy"));
        return destroy;
    }
    public List<String> getLoreFormat() {
        if(loreFormat == null) loreFormat = api.colorizeListString(settingsYaml.getStringList("settings.lore format"));
        return loreFormat;
    }
    public int getSuccessSlot() {
        if(successSlot == -1) successSlot = getLoreFormat().indexOf("{SUCCESS}");
        return successSlot;
    }
    public int getDestroySlot() {
        if(destroySlot == -1) destroySlot = getLoreFormat().indexOf("{DESTROY}");
        return destroySlot;
    }
    public Firework getFirework() {
        if(firework == null) {
            final String[] a = settingsYaml.getString("revealed item.firework").split(":");
            firework = api.createFirework(FireworkEffect.Type.valueOf(a[0].toUpperCase()), api.getColor(a[1]), api.getColor(a[2]), Integer.parseInt(a[3]));
        }
        return firework;
    }
    public List<CustomEnchant> getEnchants() { return enchants; }

    public static EnchantRarity valueOf(ItemStack is) {
        if(is != null && rarities != null) {
            for(EnchantRarity r : rarities.values()) {
                final ItemStack re = r.getRevealItem();
                if(re != null && re.isSimilar(is)) {
                    return r;
                }
            }
        }
        return null;
    }
    public static EnchantRarity valueOf(CustomEnchant enchant) {
        if(rarities != null) {
            for(EnchantRarity e : rarities.values()) {
                if(e.enchants.contains(enchant)) {
                    return e;
                }
            }
        }
        return null;
    }
    public static void deleteAll() {
        rarities = null;
        api = null;
    }
}
