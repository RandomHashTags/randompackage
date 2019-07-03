package me.randomhashtags.randompackage.recode.utils;

import me.randomhashtags.randompackage.recode.api.addons.active.LivingConquestMob;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

public class ConquestMob {
    public static HashMap<String, ConquestMob> bosses;
    private static UVersion uv;

    public String path, type, name;
    public List<String> attributes, equipment, drops;

    public ConquestMob(String path, String type, String name, List<String> attributes, List<String> equipment, List<String> drops) {
        if(bosses == null) {
            bosses = new HashMap<>();
            uv = UVersion.getUVersion();
        }
        this.path = path;
        this.type = type;
        this.name = name;
        this.attributes = attributes;
        this.equipment = equipment;
        this.drops = drops;
        bosses.put(path, this);
    }

    public LivingConquestMob spawn(Location location) {
        final LivingEntity le = uv.getEntity(type, location, true);
        final LivingConquestMob l = new LivingConquestMob(le, this);
        return l;
    }
    public static void deleteAll() {
        bosses = null;
        uv = null;
    }
}
