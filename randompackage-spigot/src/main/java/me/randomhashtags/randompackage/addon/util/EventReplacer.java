package me.randomhashtags.randompackage.addon.util;

import java.util.HashMap;

public interface EventReplacer {
    default String replaceValue(String value, HashMap<String, String> valueReplacements) {
        String string = value;
        if(valueReplacements != null) {
            for(String s : valueReplacements.keySet()) {
                string = string.replace(s, valueReplacements.get(s));
            }
        }
        return string;
    }
}
