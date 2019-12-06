package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.util.Mathable;
import me.randomhashtags.randompackage.attribute.Combo;
import me.randomhashtags.randompackage.universal.UVersionable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public interface EventReplacer extends Combo, Mathable, UVersionable {
    default String replaceValue(HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        String string = value;

        if(string != null) {
            if(valueReplacements != null) {
                for(String s : valueReplacements.keySet()) {
                    final String r = valueReplacements.get(s);
                    if(r != null) {
                        string = string.replace(s, r);
                    }
                }
            }

            for(String entity : entities.keySet()) {
                final Entity E = entities.get(entity);
                final boolean isLiving = E instanceof LivingEntity, isPlayer = isLiving && E instanceof Player;
                final LivingEntity le = isLiving ? (LivingEntity) E : null;
                final Player player = isPlayer ? (Player) E : null;
                string = string.replace("get" + entity + "MaxHP", isLiving ? Double.toString(le.getMaxHealth()) : "0");
                string = string.replace("get" + entity + "HP", isLiving ? Double.toString(le.getHealth()) : "0");
                string = string.replace("get" + entity + "Saturation", isPlayer ? Float.toString(player.getSaturation()) : "0");
                if(string.contains("loc")) {
                    final Location l = E.getLocation();
                    string = string.replace("get" + entity + "LocX", Double.toString(l.getX()));
                    string = string.replace("get" + entity + "LocY", Double.toString(l.getY()));
                    string = string.replace("get" + entity + "LocZ", Double.toString(l.getZ()));
                }
                if(string.contains("exp")) {
                    string = string.replace("get" + entity + "Exp", isPlayer ? Integer.toString(api.getTotalExperience(player)) : "0");
                    string = string.replace("get" + entity + "ExpLevel", isPlayer ? Integer.toString(player.getLevel()) : "0");
                }
                if(string.contains("getRandom(")) {
                    final String r = string.split("getRandom\\(")[1];
                    final String[] values = r.split("\\)");
                    final String randomString = values[values.length-2];
                    final String[] bounds = randomString.split(":");
                    final String min = bounds[0], max = bounds[1];
                    final double minimum = evaluate(min), maximum = evaluate(max);
                    final float random = (float) (minimum+ThreadLocalRandom.current().nextDouble(minimum, maximum+1));
                    string = string.replace("getRandom(" + randomString + ")", Float.toString(random).substring(0, 3));
                }
                final boolean hasCombo = string.contains("Combo(");
                if(hasCombo || string.contains("Multiplier(")) {
                    final UUID u = E.getUniqueId();
                    final String combo = string.split("\\(")[1].split("\\)")[0];
                    string = string.replace("get" + entity + (hasCombo ? "Combo" : "Multiplier") + "(" + combo + ")", isPlayer ? Double.toString(getCombo(u, combo)) : "1");
                }
            }
        }
        return string;
    }
}
