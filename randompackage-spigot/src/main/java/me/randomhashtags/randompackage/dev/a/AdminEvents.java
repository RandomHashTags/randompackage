package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class AdminEvents extends RPFeature {
    private static AdminEvents instance;
    public static AdminEvents getAdminEvents() {
        if(instance == null) instance = new AdminEvents();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "ADMIN_EVENTS"; }
    protected RPFeature getFeature() { return getAdminEvents(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Admin Events &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
