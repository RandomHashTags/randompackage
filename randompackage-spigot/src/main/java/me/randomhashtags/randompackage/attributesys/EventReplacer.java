package me.randomhashtags.randompackage.attributesys;

import me.randomhashtags.randompackage.addon.util.Mathable;
import me.randomhashtags.randompackage.attribute.Combo;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import me.randomhashtags.randompackage.util.RPValues;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public interface EventReplacer extends Combo, Mathable, UVersionableSpigot, RPValues {
    default String replaceValue(HashMap<String, Entity> entities, String value, HashMap<String, String> valueReplacements) {
        String string = value;

        if(string != null) {
            if(valueReplacements != null) {
                for(String s : valueReplacements.keySet()) {
                    final String replacement = valueReplacements.get(s);
                    if(replacement != null) {
                        string = string.replace(s, replacement);
                    }
                }
            }

            for(String entityString : entities.keySet()) {
                final Entity entity = entities.get(entityString);
                final boolean isLiving = entity instanceof LivingEntity, isPlayer = isLiving && entity instanceof Player;
                final LivingEntity le = isLiving ? (LivingEntity) entity : null;
                final Player player = isPlayer ? (Player) entity : null;
                string = string.replace("get" + entityString + "MaxHP", isLiving ? Double.toString(le.getMaxHealth()) : "0");
                string = string.replace("get" + entityString + "HP", isLiving ? Double.toString(le.getHealth()) : "0");
                string = string.replace("get" + entityString + "Saturation", isPlayer ? Float.toString(player.getSaturation()) : "0");
                if(string.contains("get" + entityString + "EmptyFatBuckets")) {
                    string = string.replace("get" + entityString + "EmptyFatBuckets", Integer.toString(getFatBuckets(player).size()));
                }
                if(string.contains("get" + entityString + "Loc")) {
                    final Location l = entity.getLocation();
                    string = string.replace("get" + entityString + "LocX", Double.toString(l.getX()));
                    string = string.replace("get" + entityString + "LocY", Double.toString(l.getY()));
                    string = string.replace("get" + entityString + "LocZ", Double.toString(l.getZ()));
                }
                if(string.contains("get" + entityString + "Exp")) {
                    string = string.replace("get" + entityString + "Exp", isPlayer ? Integer.toString(getTotalExperience(player)) : "0");
                    string = string.replace("get" + entityString + "ExpLevel", isPlayer ? Integer.toString(player.getLevel()) : "0");
                }
                if(string.contains("getRandom(")) { // TODO: fix this brudda | temporary fix
                    final String randomValue = string.split("getRandom\\(")[1];
                    final String[] values = randomValue.split("\\)");
                    final int length = values.length;
                    final String randomString = values[length == 1 ? 0 : length-2];
                    final String[] bounds = randomString.split(":");
                    final String min = bounds[0], max = bounds[1];
                    final double minimum = evaluate(min), maximum = evaluate(max);
                    final float random = (float) (minimum+ThreadLocalRandom.current().nextDouble(minimum, maximum+1));
                    string = string.replace("getRandom(" + randomString + ")", Float.toString(random).substring(0, 3));
                }
                final boolean hasCombo = string.contains("Combo(");
                if(hasCombo || string.contains("Multiplier(")) {
                    final UUID u = entity.getUniqueId();
                    final String combo = string.split("\\(")[1].split("\\)")[0];
                    string = string.replace("get" + entityString + (hasCombo ? "Combo" : "Multiplier") + "(" + combo + ")", isPlayer ? Double.toString(getCombo(u, combo)) : "1");
                }
            }
        }
        return string;
    }
}
