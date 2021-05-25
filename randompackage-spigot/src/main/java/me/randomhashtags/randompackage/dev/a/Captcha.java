package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public enum Captcha implements RPFeature {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public String getIdentifier() {
        return "CAPTCHA";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Captcha &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDeathEvent(EntityDeathEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
