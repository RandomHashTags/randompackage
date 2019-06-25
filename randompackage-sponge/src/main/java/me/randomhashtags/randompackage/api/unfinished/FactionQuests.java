package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.EventAttributes;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.File;

public class FactionQuests extends EventAttributes implements Listener, CommandExecutor {

    private static FactionQuests instance;
    public static final FactionQuests getFactionQuests() {
        if(instance == null) instance = new FactionQuests();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;

    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        isEnabled = true;
        pluginmanager.registerEvents(this, randompackage);
        save(null, "faction quests.yml");

        config = YamlConfiguration.loadConfiguration(new File(rpd, "faction quests.yml"));

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
                    "KILL_BOSS_UNDEAD_ASSASSIN",
                    "KILL_CONQUEST_BOSSES_I",
                    "KOTH_CAPTURER_I",
                    "LMS_DEFENDER_I",
            };
            for(String s : q) save("faction quests", s + ".yml");
            otherdata.set("saved default faction quests", true);
            saveOtherData();
        }

        for(File f : new File(rpd + separator + "faction quests").listFiles()) {
        }

        sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Quests &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        config = null;
        HandlerList.unregisterAll(this);
    }
}
