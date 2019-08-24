package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.addons.PlayerRank;
import me.randomhashtags.randompackage.utils.RPFeature;

public class PlayerRanks extends RPFeature {
    private static PlayerRanks instance;
    public static PlayerRanks getPlayerRanks() {
        if(instance == null) instance = new PlayerRanks();
        return instance;
    }

    private ItemStack interactable;

    public String getIdentifier() { return "PLAYER_RANKS"; }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "player ranks.yml");
        if(!otherdata.getBoolean("saved default player ranks")) {
            final String[] a = new String[] {};
            for(String s : a) save("player ranks", s + ".yml");
            otherdata.set("saved default player ranks", true);
            saveOtherData();
        }
        int loaded = 0;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + loaded + " Player Ranks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }

    public void tryRedeeming(Player player, PlayerRank rank) {
    }
}
