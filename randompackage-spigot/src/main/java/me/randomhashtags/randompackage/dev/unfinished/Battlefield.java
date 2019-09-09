package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Battlefield extends RPFeature {
    private static Battlefield instance;
    public static Battlefield getBattlefield() {
        if(instance == null) instance = new Battlefield();
        return instance;
    }

    public String getIdentifier() { return "BATTLEFIELD"; }
    protected RPFeature getFeature() { return getBattlefield(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Battlefield &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerTeleportEvent(PlayerTeleportEvent event) {
    }
}
