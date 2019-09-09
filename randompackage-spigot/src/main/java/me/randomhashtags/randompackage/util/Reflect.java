package me.randomhashtags.randompackage.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public abstract class Reflect extends RPFeature {
    protected String v = Bukkit.getVersion();
    protected boolean isLegacy = v.contains("1.8") || v.contains("1.9") || v.contains("1.10") || v.contains("1.11") || v.contains("1.12");
    protected Object getPrivateField(Object object, String field) throws Exception { return getPrivateField(object, field, false); }
    protected Object getPrivateField(Object object, String field, boolean inSuper) throws Exception {
        final Class<?> clazz = object.getClass();
        final Field objectField = inSuper ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field);
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        objectField.setAccessible(true);
        final Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    protected Field getPrivateField(Class clazz, String field) throws Exception { return getPrivateField(clazz, field, false); }
    protected Field getPrivateField(Class clazz, String field, boolean inSuper) throws Exception {
        final Field objectField = inSuper ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field);
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        return objectField;
    }
}
