package me.randomhashtags.randompackage.attribute;

import java.util.HashMap;
import java.util.UUID;

public interface Combo {
    HashMap<UUID, HashMap<String, Double>> combos = new HashMap<>();
    default double getCombo(UUID uuid, String identifier) {
        return combos.containsKey(uuid) ? combos.get(uuid).getOrDefault(identifier, 1.00) : 1.00;
    }
}
