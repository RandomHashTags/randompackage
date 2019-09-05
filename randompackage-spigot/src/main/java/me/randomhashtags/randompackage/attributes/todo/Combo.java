package me.randomhashtags.randompackage.attributes.todo;

import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;

import java.util.HashMap;
import java.util.UUID;

public abstract class Combo extends AbstractEventAttribute {
    protected static HashMap<UUID, HashMap<String, Integer>> combos = new HashMap<>();
}
