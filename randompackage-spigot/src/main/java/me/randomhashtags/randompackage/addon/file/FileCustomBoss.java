package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomBoss;
import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.obj.CustomBossAttack;
import me.randomhashtags.randompackage.addon.obj.CustomMinion;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class FileCustomBoss extends RPSpawnableSpigot implements CustomBoss {
    private final String type;
    private final MultilingualString name;

    private final Scoreboard scoreboard;

    private final ItemStack spawn_item;
    private final List<String> attributes, rewards;
    private final HashMap<Integer, List<String>> messages;
    private final List<CustomBossAttack> attacks;
    private final int message_radius;
    private final int max_minions;
    private final CustomMinion minion;
    private final List<String> scores;

    public FileCustomBoss(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        type = parse_string_in_json(json, "type").toUpperCase();
        name = parse_multilingual_string_in_json(json, "name");
        spawn_item = create_item_stack(json, "spawn item");
        attributes = parse_list_string_in_json(json, "attributes");
        rewards = parse_list_string_in_json(json, "rewards");

        final JSONObject minion_json = parse_json_in_json(json, "minion", null), messages_json = parse_json_in_json(json, "messages");
        if(minion_json != null) {
            final String minion_type = parse_string_in_json(minion_json, "type").toUpperCase(), minion_name = parse_string_in_json(minion_json, "name");
            final List<String> minion_attributes = parse_list_string_in_json(minion_json, "attributes");
            minion = new CustomMinion(minion_type, minion_name, minion_attributes);
            message_radius = parse_int_in_json(minion_json, "radius");
            max_minions = parse_int_in_json(minion_json, "max");
        } else {
            minion = null;
            message_radius = 100;
            max_minions = 10;
        }
        messages = new HashMap<>();
        messages.put(-5, parse_list_string_in_json(messages_json, "summon"));
        messages.put(-4, parse_list_string_in_json(messages_json, "summon broadcast"));
        messages.put(-3, parse_list_string_in_json(messages_json, "defeated"));
        messages.put(-2, parse_list_string_in_json(messages_json, "defeated broadcast"));
        for(int i = 0; i <= 100; i++) {
            final List<String> s = parse_list_string_in_json(messages_json, Integer.toString(i));
            if(!s.isEmpty()) {
                messages.put(i, s);
            }
        }

        final JSONObject scoreboard_json = parse_json_in_json(json, "scoreboard");
        scoreboard = SCOREBOARD_MANAGER.getNewScoreboard();
        scoreboard.registerNewObjective("dummy", "dummy");
        final Objective o = scoreboard.getObjective("dummy");
        o.setDisplayName(parse_string_in_json(scoreboard_json, "title"));
        o.setDisplaySlot(DisplaySlot.valueOf( parse_string_in_json(scoreboard_json, "display slot").toUpperCase()));
        int i = 15;
        final List<String> scores = new ArrayList<>(), scores_list = parse_list_string_in_json(scoreboard_json, "scores");
        for(String score : scores_list) {
            o.getScore(score).setScore(i);
            i--;
            scores.add(score);
        }
        this.scores = scores;

        attacks = new ArrayList<>();
        final JSONObject attacks_json = parse_json_in_json(json, "attacks");
        final Iterator<String> attack_keys = attacks_json.keys();
        for(; attack_keys.hasNext(); ) {
            String s = attack_keys.next();
            final JSONObject attack_json = attacks_json.getJSONObject(s);
            final int chance = parse_int_in_json(attack_json, "chance"), radius = parse_int_in_json(attack_json, "radius");
            final List<String> attack = parse_list_string_in_json(attack_json, "attack");
            attacks.add(new CustomBossAttack(chance, radius, attack));
        }

        register(Feature.CUSTOM_BOSS, this);
    }

    public @NotNull String getType() {
        return type;
    }
    public @NotNull MultilingualString getName() {
        return name;
    }
    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
    @Override
    public int getScoreboardRadius() {
        return getMessageRadius();
    }
    @Override
    public int getScoreboardUpdateInterval() {
        return 20;
    }
    @Override
    public List<String> getScores() {
        return scores;
    }

    public @NotNull ItemStack getSpawnItem() {
        return spawn_item.clone();
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
    public @NotNull List<String> getRewards() {
        return rewards;
    }
    @Override
    public @NotNull List<CustomBossAttack> getAttacks() {
        return attacks;
    }
    @Override
    public @NotNull HashMap<Integer, List<String>> getMessages() {
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
    @Override
    @Nullable
    public CustomMinion getMinion() {
        return minion;
    }
}
