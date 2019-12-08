package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.living.LivingConquestMob;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

public class ConquestMob implements UVersionable {
    public static HashMap<String, ConquestMob> bosses;

    private String path, type, name;
    private List<String> attributes, equipment, drops;

    public ConquestMob(String path, String type, String name, List<String> attributes, List<String> equipment, List<String> drops) {
        if(bosses == null) {
            bosses = new HashMap<>();
        }
        this.path = path;
        this.type = type;
        this.name = name;
        this.attributes = attributes;
        this.equipment = equipment;
        this.drops = drops;
        bosses.put(path, this);
    }

    public String getPath() { return path; }
    public String getType() { return type; }
    public String getName() { return name; }

    public List<String> getAttributes() { return attributes; }
    public List<String> getEquipment() { return equipment; }
    public List<String> getDrops() { return drops; }

    public LivingConquestMob spawn(Location location) {
        location.getChunk().load();
        final LivingEntity le = getEntity(type, location, true);
        final LivingConquestMob l = new LivingConquestMob(le, this);
        return l;
    }
    public static void deleteAll() {
        bosses = null;
    }
}
