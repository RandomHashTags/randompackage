package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.addon.obj.FactionTopInfo;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;

public class FactionTop extends RPFeature implements CommandExecutor {
    private static FactionTop instance;
    public static FactionTop getFactionTop() {
        if(instance == null) instance = new FactionTop();
        return instance;
    }
    public static HashMap<String, FactionTopInfo> topFactions;

    public HashMap<Integer, String> topFactionPlacements;

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
    public String getIdentifier() { return "FACTION_TOP"; }
    protected RPFeature getFeature() { return getFactionTop(); }
    public void load() {
        final long started = System.currentTimeMillis();
        save("_Data", "faction top.yml");
        save(null, "faction top.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "faction top.yml"));

        topFactions = new HashMap<>();
        topFactionPlacements = new HashMap<>();

        sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Top &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        topFactions = null;
    }
    public void backup() {

    }
    public void loadBackup() {
    }

    public void viewTop(CommandSender sender) {
    }

    public void calculateTopFactions() {
        topFactionPlacements.clear();
        for(String s : topFactions.keySet()) {
            final FactionTopInfo info = topFactions.get(s);
            final BigInteger points = info.getFactionPoints();
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerJoinEvent(PlayerJoinEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerQuitEvent(PlayerQuitEvent event) {
    }
}
