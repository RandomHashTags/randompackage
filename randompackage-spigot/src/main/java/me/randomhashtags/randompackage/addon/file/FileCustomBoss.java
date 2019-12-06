package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomBoss;
import me.randomhashtags.randompackage.addon.obj.CustomBossAttack;
import me.randomhashtags.randompackage.addon.obj.CustomMinion;
import me.randomhashtags.randompackage.dev.Feature;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileCustomBoss extends RPSpawnable implements CustomBoss {
    private Scoreboard scoreboard;

    private ItemStack spawnitem;
    private HashMap<Integer, List<String>> messages;
    private List<CustomBossAttack> attacks;
    private CustomMinion minion;
    private List<String> scores;

    public FileCustomBoss(File f) {
        load(f);
        register(Feature.CUSTOM_BOSS, this);
    }
    public String getIdentifier() { return getYamlName();  }

    public String getType() { return yml.getString("type").toUpperCase(); }
    public String getName() { return colorize(yml.getString("name")); }
    public Scoreboard getScoreboard() {
        if(scoreboard == null) {
            scoreboard = SCOREBOARD_MANAGER.getNewScoreboard();
            scoreboard.registerNewObjective("dummy", "dummy");
            final Objective o = scoreboard.getObjective("dummy");
            o.setDisplayName(colorize(yml.getString("scoreboard.title")));
            o.setDisplaySlot(DisplaySlot.valueOf(yml.getString("scoreboard.display slot").toUpperCase()));
            int i = 15;
            final List<String> scores = new ArrayList<>();
            for(String sc : yml.getStringList("scoreboard.scores")) {
                final String score = colorize(sc);
                o.getScore(score).setScore(i);
                i--;
                scores.add(score);
            }
            this.scores = scores;
        }
        return scoreboard;
    }
    public int getScoreboardRadius() { return getMessageRadius(); }
    public int getScoreboardUpdateInterval() { return 20; }
    public List<String> getScores() {
        getScoreboard();
        return scores;
    }

    public ItemStack getSpawnItem() {
        if(spawnitem == null) spawnitem = api.d(yml, "spawn item");
        return spawnitem.clone();
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
    public List<String> getRewards() { return yml.getStringList("rewards"); }
    public List<CustomBossAttack> getAttacks() {
        if(attacks == null) {
            attacks = new ArrayList<>();
            final ConfigurationSection cs = yml.getConfigurationSection("attacks");
            if(cs != null) {
                for(String s : cs.getKeys(false)) {
                    attacks.add(new CustomBossAttack(yml.getInt("attacks." + s + ".chance"), yml.getInt("attacks." + s + ".radius"), yml.getStringList("attacks." + s + ".attack")));
                }
            }
        }
        return attacks;
    }
    public HashMap<Integer, List<String>> getMessages() {
        if(messages == null) {
            messages = new HashMap<>();
            messages.put(-5, yml.getStringList("messages.summon"));
            messages.put(-4, yml.getStringList("messages.summon broadcast"));
            messages.put(-3, yml.getStringList("messages.defeated"));
            messages.put(-2, yml.getStringList("messages.defeated broadcast"));
            for(int i = 0; i <= 100; i++) {
                final List<String> s = yml.getStringList("messages." + i);
                if(!s.isEmpty()) {
                    messages.put(i, s);
                }
            }
        }
        return messages;
    }
    public int getMessageRadius() { return yml.getInt("messages.radius"); }
    public int getMaxMinions() { return yml.getInt("minion.max"); }
    public CustomMinion getMinion() {
        if(minion == null) minion = new CustomMinion(yml.getString("minion.type").toUpperCase(), colorize(yml.getString("minion.name")), yml.getStringList("minion.attributes"));
        return minion;
    }
}
