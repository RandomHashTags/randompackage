package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomBoss;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.obj.CustomBossAttack;
import me.randomhashtags.randompackage.addon.obj.CustomMinion;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class FileCustomBoss extends RPSpawnableSpigot implements CustomBoss {
    private final String type;
    private final MultilingualString name;

    private Scoreboard scoreboard;

    private final ItemStack spawn_item;
    private final List<String> attributes, rewards;
    private HashMap<Integer, List<String>> messages;
    private List<CustomBossAttack> attacks;
    private final int message_radius;
    private final int max_minions;
    private CustomMinion minion;
    private List<String> scores;

    public FileCustomBoss(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        type = parse_string_in_json(json, "type").toUpperCase();
        name = parse_multilingual_string_in_json(json, "name");
        spawn_item = create_item_stack(json, "spawn item");
        attributes = parse_list_string_in_json(json, "attributes");
        rewards = parse_list_string_in_json(json, "rewards");

        final JSONObject minion_json = json.optJSONObject("minion");
        if(minion_json != null) {
            message_radius = parse_int_in_json(minion_json, "radius");
            max_minions = parse_int_in_json(minion_json, "max");
        } else {
            message_radius = 100;
            max_minions = 10;
        }
        register(Feature.CUSTOM_BOSS, this);
    }

    public @NotNull String getType() {
        return type;
    }
    public @NotNull MultilingualString getName() {
        return name;
    }
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
    public int getScoreboardRadius() {
        return getMessageRadius();
    }
    public int getScoreboardUpdateInterval() {
        return 20;
    }
    public List<String> getScores() {
        getScoreboard();
        return scores;
    }

    public @NotNull ItemStack getSpawnItem() {
        return spawn_item.clone();
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
    public List<String> getRewards() {
        return rewards;
    }
    public @NotNull List<CustomBossAttack> getAttacks() {
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
    public @NotNull HashMap<Integer, List<String>> getMessages() {
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
    @Override
    public int getMessageRadius() {
        return message_radius;
    }
    @Override
    public int getMaxMinions() {
        return max_minions;
    }
    public CustomMinion getMinion() {
        if(minion == null) minion = new CustomMinion(yml.getString("minion.type").toUpperCase(), colorize(yml.getString("minion.name")), yml.getStringList("minion.attributes"));
        return minion;
    }
}
