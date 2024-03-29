package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.addon.obj.FactionTopInfo;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;

public enum FactionTop implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public static HashMap<String, FactionTopInfo> topFactions;
    public HashMap<Integer, String> topFactionPlacements;
    public YamlConfiguration config;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save("_Data", "faction top.yml");
        save(null, "faction top.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER, "faction top.yml"));

        topFactions = new HashMap<>();
        topFactionPlacements = new HashMap<>();

        sendConsoleMessage("&aLoaded Faction Top &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
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
