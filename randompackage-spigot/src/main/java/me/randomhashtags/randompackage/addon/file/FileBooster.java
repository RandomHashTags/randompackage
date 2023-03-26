package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.Booster;
import me.randomhashtags.randompackage.addon.enums.BoosterRecipients;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class FileBooster extends RPAddonSpigot implements Booster {
    private ItemStack item;
    private BoosterRecipients recipients;

    public FileBooster(File f) {
        super(f);
        register(Feature.BOOSTER, this);
    }

    public @NotNull BoosterRecipients getRecipients() {
        if(recipients == null) {
            final String target = yml.getString("settings.recipients");
            recipients = target != null && !target.isEmpty() ? BoosterRecipients.valueOf(target.toUpperCase()) : BoosterRecipients.SELF;
        }
        return recipients;
    }
    public @NotNull ItemStack getItem() {
        if(item == null) item = createItemStack(yml, "item");
        return getClone(item);
    }

    public int getTimeLoreSlot() {
        return get("{TIME}");
    }
    public int getMultiplierLoreSlot() {
        return get("{MULTIPLIER}");
    }
    private int get(String string) {
        final List<String> l = getItem().getItemMeta().getLore();
        for(int i = 0; i < l.size(); i++) {
            if(l.get(i).contains(string)) {
                return i;
            }
        }
        return -1;
    }
    public @NotNull List<String> getActivateMsg() {
        return getStringList(yml, "messages.activate");
    }
    public @NotNull List<String> getExpireMsg() {
        return getStringList(yml, "messages.expire");
    }
    public @NotNull List<String> getNotifyMsg() {
        return getStringList(yml, "messages.notify");
    }
    public @NotNull List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
}
