package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Title;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class FileTitle extends RPAddonSpigot implements Title {
    public static ItemStack TITLE_ITEMSTACK;
    public static String TITLE_CHAT_FORMAT, TITLE_TAB_FORMAT;

    private final String title;
    public FileTitle(String title) {
        this.title = title;
        register(Feature.TITLE, this);
    }

    @Override
    public String getIdentifier() {
        return title;
    }

    @Override
    public String getChatTitle() {
        return TITLE_CHAT_FORMAT.replace("{TITLE}", getIdentifier());
    }

    @Override
    public String getTabTitle() {
        return TITLE_TAB_FORMAT.replace("{TITLE}", getIdentifier());
    }

    public ItemStack getItem() {
        final String title = getIdentifier();
        final ItemStack item = getClone(TITLE_ITEMSTACK);
        final ItemMeta itemMeta = item.getItemMeta();
        final List<String> lore = new ArrayList<>();
        itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TITLE}", colorize(title)));
        for(String string : itemMeta.getLore()) {
            lore.add(colorize(string.replace("{TITLE}", title)));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
