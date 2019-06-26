package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.RandomPackageAPI;

public class Disguises extends RandomPackageAPI {

    private static Disguises instance;
    public static Disguises getDisguises() {
        if(instance == null) instance = new Disguises();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }
    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        isEnabled = true;
        save(null, "disguises.yml");
        eventmanager.registerListeners(randompackage, this);

        sendConsoleMessage("&6[RandomPackage] &aLoaded Disguises &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        eventmanager.unregisterListeners(this);
    }
}
