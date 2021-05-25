package me.randomhashtags.randompackage.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public interface Reflect extends RPFeature {
    default Object getPrivateField(Object object, String field) throws Exception {
        return getPrivateField(object, field, false);
    }
    default Object getPrivateField(Object object, String field, boolean inSuper) throws Exception {
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

    default Field getPrivateField(Class clazz, String field) throws Exception {
        return getPrivateField(clazz, field, false);
    }
    default Field getPrivateField(Class clazz, String field, boolean inSuper) throws Exception {
        final Field objectField = inSuper ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field);
        if(objectField == null) {
            Bukkit.broadcastMessage("objectField == null!");
            return null;
        }
        return objectField;
    }
}
