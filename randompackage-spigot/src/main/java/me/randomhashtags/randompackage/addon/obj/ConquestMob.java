package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.living.LivingConquestMob;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

public final class ConquestMob implements UVersionable {
    public static HashMap<String, ConquestMob> BOSSES;

    private final String path, type, name;
    private final List<String> attributes, equipment, drops;

    public ConquestMob(String path, String type, String name, List<String> attributes, List<String> equipment, List<String> drops) {
        if(BOSSES == null) {
            BOSSES = new HashMap<>();
        }
        this.path = path;
        this.type = type;
        this.name = name;
        this.attributes = attributes;
        this.equipment = equipment;
        this.drops = drops;
        BOSSES.put(path, this);
    }

    public String getPath() {
        return path;
    }
    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public List<String> getAttributes() {
        return attributes;
    }
    public List<String> getEquipment() {
        return equipment;
    }
    public List<String> getDrops() {
        return drops;
    }

    public LivingConquestMob spawn(Location location) {
        location.getChunk().load();
        final LivingEntity le = getEntity(type, location, true);
        return new LivingConquestMob(le, this);
    }
    public static void deleteAll() {
        BOSSES = null;
    }
}
