package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import org.bukkit.Location;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public abstract class AbstractBattlefield extends RPAddonSpigot implements BattlefieldEvent {

    private final Location warp_location;
    private final List<String> allowed_commands, started_message;

    public AbstractBattlefield(File file) {
        super(file);

        final JSONObject json = parse_json_from_file(file);
        final JSONObject settings_json = json.getJSONObject("settings");
        final String warp_location_string = parse_string_in_json(settings_json, "warp location");
        warp_location = string_to_location(warp_location_string);

        allowed_commands = parse_list_string_in_json(json, "allowed commands");
        final JSONObject messages_json = json.getJSONObject("messages");
        started_message = parse_list_string_in_json(messages_json, "started");
    }

    @Override
    public Location getWarpLocation() {
        return warp_location;
    }
    @Override
    public List<String> getAllowedCommands() {
        return allowed_commands;
    }

    @Override
    public List<String> getStartedMsg() {
        return started_message;
    }
}
