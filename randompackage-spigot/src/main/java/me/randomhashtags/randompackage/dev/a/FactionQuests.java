package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.attributesys.EventAttributes;
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

    public String getIdentifier() { return "FACTION_QUESTS"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save("faction quests", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd + separator + "faction quests", "_settings.yml"));

        if(!otherdata.getBoolean("saved default faction quests")) {
            final String[] q = new String[]{
                    "CONQUEST_BREAKER_I",
                    "DAILY_CHALLENGE_MASTER_I",
                    "DUNGEON_MASTER_I",
                    "DUNGEON_PORTALS_I",
                    "DUNGEON_RUNNER_I", "DUNGEON_RUNNER_II",
                    "HOLD_COSMONAUT_OUTPOST_I",
                    "HOLD_HERO_OUTPOST_I",
                    "HOLD_TRAINEE_OUTPOST_I",
                    "IRON_KOTH_MERCHANT_I",
                    "KILL_BLAZE_I",
                    "KILL_BOSS_BROOD_MOTHER",
                    "KILL_BOSS_KING_SLIME",
                    "KILL_BOSS_PLAGUE_BLOATER",
                    "KILL_BOSS_UNDEAD_ASSASSIN",
                    "KILL_CONQUEST_BOSSES_I",
                    "KOTH_CAPTURER_I",
                    "LEGENDARY_ENCHANTER_I",
                    "LMS_DEFENDER_I",
                    "TOP_DOG",
                    "ULTIMATE_ENCHANTER_I",
            };
            for(String s : q) save("faction quests", s + ".yml");
            otherdata.set("saved default faction quests", true);
            saveOtherData();
        }
        for(File f : new File(rpd + separator + "faction quests").listFiles()) {
            if(!f.getName().equals("_settings.yml")) {
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Quests &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void view(Player player) {
        if(hasPermission(player, "RandomPackage.factionquests.view", true)) {
        }
    }

    public void tryClaiming(Player player/*, ActiveFactionQuest quest*/) {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
