package me.randomhashtags.randompackage.database.file;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JSONFile implements DatabaseFile {
    private final File file;
    private JSONObject json;
    private List<JSONAware> pending;

    public JSONFile(File file) {
        this.file = file;
        try {
            json = (JSONObject) new JSONParser().parse(new FileReader(file.getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object get(String path) { return json.get(path); }
    public String getString(String path) {
        final Object o = get(path);
        return o != null ? (String) o : null;
    }
    public boolean getBoolean(String path) {
        final Object o = get(path);
        return o != null && Boolean.parseBoolean((String) o);
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
        final Object o = get(path);
        return o != null ? Long.parseLong((String) o) : -999;
    }
    public List<String> getStringList(String path) {
        final List<String> a = new ArrayList<>();
        final Object o = get(path);
        if(o instanceof JSONArray) {
           final JSONArray array = (JSONArray) o;
            for(String s : (Iterable<String>) array) {
                a.add(s);
            }
        }
        return a.isEmpty() ? null : a;
    }
    public BigDecimal getBigDecimal(String path) {
        final Object o = get(path);
        return o != null ? BigDecimal.valueOf(Double.parseDouble((String) o)) : BigDecimal.ZERO;
    }

    public void save() {
        final boolean isPending = pending != null;
        try (FileWriter file = new FileWriter(this.file.getAbsolutePath())) {
            if(isPending) {
                for(JSONAware o : pending) {
                    file.write(o.toJSONString());
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
