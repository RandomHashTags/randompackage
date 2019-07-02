package me.randomhashtags.randompackage.utils.classes.customenchants;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomEnchant;
import me.randomhashtags.randompackage.utils.abstraction.AbstractEnchantRarity;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class EnchantRarity extends AbstractEnchantRarity {
    private YamlConfiguration settingsYaml;
    private List<String> revealedEnchantMsg, loreFormat;
    private ItemStack revealItem, revealedItem;
    private boolean successDestroy100;
    private String name;
    private Firework firework;
    protected List<AbstractCustomEnchant> enchants;
    public EnchantRarity(File folder, File f) {
        settingsYaml = YamlConfiguration.loadConfiguration(f);
        name = folder.getName().split("\\.yml")[0];
        successDestroy100 = settingsYaml.getBoolean("settings.success+destroy=100");
        enchants = new ArrayList<>();
        created(new NamespacedKey(getPlugin, getName()));
    }
    public YamlConfiguration getSettingsYaml() { return settingsYaml; }
    public String getName() { return name; }
    public String[] getRevealedEnchantRarities() { return settingsYaml.getString("reveals enchant rarities").split(";"); }
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
    public String getNameColors() { return ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("revealed item.name colors")); }
    public String getApplyColors() { return ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("revealed item.apply colors")); }
    public boolean percentsAddUpto100() { return successDestroy100; }
    public String getSuccess() { return ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("settings.success")); }
    public String getDestroy() { return ChatColor.translateAlternateColorCodes('&', settingsYaml.getString("settings.destroy")); }
    public List<String> getLoreFormat() {
        if(loreFormat == null) loreFormat = api.colorizeListString(settingsYaml.getStringList("settings.lore format"));
        return loreFormat;
    }
    public int getSuccessSlot() { return getLoreFormat().indexOf("{SUCCESS}"); }
    public int getDestroySlot() { return getLoreFormat().indexOf("{DESTROY}"); }
    public Firework getFirework() {
        if(firework == null) {
            final String[] a = settingsYaml.getString("revealed item.firework").split(":");
            firework = api.createFirework(FireworkEffect.Type.valueOf(a[0].toUpperCase()), api.getColor(a[1]), api.getColor(a[2]), Integer.parseInt(a[3]));
        }
        return firework;
    }
    public List<AbstractCustomEnchant> getEnchants() { return enchants; }

}
