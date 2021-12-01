package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.addon.util.PlayerRank;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public enum PlayerRanks implements RPFeatureSpigot {
    INSTANCE;

    private ItemStack interactable;

    @Override
    public String getIdentifier() {
        return "PLAYER_RANKS";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "player ranks.yml");
        if(!OTHER_YML.getBoolean("saved default player ranks")) {
            final String[] a = new String[] {};
            for(String s : a) save("player ranks", s + ".yml");
            OTHER_YML.set("saved default player ranks", true);
            saveOtherData();
        }
        int loaded = 0;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + loaded + " Player Ranks &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }

    public void tryRedeeming(Player player, PlayerRank rank) {
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }
}
