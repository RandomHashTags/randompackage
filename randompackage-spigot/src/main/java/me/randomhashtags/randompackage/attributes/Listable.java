package me.randomhashtags.randompackage.attributes;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface Listable {
    HashMap<UUID, List<String>> list = new HashMap<>();
}
