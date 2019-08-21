package me.randomhashtags.randompackage.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public abstract class Reflect extends RPFeature {
    protected String v = Bukkit.getVersion();
    protected boolean isLegacy = v.contains("1.8") || v.contains("1.9") || v.contains("1.10") || v.contains("1.11") || v.contains("1.12");
    protected Object getPrivateField(Object object, String field) throws Exception { return getPrivateField(object, field, field.equals("knownCommands") && !(isLegacy || v.contains("1.13"))); }
    protected Object getPrivateField(Object object, String field, boolean inSuper) throws Exception {
        /* Code from "zeeveener" at https://bukkit.org/threads/131808/ , edited by RandomHashTags */
        Class<?> clazz = object.getClass();
        Field objectField = inSuper ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field);
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    protected Field getPrivateField(Class clazz, String field) throws Exception { return getPrivateField(clazz, field, false); }
    protected Field getPrivateField(Class clazz, String field, boolean inSuper) throws Exception {
        Field objectField = inSuper ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field);
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        return objectField;
    }
}
