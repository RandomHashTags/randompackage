package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.dev.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FileTitle extends RPAddon implements Title {
    private String title;
    public FileTitle(String title) {
        this.title = title;
        register(Feature.TITLE, this);
    }
    public String getIdentifier() { return title; }

    public static ItemStack i;
    public static String titleChatFormat, titleTabFormat;

    public String getChatTitle() { return titleChatFormat.replace("{TITLE}", getIdentifier()); }
    public String getTabTitle() { return titleTabFormat.replace("{TITLE}", getIdentifier()); }

    public ItemStack getItem() {
        final String title = getIdentifier();
        final ItemStack item = getClone(i);
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> a = new ArrayList<>();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TITLE}", colorize(title)));
        for(String l : itemMeta.getLore()) {
            a.add(colorize(l.replace("{TITLE}", title)));
        }
        itemMeta.setLore(a);
        item.setItemMeta(itemMeta);
        return item;
    }
}
