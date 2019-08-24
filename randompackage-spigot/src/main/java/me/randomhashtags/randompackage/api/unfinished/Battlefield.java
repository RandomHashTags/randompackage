package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;

public class Battlefield extends RPFeature {
    private static Battlefield instance;
    public static Battlefield getBattlefield() {
        if(instance == null) instance = new Battlefield();
        return instance;
    }

    public String getIdentifier() { return "BATTLEFIELD"; }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Battlefield &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    @EventHandler
    private void playerTeleportEvent(PlayerTeleportEvent event) {
    }
}
