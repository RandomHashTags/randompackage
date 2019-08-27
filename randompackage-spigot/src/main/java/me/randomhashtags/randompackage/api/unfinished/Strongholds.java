package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Strongholds extends RPFeature implements CommandExecutor {
    private static Strongholds instance;
    public static Strongholds getStrongholds() {
        if(instance == null) instance = new Strongholds();
        return instance;
    }
    public YamlConfiguration config;

    public String getIdentifier() { return "STRONGHOLDS"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "strongholds.yml");
        if(!otherdata.getBoolean("saved default strongholds")) {
            final String[] a = new String[] {};
            for(String s : a) save("strongholds", s + ".yml");
            otherdata.set("saved default strongholds", true);
            saveOtherData();
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (strongholds != null ? strongholds.size() : 0) + " Strongholds &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        strongholds = null;
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }
    @EventHandler
    private void blockPlaceEvent(BlockPlaceEvent event) {
    }
    @EventHandler
    private void blockBreakEvent(BlockBreakEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void projectileLaunchEvent(ProjectileLaunchEvent event) {
        if(event.getEntity() instanceof EnderPearl) {
        }
    }
}
