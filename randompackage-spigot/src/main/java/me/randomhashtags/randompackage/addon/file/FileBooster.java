package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Booster;
import me.randomhashtags.randompackage.addon.enums.BoosterRecipients;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

public final class FileBooster extends RPAddonSpigot implements Booster {
    private final ItemStack item;
    private final BoosterRecipients recipients;
    private final List<String> attributes, activate_message, expire_message, notify_message;

    public FileBooster(File f) {
        super(f);
        final JSONObject json = parse_json_from_file(f);
        item = create_item_stack(json, "item");
        attributes = parse_list_string_in_json(json, "attributes");

        final JSONObject settings_json = parse_json_in_json(json, "settings");
        recipients = parse_booster_recipients_in_json(settings_json, "recipients");

        final JSONObject messages_json = parse_json_in_json(json, "messages");
        activate_message = parse_list_string_in_json(messages_json, "activate");
        expire_message = parse_list_string_in_json(messages_json, "expire");
        notify_message = parse_list_string_in_json(messages_json, "notify");

        register(Feature.BOOSTER, this);
    }

    public @NotNull BoosterRecipients getRecipients() {
        return recipients;
    }
    public @NotNull ItemStack getItem() {
        return getClone(item);
    }

    public int getTimeLoreSlot() {
        return get("{TIME}");
    }
    public int getMultiplierLoreSlot() {
        return get("{MULTIPLIER}");
    }
    private int get(@NotNull String string) {
        final List<String> l = getItem().getItemMeta().getLore();
        for(int i = 0; i < l.size(); i++) {
            if(l.get(i).contains(string)) {
                return i;
            }
        }
        return -1;
    }
    public @NotNull List<String> getActivateMsg() {
        return activate_message;
    }
    public @NotNull List<String> getExpireMsg() {
        return expire_message;
    }
    public @NotNull List<String> getNotifyMsg() {
        return notify_message;
    }
    public @NotNull List<String> getAttributes() {
        return attributes;
    }
}
