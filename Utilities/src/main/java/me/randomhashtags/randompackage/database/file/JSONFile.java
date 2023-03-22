package me.randomhashtags.randompackage.database.file;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JSONFile implements DatabaseFile {
    private final File file;
    private JSONObject json;
    private List<JSONObject> pending;

    public JSONFile(File file) {
        this.file = file;
        try {
            json = new JSONObject(new JSONTokener(new FileReader(file.getAbsolutePath())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object get(String path) {
        return json.get(path);
    }
    public String getString(String path) {
        final Object object = get(path);
        return object != null ? (String) object : null;
    }
    public boolean getBoolean(String path) {
        final Object object = get(path);
        return object != null && Boolean.parseBoolean((String) object);
    }
    public int getInt(String path) {
        final Object o = get(path);
        return o != null ? Integer.parseInt((String) o) : -999;
    }
    public double getDouble(String path) {
        final Object o = get(path);
        return o != null ? Double.parseDouble((String) o) : -999;
    }
    public long getLong(String path) {
        final Object object = get(path);
        return object != null ? Long.parseLong((String) object) : -999;
    }
    public List<String> getStringList(String path) {
        final List<String> array = new ArrayList<>();
        final Object object = get(path);
        if(object instanceof JSONArray) {
           final JSONArray inner_array = (JSONArray) object;
            for(Object inner_array_object : inner_array) {
                final String s = (String) inner_array_object;
                array.add(s);
            }
        }
        return array.isEmpty() ? null : array;
    }
    public BigDecimal getBigDecimal(String path) {
        final Object o = get(path);
        return o != null ? BigDecimal.valueOf(Double.parseDouble((String) o)) : BigDecimal.ZERO;
    }

    public void save() {
        final boolean isPending = pending != null;
        try (FileWriter file = new FileWriter(this.file.getAbsolutePath())) {
            if(isPending) {
                for(JSONObject o : pending) {
                    file.write(o.toString());
                }
                pending.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void set(String path, Object value) {
        if(pending == null) {
            pending = new ArrayList<>();
        }
        final JSONObject obj = new JSONObject();
        obj.put(path, value);
        pending.add(obj);
    }

    public void convertTo(Class<? extends DatabaseFile> file) {
        if(file.isInstance(YamlFile.class)) {
        } else if(file.isInstance(MySQLFile.class)) {
        }
    }
}
