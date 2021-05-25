package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;

public enum Battlefield implements RPFeature {
    INSTANCE;

    @Override
    public String getIdentifier() {
        return "BATTLEFIELD";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Battlefield &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerTeleportEvent(PlayerTeleportEvent event) {
    }
}
