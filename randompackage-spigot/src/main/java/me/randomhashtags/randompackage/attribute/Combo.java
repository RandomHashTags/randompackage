package me.randomhashtags.randompackage.attribute;

import java.util.HashMap;
import java.util.UUID;

public interface Combo {
    HashMap<UUID, HashMap<String, Double>> COMBOS = new HashMap<>();
    default double getCombo(UUID uuid, String identifier) {
        return COMBOS.containsKey(uuid) ? COMBOS.get(uuid).getOrDefault(identifier, 1.00) : 1.00;
    }
}
