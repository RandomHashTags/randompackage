package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.Stronghold;
import me.randomhashtags.randompackage.addon.enums.CaptureType;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.obj.PolyBoundary;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public abstract class FileStronghold extends RPAddonSpigot implements Stronghold {
    private final ItemStack is;
    private final int slot;

    private final boolean allows_afk_capturing;
    private final Location center;
    private final CaptureType captureType;
    private final double capture_radius;
    private PolyBoundary squareZone;
    private final List<String> message_no_longer_controlling, message_taken_control;
    private final List<String> rewards;
    public FileStronghold(File f) {
        super(f);

        final JSONObject json = parse_json_from_file(f);
        is = create_item_stack(json, "gui");

        final JSONObject gui_json = json.getJSONObject("gui");
        slot = parse_int_in_json(gui_json, "slot");

        final JSONObject settings_json = json.getJSONObject("settings");
        allows_afk_capturing = parse_boolean_in_json(settings_json, "allows afk capturing");

        center = string_to_location(parse_string_in_json(settings_json, "center"));
        captureType = CaptureType.valueOf(parse_string_in_json(settings_json, "capture type").toUpperCase());
        capture_radius = parse_double_in_json(settings_json, "capture radius");

        final JSONObject messages_json = json.getJSONObject("messages");
        message_no_longer_controlling = parse_list_string_in_json(messages_json, "no longer controlling");
        message_taken_control = parse_list_string_in_json(messages_json, "taken control");

        rewards = parse_list_string_in_json(json, "rewards");

        register(Feature.STRONGHOLD, this);
    }

    @NotNull
    @Override
    public ItemStack getItem() {
        return getClone(is);
    }

    @Override
    public int getSlot() {
        return slot;
    }

    public long getStartTime() {
        return 0;
    }
    @Override
    public boolean allowsAFKCapturing() {
        return allows_afk_capturing;
    }
    @Override
    public Location getCenter() {
        return center;
    }
    @Override
    public CaptureType getCaptureType() {
        return captureType;
    }
    @Override
    public double getCaptureRadius() {
        return capture_radius;
    }
    public PolyBoundary getSquareCaptureZone() {
        if(squareZone == null && getCaptureType().equals(CaptureType.SQUARE)) {
            squareZone = new PolyBoundary(getCenter(), (int) getCaptureRadius());
        }
        return squareZone;
    }
    public long getCaptureTime() {
        return 0;
    }
    public Player getCapturer() {
        return null;
    }
    public String getStatus() {
        return null;
    }
    public HashMap<String, String> getStatuses() {
        return null;
    }
    public double getControllingPercent() {
        return 0;
    }

    public List<String> getCapturedMsg() {
        return null;
    }
    public List<String> getNoLongerControllingMsg() {
        return message_no_longer_controlling;
    }
    public List<String> getTakenControlMsg() {
        return message_taken_control;
    }

    public @NotNull List<String> getRewards() {
        return rewards;
    }
}
