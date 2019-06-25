package me.randomhashtags.randompackage.utils.classes;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Title {
    public static HashMap<String, Title> titles;
    public static HashMap<Integer, Title> numbers;
    public static ItemStack i;
    public static String titleChatFormat, titleTabFormat;
    private static int N;

    private String title;
    private ItemStack item;
    public Title(String title) {
        if(titles == null) {
            titles = new HashMap<>();
            numbers = new HashMap<>();
            N = 1;
        }
        this.title = title;
        titles.put(title, this);
        numbers.put(N, this);
        N++;
    }
    public String getTitle() { return title; }
    public String getChatTitle() { return titleChatFormat.replace("{TITLE}", title); }
    public String getTabTitle() { return titleTabFormat.replace("{TITLE}", title); }
    public ItemStack getItem() {
        if(item == null) {
            final ItemStack item = i.clone();
            final ItemMeta itemMeta = item.getItemMeta();
            final List<String> a = new ArrayList<>();
            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("{TITLE}", ChatColor.translateAlternateColorCodes('&', title)));
            for(String l : itemMeta.getLore()) {
                a.add(ChatColor.translateAlternateColorCodes('&', l.replace("{TITLE}", title)));
            }
            itemMeta.setLore(a);
            item.setItemMeta(itemMeta);
            this.item = item;
        }
        return item.clone();
    }

    public static Title valueOf(ItemStack is) {
        if(is != null && titles != null) {
            for(String s : titles.keySet()) {
                final Title T = titles.get(s);
                if(T.getItem().isSimilar(is)) {
                    return T;
                }
            }
        }
        return null;
    }

    public static void deleteAll() {
        titles = null;
        numbers = null;
        i = null;
        titleChatFormat = null;
        titleTabFormat = null;
    }
}
