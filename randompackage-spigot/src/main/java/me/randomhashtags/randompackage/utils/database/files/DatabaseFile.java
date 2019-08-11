package me.randomhashtags.randompackage.utils.database.files;

import java.math.BigDecimal;
import java.util.List;

public interface DatabaseFile {
    Object get(String path);
    default Object get(String path, Object def) {
        final Object o = get(path);
        return o != null ? o : def;
    }

    String getString(String path);
    default String getString(String path, String def) {
        final String s = getString(path);
        return s != null ? s : def;
    }

    boolean getBoolean(String path);
    default boolean getBoolean(String path, boolean def) {
        final Object o = get(path);
        return o != null ? (boolean) o : def;
    }

    int getInt(String path);
    default int getInt(String path, int def) {
        final Object o = get(path);
        return o != null ? (int) o : def;
    }

    double getDouble(String path);
    default double getDouble(String path, double def) {
        final Object o = get(path);
        return o != null ? (double) o : def;
    }

    long getLong(String path);
    default long getLong(String path, long def) {
        final Object o = get(path);
        return o != null ? (long) o : def;
    }

    List<String> getStringList(String path);
    default List<String> getStringList(String path, List<String> def) {
        final List<String> t = getStringList(path);
        return t != null ? def : null;
    }

    default BigDecimal getBigDecimal(String path, BigDecimal def) {
        final Object d = get(path, null);
        return d != null ? BigDecimal.valueOf((double) d) : def;
    }


    void save();
    void set(String path, Object value);
    default void set(String path, Object value, boolean save) {
        set(path, value);
        if(save) save();
    }
}
