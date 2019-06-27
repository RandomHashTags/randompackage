package me.randomhashtags.randompackage.utils.classes.playerquests;

import me.randomhashtags.randompackage.utils.abstraction.AbstractPlayerQuest;

import java.io.File;
import java.util.TreeMap;

public class PlayerQuest extends AbstractPlayerQuest {
    public static TreeMap<String, PlayerQuest> enabled, disabled;
    public PlayerQuest(File f) {
        if(enabled == null) {
            enabled = new TreeMap<>();
            disabled = new TreeMap<>();
        }
        load(f);
        (isEnabled() ? enabled : disabled).put(getYamlName(), this);
    }
    public static void deleteAll() {
        enabled = null;
        disabled = null;
    }
}
