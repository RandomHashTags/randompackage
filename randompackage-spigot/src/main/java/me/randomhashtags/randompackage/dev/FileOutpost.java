package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.MultilingualString;
import me.randomhashtags.randompackage.addon.Outpost;
import me.randomhashtags.randompackage.addon.enums.OutpostStatus;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

// TODO: fix class
public abstract class FileOutpost extends RPAddonSpigot implements Outpost {
    private final MultilingualString name;
    private final int slot;
    private final ItemStack item;
    private final List<String> lostControl, claimed;
    private final List<String> limits, unallowed_items, rewards;
    private Scoreboard scoreboard;

    private String attacking, controlling, status;
    private OutpostStatus statusType;
    private final Location warp_location;
    private long controlledStarting;
    private double controlPercent;
    public FileOutpost(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);

        final JSONObject settings_json = json.getJSONObject("settings");
        name = parse_multilingual_string_in_json(settings_json, "name");
        warp_location = string_to_location(parse_string_in_json(settings_json, "warp location"));

        final JSONObject gui_json = json.getJSONObject("gui");
        slot = parse_int_in_json(gui_json, "slot");

        item = create_item_stack(json, "gui");

        final JSONObject messages_json = json.getJSONObject("messages");
        lostControl = parse_list_string_in_json(messages_json, "lost control");
        claimed = parse_list_string_in_json(messages_json, "claimed");

        limits = parse_list_string_in_json(json, "limits");
        unallowed_items = parse_list_string_in_json(json, "unallowed items");
        rewards = parse_list_string_in_json(json, "rewards");

        final JSONObject scoreboard_json = json.getJSONObject("scoreboard");
        scoreboard = SCOREBOARD_MANAGER.getNewScoreboard();
        final Objective o = scoreboard.registerNewObjective("dummy", "dummy");
        o.setDisplayName(parse_string_in_json(scoreboard_json, "title"));
        final int s = parse_int_in_json(scoreboard_json, "score start");
        int i = 0;
        for(String a : parse_list_string_in_json(scoreboard_json, "scores")) {
            o.getScore(a).setScore(s-i);
            i++;
        }

        register(Feature.OUTPOST, this);
    }

    public @NotNull MultilingualString getName() {
        return name;
    }
    public int getSlot() {
        return slot;
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(item);
    }
    public List<String> getLostControlMsg() {
        return lostControl;
    }
    public List<String> getClaimedMsg() {
        return claimed;
    }
    public List<String> getLimits() {
        return limits;
    }
    public List<String> getUnallowedItems() {
        return unallowed_items;
    }
    public @NotNull List<String> getRewards() {
        return rewards;
    }
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
    public Location getWarpLocation() {
        return warp_location;
    }

    public String getAttackingFaction() {
        return attacking;
    }
    public void setAttackingFaction(String faction) {
        attacking = faction;
    }
    public String getControllingFaction() {
        return controlling;
    }
    public void setControllingFaction(String faction) {
        controlling = faction;
    }
    public String getStatus() {
        final String a = attacking, c = controlling;
        return status.replace("{CAP%}", Double.toString(round(controlPercent, 4))).replace("{ATTACKING_FACTION}", a != null ? a : "N/A").replace("{CONTROLLING_FACTION}", c != null ? c : "N/A");
    }
    public long getControlledStarting() {
        return controlledStarting;
    }
    public double getControlPercent() {
        return controlPercent;
    }
    public OutpostStatus getOutpostStatus() {
        return statusType;
    }
    public void setOutpostStatus(OutpostStatus type) {
        statusType = type;
        status = getStatuses().get(type.name());
    }
}
