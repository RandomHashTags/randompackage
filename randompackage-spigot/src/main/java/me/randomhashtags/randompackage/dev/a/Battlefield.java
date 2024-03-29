package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;

public enum Battlefield implements RPFeatureSpigot {
    INSTANCE;

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&aLoaded Battlefield &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerTeleportEvent(PlayerTeleportEvent event) {
    }
}
