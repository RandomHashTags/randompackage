package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.addon.EnchantRarity;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileEnchantRarity extends RPAddon implements EnchantRarity {
    private File folder;
    private ItemStack revealItem, revealedItem;
    private Firework firework;
    protected List<CustomEnchant> enchants;

    public FileEnchantRarity(File folder, File f) {
        this.folder = folder;
        load(f);
        enchants = new ArrayList<>();
        register(Feature.CUSTOM_ENCHANT_RARITY, this);
    }
    public String getIdentifier() { return folder.getName(); }

    public String[] getRevealedEnchantRarities() {
        return yml.getString("reveals enchant rarities").split(";");
    }
    public List<String> getRevealedEnchantMsg() {
        return getStringList(yml, "reveal enchant msg");
    }
    public ItemStack getRevealItem() {
        if(revealItem == null) revealItem = API.createItemStack(yml, "reveal item");
        return getClone(revealItem);
    }
    public ItemStack getRevealedItem() {
        if(revealedItem == null) revealedItem = API.createItemStack(yml, "revealed item");
        return getClone(revealedItem);
    }
    public String getNameColors() {
        return getString(yml, "revealed item.name colors");
    }
    public String getApplyColors() {
        return getString(yml, "revealed item.apply colors");
    }
    public boolean percentsAddUpto100() {
        return yml.getBoolean("settings.success+destroy=100");
    }
    public String getSuccess() {
        return getString(yml, "settings.success");
    }
    public String getDestroy() {
        return getString(yml, "settings.destroy");
    }
    public List<String> getLoreFormat() {
        return getStringList(yml, "settings.lore format");
    }
    public int getSuccessSlot() {
        return getLoreFormat().indexOf("{SUCCESS}");
    }
    public int getDestroySlot() {
        return getLoreFormat().indexOf("{DESTROY}");
    }
    public Firework getFirework() {
        if(firework == null) {
            final String[] values = yml.getString("revealed item.firework").split(":");
            firework = createFirework(FireworkEffect.Type.valueOf(values[0].toUpperCase()), getColor(values[1]), getColor(values[2]), Integer.parseInt(values[3]));
        }
        return firework;
    }
    public List<CustomEnchant> getEnchants() {
        return enchants;
    }
}
