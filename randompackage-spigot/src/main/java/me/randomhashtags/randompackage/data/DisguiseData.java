package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.dev.Disguise;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

public interface DisguiseData {
    EntityType getDisguise();
    HashMap<Disguise, String> getSettings();
}
