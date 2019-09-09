package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Captcha extends RPFeature {
    private static Captcha instance;
    public static Captcha getCaptcha() {
        if(instance == null) instance = new Captcha();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "CAPTCHA"; }
    protected RPFeature getFeature() { return getCaptcha(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Captcha &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    @EventHandler
    private void entityDeathEvent(EntityDeathEvent event) {
    }
    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
