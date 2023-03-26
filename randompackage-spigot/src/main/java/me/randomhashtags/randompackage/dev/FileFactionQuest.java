package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.dev.FactionQuest;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import me.randomhashtags.randompackage.enums.Feature;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class FileFactionQuest extends RPAddonSpigot implements FactionQuest {
    private String name;
    private BigDecimal completion;
    public FileFactionQuest(File f) {
        super(f);
        register(Feature.FACTION_QUEST, this);
    }
    public @NotNull String getName() {
        if(name == null) {
            name = colorize(yml.getString("settings.name"));
        }
        return name;
    }
    public BigDecimal getCompletion() {
        if(completion == null) {
            completion = BigDecimal.valueOf(yml.getDouble("settings.completion"));
        }
        return completion;
    }

    public List<String> getLore() { return getStringList(yml, "lore"); }
    public List<String> getRewards() { return getStringList(yml, "rewards"); }
    public @NotNull List<String> getAttributes() { return getStringList(yml, "attributes"); }
}
