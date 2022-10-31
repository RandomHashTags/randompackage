package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.attributesys.EventAttributes;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class FactionQuests extends EventAttributes implements CommandExecutor {
    private static FactionQuests instance;
    public static FactionQuests getFactionQuests() {
        if(instance == null) instance = new FactionQuests();
        return instance;
    }

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save("faction quests", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "faction quests", "_settings.yml"));

        if(!OTHER_YML.getBoolean("saved default faction quests")) {
            generateDefaultFactionQuests();
            OTHER_YML.set("saved default faction quests", true);
            saveOtherData();
        }
        for(File f : new File(DATA_FOLDER + SEPARATOR + "faction quests").listFiles()) {
            if(!f.getName().equals("_settings.yml")) {
                new FileFactionQuest(f);
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.FACTION_QUEST).size() + " Faction Quests &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unregister(Feature.FACTION_QUEST);
    }

    public void view(Player player) {
        if(hasPermission(player, "RandomPackage.factionquests", true)) {
        }
    }

    public void tryClaiming(Player player/*, ActiveFactionQuest quest*/) {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
