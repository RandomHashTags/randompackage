package me.randomhashtags.randompackage.utils;

import org.bukkit.plugin.Plugin;

public class NamespacedKey {
    public final Plugin plugin;
    public final String key;
    public NamespacedKey(Plugin plugin, String key) {
        this.plugin = plugin;
        this.key = key;
    }
}
