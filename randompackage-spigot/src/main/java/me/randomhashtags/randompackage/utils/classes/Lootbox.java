package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractLootbox;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class Lootbox extends AbstractLootbox {
    public static HashMap<String, Lootbox> lootboxes;

    public Lootbox(File f) {
        if(lootboxes == null) lootboxes = new HashMap<>();
        load(f);
        lootboxes.put(getYamlName(), this);
    }
    public static Lootbox valueOf(String guiTitle) {
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values())
                if(l.getGuiTitle().equals(guiTitle))
                    return l;
        }
        return null;
    }
    public static Lootbox valueof(String previewTitle) {
        if(lootboxes != null) {
            previewTitle = ChatColor.stripColor(previewTitle);
            for(Lootbox l : lootboxes.values()) {
                if(ChatColor.stripColor(l.getPreviewTitle()).equals(previewTitle)) {
                    return l;
                }
            }
        }
        return null;
    }
    public static Lootbox valueOf(ItemStack is) {
        if(lootboxes != null && is != null && is.hasItemMeta())
            for(Lootbox l : lootboxes.values())
                if(l.getItem().isSimilar(is))
                    return l;
        return null;
    }
    public static Lootbox valueOf(int priority) {
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values())
                if(l.getPriority() == priority)
                    return l;
        }
        return null;
    }
    public static Lootbox latest() {
        int p = 0;
        Lootbox lo = null;
        if(lootboxes != null) {
            for(Lootbox l : lootboxes.values()) {
                final int P = l.getPriority();
                if(lo == null || P > p) {
                    p = P;
                    lo = l;
                }
            }
        }
        return lo;
    }

    public static void deleteAll() {
        lootboxes = null;
    }
}
