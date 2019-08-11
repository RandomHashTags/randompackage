package me.randomhashtags.randompackage.utils.database.files;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class YamlFile implements DatabaseFile {
    private File file;
    private YamlConfiguration yml;

    public YamlFile(File file) {
        this.file = file;
        this.yml = YamlConfiguration.loadConfiguration(file);
    }
    
    public Object get(String path) { return yml.get(path); }
    public String getString(String path) { return yml.getString(path); }
    public boolean getBoolean(String path) { return yml.getBoolean(path); }
    public int getInt(String path) { return yml.getInt(path); }
    public double getDouble(String path) { return yml.getDouble(path); }
    public long getLong(String path) { return yml.getLong(path); }
    public List<String> getStringList(String path) { return yml.getStringList(path); }

    public void save() {
        try {
            yml.save(file);
            file = new File(file.getAbsolutePath());
            yml = YamlConfiguration.loadConfiguration(file);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object value) { yml.set(path, value); }
}
