package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.ArmorSet;
import me.randomhashtags.randompackage.addon.GlobalChallenge;
import me.randomhashtags.randompackage.addon.util.Identifiable;

import java.util.HashMap;
import java.util.LinkedHashMap;

public interface RPStorage {
    HashMap<Feature, LinkedHashMap<String, Identifiable>> FEATURES = new HashMap<>();

    default void register(Feature f, Identifiable obj) {
        if(!FEATURES.containsKey(f)) {
            FEATURES.put(f, new LinkedHashMap<>());
        }
        FEATURES.get(f).put(obj.getIdentifier(), obj);
    }

    default Identifiable get(Feature f, String identifier) {
        return FEATURES.containsKey(f) ? FEATURES.get(f).getOrDefault(identifier, null) : null;
    }

    default ArmorSet getArmorSet(String identifier) {
        final Identifiable o = get(Feature.ARMOR_SET, identifier);
        return o != null ? (ArmorSet) o : null;
    }
    default GlobalChallenge getGlobalChallenge(String identifier) {
        final Identifiable o = get(Feature.GLOBAL_CHALLENGE, identifier);
        return o != null ? (GlobalChallenge) o : null;
    }
}
